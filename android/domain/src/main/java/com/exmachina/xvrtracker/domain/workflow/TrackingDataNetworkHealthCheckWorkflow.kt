package com.exmachina.xvrtracker.domain.workflow

import com.exmachina.xvrtracker.common.type.LocalNetworkRoute
import com.exmachina.xvrtracker.common.util.isSameNetwork
import com.exmachina.xvrtracker.common.util.resolveIpAddress
import com.exmachina.xvrtracker.data.repository.VrcConnectionSettingsRepository
import com.exmachina.xvrtracker.domain.usecase.GetPrivateNetworkRoutesUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress
import javax.inject.Inject
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

class TrackingDataNetworkHealthCheckWorkflow @Inject constructor(
    private val vrcConnectionSettingsRepository: VrcConnectionSettingsRepository,
    private val getPrivateNetworkRoutesUseCase: GetPrivateNetworkRoutesUseCase,
) {

    enum class SuspendState {
        PrivateNetworkNotConnected,
        DestinationAddressResolvingFailed,
        GlobalAddressSpecified,
        UnknownNetworkState,
    }

    enum class ResumeState {
        Confirm,
        Abort,
        Retry,
    }

    private enum class NetworkErrorState {
        UnknownNetworkError,
    }

    private val connectionTestTimeoutMs = 5_000L

    private var isRunning: Boolean = false
    private var isSuspended: Boolean = false

    private var continuation: Continuation<ResumeState>? = null
    private val mutex = Mutex()

    suspend fun isRunning(): Boolean = mutex.withLock { isRunning }

    suspend fun isSuspended(): Boolean = mutex.withLock { isSuspended }

    suspend fun execute(
        onSuspend: suspend (SuspendState) -> Unit,
        onComplete: (suspend () -> Unit)? = null,
        onAbort: (suspend () -> Unit)? = null,
    ) {
        mutex.withLock {
            if (isRunning) {
                assert(true, { "execute() called while already running" })
                return
            }

            try {
                isRunning = true

                // check NIC

                Timber.d("check NIC")
                var networkRoutes: List<LocalNetworkRoute>
                while (true) {
                    val routes = getPrivateNetworkRoutesUseCase()
                    if (routes.isEmpty()) {
                        when (yield(SuspendState.PrivateNetworkNotConnected, onSuspend)) {
                            ResumeState.Confirm ->
                                Unit

                            ResumeState.Abort -> {
                                onAbort?.invoke()
                                return
                            }

                            ResumeState.Retry ->
                                continue
                        }
                    }
                    networkRoutes = routes
                    break
                }

                // resolve destination address

                val vrcConnectionSettings = vrcConnectionSettingsRepository.dataFlow.first()
                var destinationAddress: InetAddress
                while (true) {
                    val ipAddress = resolveIpAddress(vrcConnectionSettings.ipOrDomain)
                    if (ipAddress == null) {
                        when (yield(SuspendState.DestinationAddressResolvingFailed, onSuspend)) {
                            ResumeState.Confirm -> {
                                onComplete?.invoke()
                                return
                            }

                            ResumeState.Abort -> {
                                onAbort?.invoke()
                                return
                            }

                            ResumeState.Retry ->
                                continue
                        }
                    }
                    destinationAddress = ipAddress
                    break
                }

                // check destination routing

                while (true) {
                    val destinationRoute = networkRoutes.firstOrNull { route ->
                        isSameNetwork(destinationAddress, route.address, route.prefix)
                    }
                    if (destinationRoute == null) {
                        when (yield(SuspendState.GlobalAddressSpecified, onSuspend)) {
                            ResumeState.Confirm ->
                                Unit

                            ResumeState.Abort -> {
                                onAbort?.invoke()
                                return
                            }

                            ResumeState.Retry ->
                                continue
                        }
                    }
                    break
                }

                // test UDP connection

                while (true) {
                    val networkErrorState = withTimeoutOrNull(connectionTestTimeoutMs) {
                        try {
                            DatagramSocket().use { socket ->
                                socket.connect(
                                    InetSocketAddress(
                                        destinationAddress,
                                        vrcConnectionSettings.port.value,
                                    ),
                                )
                            }
                            null
                        } catch (_: Exception) {
                            NetworkErrorState.UnknownNetworkError
                        }
                    }

                    when (networkErrorState) {
                        NetworkErrorState.UnknownNetworkError -> {
                            when (yield(SuspendState.UnknownNetworkState, onSuspend)) {
                                ResumeState.Confirm ->
                                    break

                                ResumeState.Abort -> {
                                    onAbort?.invoke()
                                    return
                                }

                                ResumeState.Retry ->
                                    continue
                            }
                        }

                        else ->
                            break
                    }
                }

                // network health checking is completed

                onComplete?.invoke()
            } finally {
                isRunning = false
            }
        }
    }

    private suspend fun yield(
        suspendState: SuspendState,
        onSuspend: suspend (SuspendState) -> Unit,
    ): ResumeState {
        isSuspended = true
        onSuspend(suspendState)
        return try {
            suspendCancellableCoroutine { cont ->
                continuation = cont
                mutex.unlock()
            }
        } catch (_: CancellationException) {
            ResumeState.Abort
        } finally {
            mutex.lock()
            isSuspended = false
            continuation = null
        }
    }

    suspend fun resume(resumeState: ResumeState) {
        Timber.d("resume")
        mutex.withLock {
            if (!isSuspended) return
            continuation?.resume(resumeState)
            Timber.d("resumed")
        }
    }
}
