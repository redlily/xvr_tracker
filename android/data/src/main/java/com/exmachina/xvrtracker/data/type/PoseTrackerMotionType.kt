package com.exmachina.xvrtracker.data.type

import com.exmachina.xvrtracker.common.util.toImmutableMap
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class PoseTrackerMotionType(
    val id: Int,
    val isPositionEnabled: Boolean,
    val isRotationEnabled: Boolean,
) {

    @SerialName("disable")
    DISABLE(
        id = 0,
        isPositionEnabled = false,
        isRotationEnabled = false,
    ),

    @SerialName("position")
    POSITION(
        id = 1,
        isPositionEnabled = true,
        isRotationEnabled = false,
    ),

    @SerialName("rotation")
    ROTATION(
        id = 2,
        isPositionEnabled = true,
        isRotationEnabled = false,
    ),

    @SerialName("position_and_rotation")
    POSITION_AND_ROTATION(
        id = 3,
        isPositionEnabled = true,
        isRotationEnabled = true,
    ),
    ;

    companion object {

        private val entryMap = entries.associateBy { it.id }.toImmutableMap()

        fun fromId(id: Int): PoseTrackerMotionType = entryMap[id] ?: DISABLE
    }
}

fun Int.toPoseTrackerDataType() = PoseTrackerMotionType.fromId(this)
