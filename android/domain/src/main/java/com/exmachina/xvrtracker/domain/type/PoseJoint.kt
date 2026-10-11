package com.exmachina.xvrtracker.domain.type

import androidx.compose.runtime.Immutable
import com.exmachina.xvrtracker.common.math.Axis3
import com.exmachina.xvrtracker.common.math.Vector3
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class PoseJoint(
    val position: Vector3,
    val axis: Axis3,
)

@Serializable
@Immutable
data class PoseJoints(
    val waist: PoseJoint?,
    val chest: PoseJoint?,
    val head: PoseJoint?,
    val leftArm: ArmPoseJoints,
    val leftLeg: LegPoseJoints,
    val rightArm: ArmPoseJoints,
    val rightLeg: LegPoseJoints,
) {

    @Serializable
    @Immutable
    data class ArmPoseJoints(
        val elbow: PoseJoint?,
    )

    @Serializable
    @Immutable
    data class LegPoseJoints(
        val knee: PoseJoint?,
        val feet: PoseJoint?,
    )
}

@Serializable
@Immutable
data class PoseJointsData(
    val data: List<PoseJoints>,
    val timestamp: Long,
)
