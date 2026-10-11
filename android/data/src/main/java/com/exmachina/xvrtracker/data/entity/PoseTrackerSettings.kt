package com.exmachina.xvrtracker.data.entity

import androidx.compose.runtime.Immutable
import com.exmachina.xvrtracker.data.type.PoseTrackerMotionType
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class PoseTrackerSettings(
    val head: PoseTrackerMotionType,
    val chest: PoseTrackerMotionType,
    val waist: PoseTrackerMotionType,
    val elbow: PoseTrackerMotionType,
    val knee: PoseTrackerMotionType,
    val feet: PoseTrackerMotionType,
) {

    companion object {

        val default = PoseTrackerSettings(
            head = PoseTrackerMotionType.POSITION_AND_ROTATION,
            chest = PoseTrackerMotionType.POSITION_AND_ROTATION,
            waist = PoseTrackerMotionType.POSITION_AND_ROTATION,
            elbow = PoseTrackerMotionType.DISABLE,
            knee = PoseTrackerMotionType.DISABLE,
            feet = PoseTrackerMotionType.POSITION_AND_ROTATION,
        )
    }
}
