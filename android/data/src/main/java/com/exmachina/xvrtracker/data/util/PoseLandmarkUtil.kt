package com.exmachina.xvrtracker.data.util

import com.exmachina.xvrtracker.common.math.Matrix4x4
import com.exmachina.xvrtracker.common.math.Vector3
import com.exmachina.xvrtracker.common.math.mediaPipeToUnity
import com.exmachina.xvrtracker.common.util.toImmutableList
import com.exmachina.xvrtracker.common.util.toImmutableMap
import com.exmachina.xvrtracker.data.type.PoseLandmarkData
import com.exmachina.xvrtracker.data.type.PoseLandmarkType
import com.exmachina.xvrtracker.data.type.Size2D
import com.exmachina.xvrtracker.data.type.toPoseLandmarkType
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.components.containers.Landmark
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import kotlin.jvm.optionals.getOrDefault

val Landmark.position
    get() = Vector3(x(), y(), z())

val NormalizedLandmark.position
    get() = Vector3(x(), y(), z())

val MPImage.size
    get() = Size2D(width.toFloat(), height.toFloat())

fun PoseLandmarkerResult.toPoseLandmarksDataList(
    transform: Matrix4x4,
): List<Map<PoseLandmarkType, PoseLandmarkData>> =
    landmarks()
        .zip(worldLandmarks())
        .map { (landmark, worldLandmark) ->
            landmark
                .zip(worldLandmark)
                .mapIndexedNotNull { index, (landmark, worldLandmark) ->
                    index.toPoseLandmarkType()?.let { type ->
                        type to
                            PoseLandmarkData(
                                normalizedLandmark = transform * landmark.position,
                                worldLandmark = (transform * worldLandmark.position).mediaPipeToUnity(),
                                visibility = landmark.visibility().getOrDefault(0f),
                                presence = landmark.presence().getOrDefault(0f),
                            )
                    }
                }
                .toMap()
                .toImmutableMap()
        }
        .toImmutableList()
