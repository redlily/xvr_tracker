package com.exmachina.xvrtracker.domain.logic

import com.exmachina.xvrtracker.common.math.Axis3
import com.exmachina.xvrtracker.common.math.Matrix4x4
import com.exmachina.xvrtracker.common.math.Vector3
import com.exmachina.xvrtracker.data.type.PoseLandmarkData
import com.exmachina.xvrtracker.data.type.PoseLandmarkType
import com.exmachina.xvrtracker.data.type.PoseLandmarksData
import com.exmachina.xvrtracker.domain.type.PoseDirections
import javax.inject.Inject

class PoseDirectionAnalyzer @Inject constructor() {

    private var previousDirections: List<PoseDirections> = emptyList()

    fun reset() {
        previousDirections = emptyList()
    }

    fun analyze(landmarksData: PoseLandmarksData): List<PoseDirections> {
        val directions =
            landmarksData.landmarks.mapIndexed { index, landmarks ->
                analyzePose(
                    landmarks = landmarks,
                    previousDirections = previousDirections.getOrNull(index),
                )
            }
        previousDirections = directions
        return directions
    }

    private fun analyzePose(
        landmarks: Map<PoseLandmarkType, PoseLandmarkData>,
        previousDirections: PoseDirections?,
    ): PoseDirections {
        // hip

        val leftHip = landmarks[PoseLandmarkType.LeftHip]?.worldLandmark
        val rightHip = landmarks[PoseLandmarkType.RightHip]?.worldLandmark

        val hip = Vector3.center(leftHip, rightHip)
        val hipToSide = Vector3.direction(leftHip, rightHip)

        // shoulder

        val leftShoulder = landmarks[PoseLandmarkType.LeftShoulder]?.worldLandmark
        val rightShoulder = landmarks[PoseLandmarkType.RightShoulder]?.worldLandmark

        val shoulder = Vector3.center(leftShoulder, rightShoulder)
        val shoulderToSide = Vector3.direction(leftShoulder, rightShoulder)
        val hipToShoulder = Vector3.direction(hip, shoulder)

        // head

        val nose = landmarks[PoseLandmarkType.Nose]?.worldLandmark
        val leftEar = landmarks[PoseLandmarkType.LeftEar]?.worldLandmark
        val rightEar = landmarks[PoseLandmarkType.RightEar]?.worldLandmark

        val head = Vector3.center(leftEar, rightEar)
        val headToSide = Vector3.direction(leftEar, rightEar)
        val headToForward = Vector3.direction(head, nose)
        val shoulderToHead = Vector3.direction(shoulder, head)

        return PoseDirections(
            hipToSide = hipToSide,
            hipToShoulder = hipToShoulder,
            shoulderToSide = shoulderToSide,
            shoulderToHead = shoulderToHead,
            headToSide = headToSide,
            headToForward = headToForward,
            leftArm =
                analyzeArmPose(
                    landmarks = landmarks,
                    shoulderToSide = shoulderToSide,
                    hipToShoulder = hipToShoulder,
                    previous = previousDirections,
                    isLeft = true,
                ),
            leftLeg =
                analyzeLegPoseDirection(
                    landmarks = landmarks,
                    hipToSide = hipToSide,
                    hipToShoulder = hipToShoulder,
                    previous = previousDirections,
                    isLeft = true,
                ),
            rightArm =
                analyzeArmPose(
                    landmarks = landmarks,
                    shoulderToSide = shoulderToSide,
                    hipToShoulder = hipToShoulder,
                    previous = previousDirections,
                    isLeft = false,
                ),
            rightLeg =
                analyzeLegPoseDirection(
                    landmarks = landmarks,
                    hipToSide = hipToSide,
                    hipToShoulder = hipToShoulder,
                    previous = previousDirections,
                    isLeft = false,
                ),
        )
    }

    private fun analyzeArmPose(
        landmarks: Map<PoseLandmarkType, PoseLandmarkData>,
        shoulderToSide: Vector3?,
        hipToShoulder: Vector3?,
        previous: PoseDirections?,
        isLeft: Boolean,
    ): PoseDirections.ArmPoseDirections {
        val shoulder =
            landmarks[
                if (isLeft) PoseLandmarkType.LeftShoulder else PoseLandmarkType.RightShoulder,
            ]?.worldLandmark
        val elbow =
            landmarks[
                if (isLeft) PoseLandmarkType.LeftElbow else PoseLandmarkType.RightElbow,
            ]?.worldLandmark
        val wrist =
            landmarks[
                if (isLeft) PoseLandmarkType.LeftWrist else PoseLandmarkType.RightWrist,
            ]?.worldLandmark

        // modification matrix

        val prevShoulderToSide = previous?.shoulderToSide
        val prevHipToShoulder = previous?.hipToShoulder
        var modificationMatrix =
            if (
                prevShoulderToSide != null &&
                prevHipToShoulder != null &&
                shoulderToSide != null &&
                hipToShoulder != null
            ) {
                Axis3
                    .xzy(
                        x = shoulderToSide,
                        ref = hipToShoulder,
                    ).toMatrix4x4() *
                    Axis3
                        .xzy(
                            x = prevShoulderToSide,
                            ref = prevHipToShoulder,
                        ).toMatrix4x4()
                        .transpose()
            } else {
                Matrix4x4.IDENTITY
            }

        val prevArm = previous?.let { if (isLeft) it.leftArm else it.rightArm }

        // shoulder to elbow

        val prevShoulderToElbow = prevArm?.shoulderToElbow
        val shoulderToElbow =
            Vector3.direction(shoulder, elbow)?.also {
                if (prevShoulderToElbow != null) {
                    modificationMatrix = Matrix4x4.rotation(
                        from = prevShoulderToElbow,
                        to = it,
                    ) * modificationMatrix
                }
            } ?: run {
                prevShoulderToElbow?.let {
                    modificationMatrix * it
                }
            }

        // elbow to wrist

        val elbowToWrist =
            Vector3.direction(elbow, wrist) ?: run {
                prevArm?.elbowToWrist?.let {
                    modificationMatrix * it
                }
            }

        return PoseDirections.ArmPoseDirections(
            shoulderToElbow = shoulderToElbow,
            elbowToWrist = elbowToWrist,
        )
    }

    private fun analyzeLegPoseDirection(
        landmarks: Map<PoseLandmarkType, PoseLandmarkData>,
        hipToSide: Vector3?,
        hipToShoulder: Vector3?,
        previous: PoseDirections?,
        isLeft: Boolean,
    ): PoseDirections.LegPoseDirections {
        val hip =
            landmarks[
                if (isLeft) PoseLandmarkType.LeftHip else PoseLandmarkType.RightHip,
            ]?.worldLandmark
        val knee =
            landmarks[
                if (isLeft) PoseLandmarkType.LeftKnee else PoseLandmarkType.RightKnee,
            ]?.worldLandmark
        val ankle =
            landmarks[
                if (isLeft) PoseLandmarkType.LeftAnkle else PoseLandmarkType.RightAnkle,
            ]?.worldLandmark
        val toe =
            landmarks[
                if (isLeft) PoseLandmarkType.LeftFootIndex else PoseLandmarkType.RightFootIndex,
            ]?.worldLandmark

        // modification matrix

        val prevHipToSide = previous?.hipToSide
        val prevHipToShoulder = previous?.hipToShoulder
        var modificationMatrix =
            if (
                prevHipToSide != null &&
                prevHipToShoulder != null &&
                hipToSide != null &&
                hipToShoulder != null
            ) {
                Axis3
                    .xzy(
                        x = hipToSide,
                        ref = hipToShoulder,
                    ).toMatrix4x4() *
                    Axis3
                        .xzy(
                            x = prevHipToSide,
                            ref = prevHipToShoulder,
                        ).toMatrix4x4()
                        .transpose()
            } else {
                Matrix4x4.IDENTITY
            }

        val prevLeg = previous?.let { if (isLeft) it.leftLeg else it.rightLeg }

        // hip to knee

        val prevHipToKnee = prevLeg?.hipToKnee
        val hipToKnee =
            Vector3.direction(hip, knee)?.also {
                if (prevHipToKnee != null) {
                    modificationMatrix = Matrix4x4.rotation(
                        from = prevHipToKnee,
                        to = it,
                    ) * modificationMatrix
                }
            } ?: run {
                prevHipToKnee?.let {
                    modificationMatrix * it
                }
            }

        // knee to ankle

        val prevKneeToAnkle = prevLeg?.kneeToAnkle
        val kneeToAnkle =
            Vector3.direction(knee, ankle)?.also {
                if (prevKneeToAnkle != null) {
                    modificationMatrix = Matrix4x4.rotation(
                        from = prevKneeToAnkle,
                        to = it,
                    ) * modificationMatrix
                }
            } ?: run {
                prevKneeToAnkle?.let {
                    modificationMatrix * it
                }
            }

        // ankle to toe

        val ankleToToe =
            Vector3.direction(ankle, toe) ?: run {
                prevLeg?.ankleToToe?.let {
                    modificationMatrix * it
                }
            }

        return PoseDirections.LegPoseDirections(
            hipToKnee = hipToKnee,
            kneeToAnkle = kneeToAnkle,
            ankleToToe = ankleToToe,
        )
    }
}
