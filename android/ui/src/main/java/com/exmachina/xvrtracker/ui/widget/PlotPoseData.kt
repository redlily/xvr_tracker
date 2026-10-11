package com.exmachina.xvrtracker.ui.widget

import android.graphics.Matrix
import android.view.Surface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.exmachina.xvrtracker.common.math.Matrix4x4
import com.exmachina.xvrtracker.common.math.Vector3
import com.exmachina.xvrtracker.common.math.unityToScreen
import com.exmachina.xvrtracker.data.type.HorizontalPosition
import com.exmachina.xvrtracker.data.type.PoseLandmarkData
import com.exmachina.xvrtracker.data.type.PoseLandmarkType
import com.exmachina.xvrtracker.data.type.PoseLandmarksData
import com.exmachina.xvrtracker.domain.type.PoseTrackerData
import com.exmachina.xvrtracker.domain.type.PoseTrackerType
import com.exmachina.xvrtracker.domain.type.PoseTrackersData
import kotlin.math.atan2
import kotlin.math.max

@Composable
fun PlotPoseData(
    rotation: Int,
    flip: Boolean,
    poseLandmarksData: PoseLandmarksData,
    poseTrackersData: PoseTrackersData,
    isDrawLandmarksDataEnabled: Boolean,
    isDrawTrackersDataEnabled: Boolean,
    visibilityThreshold: Float,
    presenceThreshold: Float,
) {
    Canvas(
        modifier = Modifier.fillMaxSize(),
    ) {
        val transform = landmarkToScreenTransform(
            canvasSize = size,
            imageSize = android.util.SizeF(
                poseLandmarksData.imageSize.width,
                poseLandmarksData.imageSize.height,
            ),
            rotation = rotation,
            flip = flip,
        )
        val filteredLandmarks = poseLandmarksData.landmarks.map { landmarks ->
            landmarks.filter { (_, landmark) ->
                landmark.visibility > visibilityThreshold &&
                    landmark.presence > presenceThreshold
            }
        }

        if (isDrawLandmarksDataEnabled) {
            filteredLandmarks.forEach { landmarks ->
                drawBones(landmarks = landmarks, transform = transform)
                drawJoints(landmarks = landmarks, transform = transform, isOutline = true)
                drawJoints(landmarks = landmarks, transform = transform, isOutline = false)
            }
        }
        if (isDrawTrackersDataEnabled) {
            filteredLandmarks.zip(poseTrackersData.trackers).forEach { (landmarks, trackers) ->
                drawTrackerAttitudes(landmarks = landmarks, trackers = trackers, transform = transform)
            }
        }
    }
}

private fun DrawScope.landmarkToScreenTransform(
    canvasSize: androidx.compose.ui.geometry.Size,
    imageSize: android.util.SizeF,
    rotation: Int,
    flip: Boolean,
): Matrix {
    val rotatedSize = when (rotation) {
        Surface.ROTATION_90, Surface.ROTATION_270 -> android.util.SizeF(
            imageSize.height,
            imageSize.width,
        )

        else -> imageSize
    }
    val scaleFactor = max(
        canvasSize.width / rotatedSize.width,
        canvasSize.height / rotatedSize.height,
    )
    val transform = Matrix().apply {
        preTranslate(
            -(rotatedSize.width * scaleFactor - canvasSize.width) / 2,
            -(rotatedSize.height * scaleFactor - canvasSize.height) / 2,
        )
        preScale(
            rotatedSize.width * scaleFactor,
            rotatedSize.height * scaleFactor,
        )
        when (rotation) {
            Surface.ROTATION_0 -> {
                // identity
            }

            Surface.ROTATION_180 -> {
                preTranslate(1f, 1f)
                preRotate(180f)
            }

            Surface.ROTATION_90 -> {
                preTranslate(0f, 1f)
                preRotate(-90f)
            }

            Surface.ROTATION_270 -> {
                preTranslate(1f, 0f)
                preRotate(90f)
            }
        }
        if (flip) {
            preTranslate(1f, 0f)
            preScale(-1f, 1f)
        }
    }
    return transform
}

private fun DrawScope.drawBones(
    landmarks: Map<PoseLandmarkType, PoseLandmarkData>,
    transform: Matrix,
) {
    val bones = listOf(
        PoseLandmarkType.LeftShoulder to PoseLandmarkType.RightShoulder,
        PoseLandmarkType.LeftShoulder to PoseLandmarkType.LeftElbow,
        PoseLandmarkType.LeftElbow to PoseLandmarkType.LeftWrist,
        PoseLandmarkType.RightShoulder to PoseLandmarkType.RightElbow,
        PoseLandmarkType.RightElbow to PoseLandmarkType.RightWrist,
        PoseLandmarkType.LeftShoulder to PoseLandmarkType.LeftHip,
        PoseLandmarkType.RightShoulder to PoseLandmarkType.RightHip,
        PoseLandmarkType.LeftHip to PoseLandmarkType.RightHip,
        PoseLandmarkType.LeftHip to PoseLandmarkType.LeftKnee,
        PoseLandmarkType.LeftKnee to PoseLandmarkType.LeftAnkle,
        PoseLandmarkType.RightHip to PoseLandmarkType.RightKnee,
        PoseLandmarkType.RightKnee to PoseLandmarkType.RightAnkle,
        PoseLandmarkType.LeftAnkle to PoseLandmarkType.LeftHeel,
        PoseLandmarkType.LeftAnkle to PoseLandmarkType.LeftFootIndex,
        PoseLandmarkType.RightAnkle to PoseLandmarkType.RightHeel,
        PoseLandmarkType.RightAnkle to PoseLandmarkType.RightFootIndex,
    )

    bones.forEach { (start, end) ->
        val startLandmark = landmarks[start] ?: return@forEach
        val endLandmark = landmarks[end] ?: return@forEach

        val points = floatArrayOf(
            startLandmark.normalizedLandmark.x,
            startLandmark.normalizedLandmark.y,
            endLandmark.normalizedLandmark.x,
            endLandmark.normalizedLandmark.y,
        )
        transform.mapPoints(points)

        val path = Path().apply {
            moveTo(points[0], points[1])
            lineTo(points[2], points[3])
        }
        drawPath(
            path = path,
            color = Color.White,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round),
        )
    }
}

private fun DrawScope.drawJoints(
    landmarks: Map<PoseLandmarkType, PoseLandmarkData>,
    transform: Matrix,
    isOutline: Boolean,
) {
    landmarks.forEach { (type, data) ->
        val points = floatArrayOf(
            data.normalizedLandmark.x,
            data.normalizedLandmark.y,
        )
        transform.mapPoints(points)
        val center = Offset(points[0], points[1])

        val color = if (isOutline) {
            Color.White
        } else {
            when (type.position) {
                HorizontalPosition.LEFT -> Color(1f, 0.5f, 0.5f, 1f)
                HorizontalPosition.CENTER -> Color(0.25f, 0.25f, 0.25f, 1f)
                HorizontalPosition.RIGHT -> Color(0f, 0.5f, 1f, 1f)
            }
        }
        val radius = if (isOutline) 5.dp.toPx() else 3.dp.toPx()

        drawCircle(
            color = color,
            radius = radius,
            center = center,
        )
    }
}

private fun DrawScope.drawTrackerAttitudes(
    landmarks: Map<PoseLandmarkType, PoseLandmarkData>,
    trackers: Map<PoseTrackerType, PoseTrackerData>,
    transform: Matrix,
) {
    drawAttitude(
        landmarks = landmarks,
        trackers = trackers,
        trackerType = PoseTrackerType.Waist,
        startType = PoseLandmarkType.LeftHip,
        endType = PoseLandmarkType.RightHip,
        landmarkPosition = 0.5f,
        transform = transform,
    )
    drawAttitude(
        landmarks = landmarks,
        trackers = trackers,
        trackerType = PoseTrackerType.Chest,
        startType = PoseLandmarkType.LeftShoulder,
        endType = PoseLandmarkType.RightShoulder,
        landmarkPosition = 0.5f,
        transform = transform,
    )
    drawAttitude(
        landmarks = landmarks,
        trackers = trackers,
        trackerType = PoseTrackerType.Head,
        startType = PoseLandmarkType.LeftEar,
        endType = PoseLandmarkType.RightEar,
        landmarkPosition = 0.5f,
        transform = transform,
    )
    drawAttitude(
        landmarks = landmarks,
        trackers = trackers,
        trackerType = PoseTrackerType.LeftKnee,
        startType = PoseLandmarkType.LeftHip,
        endType = PoseLandmarkType.LeftKnee,
        landmarkPosition = 0.8f,
        transform = transform,
    )
    drawAttitude(
        landmarks = landmarks,
        trackers = trackers,
        trackerType = PoseTrackerType.RightKnee,
        startType = PoseLandmarkType.RightHip,
        endType = PoseLandmarkType.RightKnee,
        landmarkPosition = 0.8f,
        transform = transform,
    )
    drawAttitude(
        landmarks = landmarks,
        trackers = trackers,
        trackerType = PoseTrackerType.LeftFeet,
        startType = PoseLandmarkType.LeftAnkle,
        endType = PoseLandmarkType.LeftFootIndex,
        landmarkPosition = 0.5f,
        transform = transform,
    )
    drawAttitude(
        landmarks = landmarks,
        trackers = trackers,
        trackerType = PoseTrackerType.RightFeet,
        startType = PoseLandmarkType.RightAnkle,
        endType = PoseLandmarkType.RightFootIndex,
        landmarkPosition = 0.5f,
        transform = transform,
    )
    drawAttitude(
        landmarks = landmarks,
        trackers = trackers,
        trackerType = PoseTrackerType.LeftElbow,
        startType = PoseLandmarkType.LeftShoulder,
        endType = PoseLandmarkType.LeftElbow,
        landmarkPosition = 0.8f,
        transform = transform,
    )
    drawAttitude(
        landmarks = landmarks,
        trackers = trackers,
        trackerType = PoseTrackerType.RightElbow,
        startType = PoseLandmarkType.RightShoulder,
        endType = PoseLandmarkType.RightElbow,
        landmarkPosition = 0.8f,
        transform = transform,
    )
}

private fun DrawScope.drawAttitude(
    landmarks: Map<PoseLandmarkType, PoseLandmarkData>,
    trackers: Map<PoseTrackerType, PoseTrackerData>,
    trackerType: PoseTrackerType,
    startType: PoseLandmarkType,
    endType: PoseLandmarkType,
    landmarkPosition: Float,
    transform: Matrix,
) {
    val trackerData = trackers[trackerType] ?: return
    val startLandmark = landmarks[startType] ?: return
    val endLandmark = landmarks[endType] ?: return

    val position = startLandmark.normalizedLandmark * (1f - landmarkPosition) +
        endLandmark.normalizedLandmark * landmarkPosition
    val points = floatArrayOf(position.x, position.y)
    transform.mapPoints(points)
    val offset = Offset(points[0], points[1])

    val values = FloatArray(9)
    transform.getValues(values)
    val rotationAngle = atan2(values[Matrix.MSKEW_Y], values[Matrix.MSCALE_X]).toDouble()
    val rotation = Matrix4x4.rotation(axis = Vector3.Z, angle = rotationAngle.toFloat())

    val attitude = rotation * trackerData.attitude.unityToScreen()
    val axisX = attitude.columnVector3(0)
    val axisY = attitude.columnVector3(1)
    val axisZ = attitude.columnVector3(2)

    val axisLength = 33.dp.toPx()

    drawAxis(offset = offset, axis = axisX, color = Color.White, width = 5.dp.toPx(), length = axisLength)
    drawAxis(offset = offset, axis = axisY, color = Color.White, width = 5.dp.toPx(), length = axisLength)
    drawAxis(offset = offset, axis = axisZ, color = Color.White, width = 5.dp.toPx(), length = axisLength)

    drawAxis(offset = offset, axis = axisX, color = Color.Red, width = 3.dp.toPx(), length = 30.dp.toPx())
    drawAxis(offset = offset, axis = axisY, color = Color.Green, width = 3.dp.toPx(), length = 30.dp.toPx())
    drawAxis(offset = offset, axis = axisZ, color = Color.Blue, width = 3.dp.toPx(), length = 30.dp.toPx())
}

private fun DrawScope.drawAxis(
    offset: Offset,
    axis: Vector3,
    color: Color,
    width: Float,
    length: Float,
) {
    val endPoint = Offset(
        offset.x + axis.x * length,
        offset.y + axis.y * length,
    )

    val path = Path().apply {
        moveTo(offset.x, offset.y)
        lineTo(endPoint.x, endPoint.y)
    }

    drawPath(
        path = path,
        color = color,
        style = Stroke(width = width, cap = StrokeCap.Round),
    )
}
