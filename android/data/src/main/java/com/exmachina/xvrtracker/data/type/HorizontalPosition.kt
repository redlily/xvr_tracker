package com.exmachina.xvrtracker.data.type

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class HorizontalPosition {

    @SerialName("left")
    LEFT,

    @SerialName("center")
    CENTER,

    @SerialName("right")
    RIGHT,
}
