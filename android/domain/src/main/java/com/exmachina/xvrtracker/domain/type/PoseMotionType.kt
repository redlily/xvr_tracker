package com.exmachina.xvrtracker.domain.type

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class PoseMotionType {

    @SerialName("stand")
    Stand,

    @SerialName("sit")
    Sit,

    @SerialName("lie_down")
    LieDown,
}
