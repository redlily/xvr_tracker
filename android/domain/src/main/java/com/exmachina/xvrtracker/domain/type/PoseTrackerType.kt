package com.exmachina.xvrtracker.domain.type

import com.exmachina.xvrtracker.data.type.HorizontalPosition
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class PoseTrackerType(
    val alignment: HorizontalPosition,
) {

    @SerialName("head")
    Head(
        alignment = HorizontalPosition.CENTER,
    ),

    @SerialName("chest")
    Chest(
        alignment = HorizontalPosition.CENTER,
    ),

    @SerialName("waist")
    Waist(
        alignment = HorizontalPosition.CENTER,
    ),

    @SerialName("left_elbow")
    LeftElbow(
        alignment = HorizontalPosition.LEFT,
    ),

    @SerialName("right_elbow")
    RightElbow(
        alignment = HorizontalPosition.RIGHT,
    ),

    @SerialName("left_knee")
    LeftKnee(
        alignment = HorizontalPosition.LEFT,
    ),

    @SerialName("right_knee")
    RightKnee(
        alignment = HorizontalPosition.RIGHT,
    ),

    @SerialName("left_feet")
    LeftFeet(
        alignment = HorizontalPosition.LEFT,
    ),

    @SerialName("right_feet")
    RightFeet(
        alignment = HorizontalPosition.RIGHT,
    ),
}
