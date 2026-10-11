package com.exmachina.xvrtracker.domain.type

import androidx.compose.runtime.Immutable
import com.exmachina.xvrtracker.common.math.Vector3
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class PoseDirections(
    val hipToSide: Vector3?,
    val hipToShoulder: Vector3?,
    val shoulderToSide: Vector3?,
    val shoulderToHead: Vector3?,
    val headToSide: Vector3?,
    val headToForward: Vector3?,
    val leftArm: ArmPoseDirections,
    val leftLeg: LegPoseDirections,
    val rightArm: ArmPoseDirections,
    val rightLeg: LegPoseDirections,
) {

    @Serializable
    @Immutable
    data class ArmPoseDirections(
        val shoulderToElbow: Vector3?,
        val elbowToWrist: Vector3?,
    )

    @Serializable
    @Immutable
    data class LegPoseDirections(
        val hipToKnee: Vector3?,
        val kneeToAnkle: Vector3?,
        val ankleToToe: Vector3?,
    )
}

@Serializable
@Immutable
data class PoseDirectionsData(
    val data: List<PoseDirections>,
    val timestamp: Long,
)
