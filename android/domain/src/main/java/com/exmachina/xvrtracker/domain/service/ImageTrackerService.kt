package com.exmachina.xvrtracker.domain.service

import android.graphics.Bitmap
import android.os.SystemClock
import com.exmachina.xvrtracker.common.math.sensorToUnity
import com.exmachina.xvrtracker.common.util.withTryLock
import com.exmachina.xvrtracker.data.repository.PoseLandmarkerRepository
import com.exmachina.xvrtracker.data.repository.PoseLandmarkerSettingsRepository
import com.exmachina.xvrtracker.data.source.AccelerationDataSource
import com.exmachina.xvrtracker.data.type.PoseLandmarkerComputeType
import com.exmachina.xvrtracker.data.type.PoseLandmarksData
import com.exmachina.xvrtracker.domain.logic.PoseLandmarkModifier
import com.exmachina.xvrtracker.domain.logic.PoseTrackerCalculator
import com.exmachina.xvrtracker.domain.type.PoseTrackersData
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import javax.inject.Inject

@ViewModelScoped
class ImageTrackerService @Inject constructor(
    private val poseLandmarkerSettingsRepository: PoseLandmarkerSettingsRepository,
    private val accelerationDataSource: AccelerationDataSource,
    private val poseLandmarkerRepository: PoseLandmarkerRepository,
    private val poseLandmarkModifier: PoseLandmarkModifier,
    private val poseTrackerCalculator: PoseTrackerCalculator,
    private val vrcClientService: VrcClientService,
) {

    private val _rawPoseLandmarksDataFlow = MutableSharedFlow<PoseLandmarksData>(extraBufferCapacity = 1)
    val rawPoseLandmarksDataFlow = _rawPoseLandmarksDataFlow.asSharedFlow()

    private val _poseLandmarksDataFlow = MutableSharedFlow<PoseLandmarksData>(extraBufferCapacity = 1)
    val poseLandmarksDataFlow = _poseLandmarksDataFlow.asSharedFlow()

    private val _poseTrackersDataFlow = MutableSharedFlow<PoseTrackersData>(extraBufferCapacity = 1)
    val poseTrackersDataFlow = _poseTrackersDataFlow.asSharedFlow()

    private var accelerationDataJob: Job? = null
    private val modelScope = CoroutineScope(Dispatchers.IO)
    private val mutex = Mutex()

    init {
        var latestTimestamp: Long = 0
        poseLandmarkerRepository.dataFlow
            .onEach { data ->
                val delta = data.timestamp - latestTimestamp
                latestTimestamp = data.timestamp

                _rawPoseLandmarksDataFlow.tryEmit(data)
                val modifiedData = poseLandmarkModifier.modify(data)
                _poseLandmarksDataFlow.tryEmit(modifiedData)
                poseTrackerCalculator.updateLandmarksData(modifiedData)

            }
            .launchIn(modelScope)

        poseLandmarkerRepository.errorFlow
            .onEach { error ->
                if (error is RuntimeException) {
                    Timber.i(error, "Pose analyser threw an error!")
                    poseLandmarkerSettingsRepository.computeType(PoseLandmarkerComputeType.CPU)
                    mutex.withLock {
                        resumePoseLandmarker()
                    }
                }
            }.launchIn(modelScope)

        poseTrackerCalculator.poseTrackersDataFlow
            .onEach {
                _poseTrackersDataFlow.tryEmit(it)
                vrcClientService.sendPoseTrackerData(it)
            }
            .launchIn(modelScope)
    }

    fun dispose() {
        modelScope.launch {
            mutex.withLock {
                poseLandmarkerRepository.teardown()
                modelScope.cancel()
            }
        }
    }

    fun resume() {
        modelScope.launch {
            mutex.withLock {
                resumePoseLandmarker()
                poseLandmarkModifier.reset()
                poseTrackerCalculator.reset()
                accelerationDataJob = accelerationDataSource.accelerationFlow
                    .onEach {
                        val unityAcceleration = it.sensorToUnity()
                        poseLandmarkModifier.updateUpperDirection(unityAcceleration)
                        poseTrackerCalculator.updateUpperDirection(unityAcceleration)
                    }.launchIn(modelScope)
            }
        }
    }

    private suspend fun resumePoseLandmarker() {
        poseLandmarkerRepository.setup(
            modelType = poseLandmarkerSettingsRepository.modelType.first(),
            calculationType = poseLandmarkerSettingsRepository.computeType.first(),
        )
    }

    fun suspend() {
        modelScope.launch {
            mutex.withLock {
                poseLandmarkerRepository.teardown()
                accelerationDataJob?.cancel()
                vrcClientService.termDown()
            }
        }
    }

    fun startNetwork() {
        modelScope.launch {
            mutex.withLock {
                vrcClientService.setup()
            }
        }
    }

    fun updateAnalysisImage(
        bitmap: Bitmap,
        rotation: Float,
    ) {
        modelScope.launch {
            mutex.withTryLock {
                poseLandmarkerRepository.detect(
                    bitmap = bitmap,
                    rotation = rotation,
                    timestamp = SystemClock.elapsedRealtime(),
                )
            }
        }
    }
}
