package com.exmachina.xvrtracker.data.type

import com.exmachina.xvrtracker.common.util.toImmutableMap
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class PoseLandmarkType(
    val id: Int,
    val category: BodyPartCategory,
    val position: HorizontalPosition,
    val parent: PoseLandmarkType? = null,
) {

    @SerialName("nose")
    Nose(
        id = 0,
        category = BodyPartCategory.Head,
        position = HorizontalPosition.CENTER,
    ),

    @SerialName("left_eye_inner")
    LeftEyeInner(
        id = 1,
        category = BodyPartCategory.Head,
        position = HorizontalPosition.LEFT,
    ),

    @SerialName("left_eye")
    LeftEye(
        id = 2,
        category = BodyPartCategory.Head,
        position = HorizontalPosition.LEFT,
    ),

    @SerialName("left_eye_outer")
    LeftEyeOuter(
        id = 3,
        category = BodyPartCategory.Head,
        position = HorizontalPosition.LEFT,
    ),

    @SerialName("right_eye_inner")
    RightEyeInner(
        id = 4,
        category = BodyPartCategory.Head,
        position = HorizontalPosition.RIGHT,
    ),

    @SerialName("right_eye")
    RightEye(
        id = 5,
        category = BodyPartCategory.Head,
        position = HorizontalPosition.RIGHT,
    ),

    @SerialName("right_eye_outer")
    RightEyeOuter(
        id = 6,
        category = BodyPartCategory.Head,
        position = HorizontalPosition.RIGHT,
    ),

    @SerialName("left_ear")
    LeftEar(
        id = 7,
        category = BodyPartCategory.Head,
        position = HorizontalPosition.LEFT,
    ),

    @SerialName("right_ear")
    RightEar(
        id = 8,
        category = BodyPartCategory.Head,
        position = HorizontalPosition.RIGHT,
    ),

    @SerialName("mouth_left")
    MouthLeft(
        id = 9,
        category = BodyPartCategory.Head,
        position = HorizontalPosition.LEFT,
    ),

    @SerialName("mouth_right")
    MouthRight(
        id = 10,
        category = BodyPartCategory.Head,
        position = HorizontalPosition.RIGHT,
    ),

    @SerialName("left_shoulder")
    LeftShoulder(
        id = 11,
        category = BodyPartCategory.Body,
        position = HorizontalPosition.LEFT,
    ),

    @SerialName("right_shoulder")
    RightShoulder(
        id = 12,
        category = BodyPartCategory.Body,
        position = HorizontalPosition.RIGHT,
    ),

    @SerialName("left_elbow")
    LeftElbow(
        id = 13,
        category = BodyPartCategory.Arm,
        position = HorizontalPosition.LEFT,
        parent = LeftShoulder,
    ),

    @SerialName("right_elbow")
    RightElbow(
        id = 14,
        category = BodyPartCategory.Arm,
        position = HorizontalPosition.RIGHT,
        parent = RightShoulder,
    ),

    @SerialName("left_wrist")
    LeftWrist(
        id = 15,
        category = BodyPartCategory.Arm,
        position = HorizontalPosition.LEFT,
        parent = LeftElbow,
    ),

    @SerialName("right_wrist")
    RightWrist(
        id = 16,
        category = BodyPartCategory.Arm,
        position = HorizontalPosition.RIGHT,
        parent = RightElbow,
    ),

    @SerialName("left_pinky")
    LeftPinky(
        id = 17,
        category = BodyPartCategory.Arm,
        position = HorizontalPosition.LEFT,
        parent = LeftWrist,
    ),

    @SerialName("right_pinky")
    RightPinky(
        id = 18,
        category = BodyPartCategory.Arm,
        position = HorizontalPosition.RIGHT,
        parent = RightWrist,
    ),

    @SerialName("left_index")
    LeftIndex(
        id = 19,
        category = BodyPartCategory.Arm,
        position = HorizontalPosition.LEFT,
        parent = LeftWrist,
    ),

    @SerialName("right_index")
    RightIndex(
        id = 20,
        category = BodyPartCategory.Arm,
        position = HorizontalPosition.RIGHT,
        parent = RightWrist,
    ),

    @SerialName("left_thumb")
    LeftThumb(
        id = 21,
        category = BodyPartCategory.Arm,
        position = HorizontalPosition.LEFT,
        parent = LeftWrist,
    ),

    @SerialName("right_thumb")
    RightThumb(
        id = 22,
        category = BodyPartCategory.Arm,
        position = HorizontalPosition.RIGHT,
    ),

    @SerialName("left_hip")
    LeftHip(
        id = 23,
        category = BodyPartCategory.Body,
        position = HorizontalPosition.LEFT,
    ),

    @SerialName("right_hip")
    RightHip(
        id = 24,
        category = BodyPartCategory.Body,
        position = HorizontalPosition.RIGHT,
    ),

    @SerialName("left_knee")
    LeftKnee(
        id = 25,
        category = BodyPartCategory.Leg,
        position = HorizontalPosition.LEFT,
        parent = LeftHip,
    ),

    @SerialName("right_knee")
    RightKnee(
        id = 26,
        category = BodyPartCategory.Leg,
        position = HorizontalPosition.RIGHT,
        parent = RightHip,
    ),

    @SerialName("left_ankle")
    LeftAnkle(
        id = 27,
        category = BodyPartCategory.Leg,
        position = HorizontalPosition.LEFT,
        parent = LeftKnee,
    ),

    @SerialName("right_ankle")
    RightAnkle(
        id = 28,
        category = BodyPartCategory.Leg,
        position = HorizontalPosition.RIGHT,
        parent = RightKnee,
    ),

    @SerialName("left_heel")
    LeftHeel(
        id = 29,
        category = BodyPartCategory.Leg,
        position = HorizontalPosition.LEFT,
        parent = LeftAnkle,
    ),

    @SerialName("right_heel")
    RightHeel(
        id = 30,
        category = BodyPartCategory.Leg,
        position = HorizontalPosition.RIGHT,
        parent = RightAnkle,
    ),

    @SerialName("left_foot_index")
    LeftFootIndex(
        id = 31,
        category = BodyPartCategory.Leg,
        position = HorizontalPosition.LEFT,
        parent = LeftAnkle,
    ),

    @SerialName("right_foot_index")
    RightFootIndex(
        id = 32,
        category = BodyPartCategory.Leg,
        position = HorizontalPosition.RIGHT,
        parent = RightAnkle,
    ),
    ;

    companion object {

        private val entryMap = entries.associateBy { it.id }.toImmutableMap()

        fun fromId(id: Int): PoseLandmarkType? = entryMap[id]
    }
}

fun Int.toPoseLandmarkType() = PoseLandmarkType.fromId(this)
