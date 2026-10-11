package com.exmachina.xvrtracker.domain.service

import android.content.Context
import com.exmachina.xvrtracker.common.storage.getBytes
import com.exmachina.xvrtracker.data.entity.AnalyzedDataPlotServerSettings
import com.exmachina.xvrtracker.data.repository.AnalyzedDataPlotServerSettingsRepository
import com.exmachina.xvrtracker.data.type.PoseLandmarksData
import com.exmachina.xvrtracker.domain.type.PoseTrackersData
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.defaultForFilePath
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.DefaultWebSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.encodeToJsonElement
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

class PlotAnalyzedDataServerService @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val analyzedDataPlotServerSettingsRepository: AnalyzedDataPlotServerSettingsRepository,
) {

    companion object {

        const val ASSET_PATH = "plot_analyzed_data"
        const val CHANNEL_POSE_LANDMARKS_DATA = "pose_landmarks_data"
        const val CHANNEL_POSE_TRACKERS_DATA = "pose_trackers_data"
    }

    private val _isStartFlow = MutableStateFlow(false)
    val isStartFlow = _isStartFlow.asStateFlow()

    private val poseLandmarksDataFlow = MutableSharedFlow<PoseLandmarksData>(extraBufferCapacity = 1)
    private val poseTrackersDataFlow = MutableSharedFlow<PoseTrackersData>(extraBufferCapacity = 1)

    private var server: EmbeddedServer<*, *>? = null
    private val webSocketConnections = mutableListOf<DefaultWebSocketSession>()
    private val mutex = Mutex()

    suspend fun start() {
        mutex.withLock {
            if (!isStartFlow.value) {
                internalStop()
            }

            val settings = analyzedDataPlotServerSettingsRepository.dataFlow.first()

            val server =
                embeddedServer(
                    factory = Netty,
                    port = settings.port.value,
                    module = { module(settings = settings) },
                )
            server.startSuspend()
            this.server = server

            _isStartFlow.value = true
        }
    }

    @OptIn(FlowPreview::class)
    private fun Application.module(settings: AnalyzedDataPlotServerSettings) {
        install(WebSockets) {
            pingPeriod = settings.ping.value.seconds
            timeout = settings.timeout.value.seconds
        }

        routing {
            get("/{filename}") {
                val filename = call.parameters["filename"] ?: "index.html"
                try {
                    call.respondBytes(
                        context.assets.getBytes("$ASSET_PATH/$filename"),
                        ContentType.defaultForFilePath(filename),
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    call.respond(HttpStatusCode.NotFound, "File not found")
                }
            }

            get("/") {
                val filename = "index.html"
                try {
                    call.respondBytes(
                        context.assets.getBytes("$ASSET_PATH/$filename"),
                        ContentType.defaultForFilePath(filename),
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    call.respond(HttpStatusCode.NotFound, "File not found")
                }
            }

            webSocket("/data") {
                synchronized(webSocketConnections) {
                    webSocketConnections.add(this)
                }

                try {
                    launch {
                        poseLandmarksDataFlow.debounce(10).collect { data ->
                            if (isActive) {
                                send(
                                    Frame.Text(
                                        Json.encodeToString(
                                            data.encodeChannelledJson(CHANNEL_POSE_LANDMARKS_DATA),
                                        ),
                                    ),
                                )
                            }
                        }
                    }

                    launch {
                        poseTrackersDataFlow
                            .debounce(10)
                            .collect { data ->
                                if (isActive) {
                                    send(
                                        Frame.Text(
                                            Json.encodeToString(
                                                data.encodeChannelledJson(CHANNEL_POSE_TRACKERS_DATA),
                                            ),
                                        ),
                                    )
                                }
                            }
                    }

                    incoming.consumeEach {
                        if (it is Frame.Text) {
                            Timber.d("Reserve WebSocket message: ${it.readText()}")
                        }
                    }
                } finally {
                    synchronized(webSocketConnections) {
                        webSocketConnections.remove(this)
                    }
                }
            }
        }
    }

    private inline fun <reified T> T.encodeChannelledJson(channel: String) =
        JsonObject(
            mapOf(
                "channel" to JsonPrimitive(channel),
                "data" to Json.encodeToJsonElement(this),
            ),
        )

    suspend fun stop() {
        mutex.withLock {
            internalStop()
            _isStartFlow.value = false
        }
    }

    private suspend fun internalStop() {
        try {
            server?.stopSuspend(gracePeriodMillis = 0, timeoutMillis = 0)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            server = null
        }
    }

    fun updatePoseLandmarksData(poseLandmarksData: PoseLandmarksData) {
        if (isStartFlow.value) {
            poseLandmarksDataFlow.tryEmit(poseLandmarksData)
        }
    }

    fun updatePoseTrackersData(poseTrackersData: PoseTrackersData) {
        if (isStartFlow.value) {
            poseTrackersDataFlow.tryEmit(poseTrackersData)
        }
    }
}
