package com.exmachina.xvrtracker.data.type

import androidx.compose.runtime.Immutable
import com.exmachina.xvrtracker.common.math.Vector3
import com.exmachina.xvrtracker.common.util.emptyImmutableList
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class PoseLandmarkData(
    val normalizedLandmark: Vector3,
    val worldLandmark: Vector3,
    val visibility: Float = 1f,
    val presence: Float = 1f,
)

@Serializable
@Immutable
data class PoseLandmarksData(
    val imageSize: Size2D,
    val landmarks: List<Map<PoseLandmarkType, PoseLandmarkData>>,
    val timestamp: Long,
) {

    companion object {

        val empty =
            PoseLandmarksData(
                imageSize = Size2D.zero,
                landmarks = emptyImmutableList(),
                timestamp = 0,
            )
    }
}
