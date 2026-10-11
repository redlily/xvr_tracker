package com.exmachina.xvrtracker.domain.logic

import com.exmachina.xvrtracker.common.math.Axis3
import com.exmachina.xvrtracker.common.math.Axis3.Companion.times
import com.exmachina.xvrtracker.common.math.Matrix4x4
import com.exmachina.xvrtracker.common.math.Vector3
import com.exmachina.xvrtracker.data.repository.BodyMeasurementSettingsRepository
import com.exmachina.xvrtracker.domain.type.BodyMeasurements
import com.exmachina.xvrtracker.domain.type.PoseDirections
import com.exmachina.xvrtracker.domain.type.PoseJoint
import com.exmachina.xvrtracker.domain.type.PoseJoints
import com.exmachina.xvrtracker.domain.type.bodyMeasurements
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min

class PoseJointAnalyzer @Inject constructor(
    bodyMeasurementSettingsRepository: BodyMeasurementSettingsRepository,
) {

    companion object {

        private val hipAngleThreshold = cos(Math.toRadians(60.0)).toFloat()
        private val elbowAngleThreshold = cos(Math.toRadians(20.0)).toFloat()
        private val kneeAngleThreshold = cos(Math.toRadians(20.0)).toFloat()
        private val elbowAngleMaximum = acos(elbowAngleThreshold)
        private val kneeAngleMaximum = acos(kneeAngleThreshold)

        private fun rotationRate(dot: Float, maximum: Float): Float {
            val clampedDot = max(-1f, min(1f, dot))
            val numerator = acos(clampedDot)
            if (maximum <= 0f) return 1f
            return min(numerator / maximum, 1f)
        }
    }

    private val bodyMeasurementsFlow = bodyMeasurementSettingsRepository.dataFlow.map { it.bodyMeasurements() }

    suspend fun analyze(directionsData: List<PoseDirections>): List<PoseJoints> {
        val bodyMeasurements = bodyMeasurementsFlow.first()
        return directionsData.map {
            analyzePoseJoints(
                poseDirections = it,
                bodyMeasurements = bodyMeasurements,
            )
        }
    }

    private fun analyzePoseJoints(
        poseDirections: PoseDirections,
        bodyMeasurements: BodyMeasurements,
    ): PoseJoints {
        val hipToSide = poseDirections.hipToSide
        val hipToShoulder = poseDirections.hipToShoulder
        val shoulderToSide = poseDirections.shoulderToSide
        val shoulderToHead = poseDirections.shoulderToHead
        val headToSide = poseDirections.headToSide
        val headToForward = poseDirections.headToForward

        val hip = if (
            hipToSide != null &&
            hipToShoulder != null
        ) {
            PoseJoint(
                position = Vector3.ZERO,
                axis = Axis3.xzy(x = hipToSide, ref = hipToShoulder),
            )
        } else {
            null
        }

        val waist = if (
            hip != null &&
            hipToShoulder != null
        ) {
            PoseJoint(
                position =
                    hip.position +
                        hipToShoulder * bodyMeasurements.waistToHipLength,
                axis = hip.axis,
            )
        } else {
            null
        }

        val shoulder = if (
            shoulderToSide != null &&
            hipToShoulder != null
        ) {
            PoseJoint(
                position = hipToShoulder * bodyMeasurements.hipToShoulderLength,
                axis = Axis3.xzy(x = shoulderToSide, ref = hipToShoulder),
            )
        } else {
            null
        }

        val chest = if (
            shoulder != null &&
            hipToShoulder != null
        ) {
            PoseJoint(
                position =
                    shoulder.position -
                        hipToShoulder * bodyMeasurements.shoulderToChestLength,
                axis = shoulder.axis,
            )
        } else {
            null
        }

        val head = if (
            shoulder != null &&
            shoulderToHead != null &&
            headToSide != null &&
            headToForward != null
        ) {
            PoseJoint(
                position =
                    shoulder.position +
                        shoulderToHead * bodyMeasurements.headToShoulderLength +
                        headToForward * -bodyMeasurements.headHeight * 0.25f +
                        headToForward * bodyMeasurements.headZOffset,
                axis = Axis3.zyx(z = headToForward, ref = headToSide),
            )
        } else {
            null
        }

        return PoseJoints(
            waist = waist,
            chest = chest,
            head = head,
            leftArm =
                analyzeArmPoseJoints(
                    shoulder = shoulder,
                    armPoseDirections = poseDirections.leftArm,
                    bodyMeasurements = bodyMeasurements,
                    isLeft = true,
                ),
            leftLeg =
                analyzeLegPoseJoints(
                    hip = hip,
                    legPoseDirections = poseDirections.leftLeg,
                    bodyMeasurements = bodyMeasurements,
                    isLeft = true,
                ),
            rightArm =
                analyzeArmPoseJoints(
                    shoulder = shoulder,
                    armPoseDirections = poseDirections.rightArm,
                    bodyMeasurements = bodyMeasurements,
                    isLeft = false,
                ),
            rightLeg =
                analyzeLegPoseJoints(
                    hip = hip,
                    legPoseDirections = poseDirections.rightLeg,
                    bodyMeasurements = bodyMeasurements,
                    isLeft = false,
                ),
        )
    }

    private fun analyzeArmPoseJoints(
        shoulder: PoseJoint?,
        armPoseDirections: PoseDirections.ArmPoseDirections,
        bodyMeasurements: BodyMeasurements,
        isLeft: Boolean,
    ): PoseJoints.ArmPoseJoints {
        val shoulderToElbow = armPoseDirections.shoulderToElbow
        val elbowToWrist = armPoseDirections.elbowToWrist

        val elbow = if (
            shoulder != null &&
            shoulderToElbow != null
        ) {
            val axis = Matrix4x4.rotation(
                from = shoulder.axis.x,
                to = if (isLeft) -shoulderToElbow else shoulderToElbow,
            ) * shoulder.axis

            val modifiedAxis = if (
                elbowToWrist != null &&
                shoulder.axis.z dot Vector3.Z > hipAngleThreshold
            ) {
                val axisRotation = if (isLeft) {
                    shoulderToElbow dotCross elbowToWrist
                } else {
                    elbowToWrist dotCross shoulderToElbow
                }
                Matrix4x4.rotation(
                    from = axis.y,
                    to = axisRotation.cross,
                    rate = rotationRate(axisRotation.dot, elbowAngleMaximum),
                ) * axis
            } else {
                axis
            }

            PoseJoint(
                position = shoulder.position +
                    (if (isLeft) -shoulder.axis.x else shoulder.axis.x) * bodyMeasurements.shoulderWidth / 2f +
                    shoulderToElbow * bodyMeasurements.armLength / 2f,
                axis = modifiedAxis,
            )
        } else {
            null
        }

        return PoseJoints.ArmPoseJoints(
            elbow = elbow,
        )
    }

    private fun analyzeLegPoseJoints(
        hip: PoseJoint?,
        legPoseDirections: PoseDirections.LegPoseDirections,
        bodyMeasurements: BodyMeasurements,
        isLeft: Boolean,
    ): PoseJoints.LegPoseJoints {
        val hipToKnee = legPoseDirections.hipToKnee
        val kneeToAnkle = legPoseDirections.kneeToAnkle
        val ankleToToe = legPoseDirections.ankleToToe

        val knee = if (
            hip != null &&
            hipToKnee != null
        ) {
            val axis = Matrix4x4.rotation(
                from = hip.axis.y,
                to = -hipToKnee,
            ) * hip.axis

            val modifiedAxis = if (
                kneeToAnkle != null &&
                hip.axis.z dot Vector3.Z > hipAngleThreshold
            ) {
                val rotationAxis = hipToKnee dotCross kneeToAnkle

                if (hip.axis.x dot rotationAxis.cross > 0) {
                    Matrix4x4.rotation(
                        from = axis.x,
                        to = rotationAxis.cross,
                        rate = rotationRate(rotationAxis.dot, kneeAngleMaximum),
                    ) * axis
                } else {
                    axis
                }
            } else {
                axis
            }

            PoseJoint(
                position = hip.position +
                    (if (isLeft) -hip.axis.x else hip.axis.x) * bodyMeasurements.hipWidth / 2f +
                    hipToKnee * bodyMeasurements.legLength / 2f,
                axis = modifiedAxis,
            )
        } else {
            null
        }

        val feet = if (knee != null && kneeToAnkle != null) {
            val axis = Matrix4x4.rotation(
                from = knee.axis.y,
                to = -kneeToAnkle,
            ) * knee.axis

            if (
                ankleToToe != null &&
                hip != null &&
                hip.axis.z dot Vector3.Z > hipAngleThreshold
            ) {
                val rotationAxis = ankleToToe dotCross kneeToAnkle
                val modifiedAxis = Matrix4x4.rotation(
                    from = axis.x,
                    to = rotationAxis.cross,
                    rate = rotationRate(rotationAxis.dot, kneeAngleMaximum),
                ) * axis

                PoseJoint(
                    position = knee.position +
                        kneeToAnkle * bodyMeasurements.legLength / 2f +
                        ankleToToe * bodyMeasurements.footLength / 2f,
                    axis = modifiedAxis,
                )
            } else {
                PoseJoint(
                    position = knee.position +
                        kneeToAnkle * bodyMeasurements.legLength / 2f +
                        (-axis.y * bodyMeasurements.ankleToHeelLength +
                            axis.z * bodyMeasurements.footLength) / 2f,
                    axis = axis,
                )
            }
        } else {
            null
        }

        return PoseJoints.LegPoseJoints(
            knee = knee,
            feet = feet,
        )
    }
}
