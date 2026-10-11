package com.exmachina.xvrtracker.domain.type

import com.exmachina.xvrtracker.common.math.Axis3
import com.exmachina.xvrtracker.common.math.Matrix4x4
import com.exmachina.xvrtracker.common.math.Vector3
import com.exmachina.xvrtracker.data.type.PoseTrackerMotionType
import kotlinx.serialization.Serializable

@Serializable
data class PoseTrackerData(
    val dataType: PoseTrackerMotionType,
    val attitude: Matrix4x4,
    val position: Vector3 = Vector3.ZERO,
    val axis: Axis3? = null,
)

@Serializable
data class PoseTrackersData(
    val trackers: List<Map<PoseTrackerType, PoseTrackerData>>,
    val modificationMatrix: Matrix4x4,
    val timestamp: Long,
) {

    companion object {

        val empty =
            PoseTrackersData(
                trackers = emptyList(),
                modificationMatrix = Matrix4x4.IDENTITY,
                timestamp = 0,
            )
    }
}
