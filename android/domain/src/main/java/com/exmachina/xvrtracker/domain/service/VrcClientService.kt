package com.exmachina.xvrtracker.domain.service

import com.exmachina.xvrtracker.common.net.SimpleOscClient
import com.exmachina.xvrtracker.common.util.assertNotMainThread
import com.exmachina.xvrtracker.data.repository.VrcConnectionSettingsRepository
import com.exmachina.xvrtracker.domain.type.PoseTrackerType
import com.exmachina.xvrtracker.domain.type.PoseTrackersData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class VrcClientService @Inject constructor(
    private val vrcConnectionSettingsRepository: VrcConnectionSettingsRepository,
    private val simpleOscClient: SimpleOscClient,
) {

    companion object {

        const val BASE_PATH = "/tracking/trackers"
        const val POSITION_PATH = "position"
        const val ROTATION_PATH = "rotation"
    }

    private val _isSetupFlow = MutableStateFlow(false)
    val isSetupFlow = _isSetupFlow.asStateFlow()

    private val vrcClientSettingsFlow = vrcConnectionSettingsRepository.dataFlow

    private val mutex = Mutex()

    suspend fun setup() {
        assertNotMainThread()
        mutex.withLock {
            if (_isSetupFlow.value) {
                internalTearDown()
            }

            val settings = vrcClientSettingsFlow.first()

            simpleOscClient.setup(
                hostname = settings.ipOrDomain,
                port = settings.port.value,
            )

            _isSetupFlow.value = true
        }
    }

    suspend fun termDown() {
        assertNotMainThread()
        mutex.withLock {
            internalTearDown()
            _isSetupFlow.value = false
        }
    }

    private suspend fun internalTearDown() {
        simpleOscClient.teardown()
    }

    suspend fun sendPoseTrackerData(data: PoseTrackersData) {
        assertNotMainThread()
        mutex.withLock {
            if (!isSetupFlow.value)
                return

            val modificationMatrix = data.modificationMatrix
            data.trackers.firstOrNull()?.let { trackers ->
                trackers.forEach { (type, data) ->
                    val path = "$BASE_PATH/" + when (type) {
                        PoseTrackerType.Head -> "head"
                        PoseTrackerType.LeftFeet -> "1"
                        PoseTrackerType.RightFeet -> "2"
                        PoseTrackerType.Waist -> "3"
                        PoseTrackerType.Chest -> "4"
                        PoseTrackerType.LeftKnee -> "5"
                        PoseTrackerType.RightKnee -> "6"
                        PoseTrackerType.LeftElbow -> "7"
                        PoseTrackerType.RightElbow -> "8"
                    }
                    val attitude = modificationMatrix * data.attitude

                    if (data.dataType.isPositionEnabled) {
                        simpleOscClient.send(
                            path = "$path/$POSITION_PATH",
                            params = attitude.columnVector3(3).toList(),
                        )
                    }

                    if (data.dataType.isRotationEnabled) {
                        val angle = -attitude.transpose().toEulerAngleZXY(isDegree = true)
                        simpleOscClient.send(
                            path = "$path/$ROTATION_PATH",
                            params = listOf(angle.beta, angle.gamma, angle.alpha),
                        )
                    }
                }
            }
        }
    }
}
