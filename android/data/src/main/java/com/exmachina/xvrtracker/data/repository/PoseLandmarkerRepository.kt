package com.exmachina.xvrtracker.data.repository

import android.graphics.Bitmap
import com.exmachina.xvrtracker.data.source.PoseLandmarkerDataSource
import com.exmachina.xvrtracker.data.type.PoseLandmarkerComputeType
import com.exmachina.xvrtracker.data.type.PoseLandmarkerModelType
import com.google.mediapipe.tasks.vision.core.RunningMode
import javax.inject.Inject

class PoseLandmarkerRepository @Inject constructor(
    private val dataSource: PoseLandmarkerDataSource,
) {

    companion object {

        const val DEFAULT_NUM_POSES = 1
        const val DEFAULT_POSE_PRESENCE_CONFIDENCE = 0.5f
        const val DEFAULT_POSE_DETECTION_CONFIDENCE = 0.5f
        const val DEFAULT_POSE_TRACKING_CONFIDENCE = 0.5f
        val DEFAULT_RUNNING_MODE = RunningMode.LIVE_STREAM
        const val DEFAULT_OUTPUT_SEGMENTATION_MASKS = false
    }

    val isSetup = dataSource.isSetupFlow
    val dataFlow = dataSource.dataFlow
    val errorFlow = dataSource.errorFlow

    suspend fun setup(
        modelType: PoseLandmarkerModelType,
        calculationType: PoseLandmarkerComputeType,
        numPoses: Int = DEFAULT_NUM_POSES,
        minPosePresenceConfidence: Float = DEFAULT_POSE_PRESENCE_CONFIDENCE,
        minPoseDetectionConfidence: Float = DEFAULT_POSE_DETECTION_CONFIDENCE,
        minPoseTrackingConfidence: Float = DEFAULT_POSE_TRACKING_CONFIDENCE,
        outputSegmentationMasks: Boolean = DEFAULT_OUTPUT_SEGMENTATION_MASKS,
        runningMode: RunningMode = DEFAULT_RUNNING_MODE,
    ) {
        dataSource.setup(
            modelType = modelType,
            calculationType = calculationType,
            numPoses = numPoses,
            minPosePresenceConfidence = minPosePresenceConfidence,
            minPoseDetectionConfidence = minPoseDetectionConfidence,
            minPoseTrackingConfidence = minPoseTrackingConfidence,
            outputSegmentationMasks = outputSegmentationMasks,
            runningMode = runningMode,
        )
    }

    suspend fun teardown() {
        dataSource.teardown()
    }

    fun detect(
        bitmap: Bitmap,
        rotation: Float,
        timestamp: Long,
    ) {
        dataSource.detect(
            bitmap = bitmap,
            rotation = rotation,
            timestamp = timestamp,
        )
    }
}
