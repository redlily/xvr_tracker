package com.exmachina.xvrtracker.data.source

import android.content.Context
import android.graphics.Bitmap
import com.exmachina.xvrtracker.common.math.Matrix4x4
import com.exmachina.xvrtracker.common.math.Vector3
import com.exmachina.xvrtracker.common.util.assertNotMainThread
import com.exmachina.xvrtracker.common.util.withTryLock
import com.exmachina.xvrtracker.data.type.PoseLandmarkerComputeType
import com.exmachina.xvrtracker.data.type.PoseLandmarkerModelType
import com.exmachina.xvrtracker.data.type.PoseLandmarksData
import com.exmachina.xvrtracker.data.type.Size2D
import com.exmachina.xvrtracker.data.util.toPoseLandmarksDataList
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class PoseLandmarkerDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {

    private val _isSetupFlow = MutableStateFlow(false)
    val isSetupFlow = _isSetupFlow.asStateFlow()

    private val _dataFlow = MutableSharedFlow<PoseLandmarksData>(extraBufferCapacity = 1)
    val dataFlow = _dataFlow.asSharedFlow()

    private val _errorFlow = MutableSharedFlow<Exception>()
    val errorFlow = _errorFlow.asSharedFlow()

    private var poseLandmarker: PoseLandmarker? = null
    private var rotation: Float = 0f
    private var lastTimestamp: Long = 0

    private val modelScope = CoroutineScope(Dispatchers.IO)
    private val mutex = Mutex()

    suspend fun setup(
        modelType: PoseLandmarkerModelType,
        calculationType: PoseLandmarkerComputeType,
        numPoses: Int,
        minPosePresenceConfidence: Float,
        minPoseDetectionConfidence: Float,
        minPoseTrackingConfidence: Float,
        outputSegmentationMasks: Boolean,
        runningMode: RunningMode,
    ) {
        assertNotMainThread()
        mutex.withLock {
            if (_isSetupFlow.value) {
                internalTeardown()
            }

            try {
                val baseOptions = BaseOptions
                    .builder()
                    .setDelegate(calculationType.delegate)
                    .setModelAssetPath(modelType.modelFilePath)
                    .build()
                val options = PoseLandmarker.PoseLandmarkerOptions
                    .builder()
                    .setBaseOptions(baseOptions)
                    .setNumPoses(numPoses)
                    .setMinPoseDetectionConfidence(minPosePresenceConfidence)
                    .setMinPosePresenceConfidence(minPoseDetectionConfidence)
                    .setMinTrackingConfidence(minPoseTrackingConfidence)
                    .setRunningMode(runningMode)
                    .setOutputSegmentationMasks(outputSegmentationMasks)
                    .apply {
                        if (runningMode == RunningMode.LIVE_STREAM) {
                            setResultListener(::emitAnalyzedResult)
                            setErrorListener(::emitAnalyzingError)
                        }
                    }.build()
                poseLandmarker = PoseLandmarker.createFromOptions(context, options)
                lastTimestamp = 0
                _isSetupFlow.value = true
            } catch (e: Exception) {
                emitAnalyzingError(e)
            }
        }
    }

    suspend fun teardown() {
        assertNotMainThread()
        mutex.withLock {
            internalTeardown()
            _isSetupFlow.value = false
        }
    }

    private fun internalTeardown() {
        poseLandmarker?.close()
        poseLandmarker = null
    }

    fun detect(
        bitmap: Bitmap,
        rotation: Float,
        timestamp: Long,
    ) {
        assertNotMainThread()
        mutex.withTryLock {
            poseLandmarker?.let {
                if (timestamp > lastTimestamp) {
                    this.rotation = rotation
                    lastTimestamp = timestamp
                    val mpImage = BitmapImageBuilder(bitmap).build()
                    try {
                        it.detectAsync(mpImage, timestamp)
                    } catch (e: Exception) {
                        emitAnalyzingError(e)
                    }
                }
            }
        }
    }

    private fun emitAnalyzedResult(
        result: PoseLandmarkerResult,
        image: MPImage,
    ) {
        val rotation = Matrix4x4.rotation(axis = Vector3.Z, angle = rotation)
        val o = rotation * Vector3.ZERO
        val x = rotation * Vector3(image.width.toFloat(), 0f, 0f)
        val y = rotation * Vector3(0f, image.height.toFloat(), 0f)
        val xz = rotation * Vector3(image.width.toFloat(), image.height.toFloat(), 0f)
        val minX = minOf(o.x, x.x, y.x, xz.x)
        val maxX = maxOf(o.x, x.x, y.x, xz.x)
        val minY = minOf(o.y, x.y, y.y, xz.y)
        val maxY = maxOf(o.y, x.y, y.y, xz.y)
        val width = maxX - minX
        val height = maxY - minY
        val translation = Vector3(-minX, -minY, 0f)
        val transform = Matrix4x4.translation(translation) * rotation
        _dataFlow.tryEmit(
            PoseLandmarksData(
                imageSize = Size2D(width = width, height = height),
                landmarks = result.toPoseLandmarksDataList(transform),
                timestamp = result.timestampMs(),
            ),
        )
    }

    private fun emitAnalyzingError(error: Exception) {
        modelScope.launch {
            _errorFlow.emit(error)
        }
    }
}
