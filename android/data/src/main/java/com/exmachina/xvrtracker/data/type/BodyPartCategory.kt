package com.exmachina.xvrtracker.data.type

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class BodyPartCategory {

    @SerialName("head")
    Head,

    @SerialName("arm")
    Arm,

    @SerialName("body")
    Body,

    @SerialName("leg")
    Leg,
}
