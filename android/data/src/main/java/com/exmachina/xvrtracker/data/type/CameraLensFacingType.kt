package com.exmachina.xvrtracker.data.type

import androidx.camera.core.CameraSelector
import com.exmachina.xvrtracker.common.util.toImmutableMap
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class CameraLensFacingType(
    val id: Int,
    val value: Int,
) {

    @SerialName("back")
    BACK(
        id = 0,
        value = CameraSelector.LENS_FACING_BACK,
    ),

    @SerialName("front")
    FRONT(
        id = 1,
        value = CameraSelector.LENS_FACING_FRONT,
    ),
    ;

    companion object {

        private val entryMap = entries.associateBy { it.id }.toImmutableMap()

        fun fromId(id: Int): CameraLensFacingType = entryMap[id] ?: BACK
    }
}

fun Int.toCameraLensFacingType() = CameraLensFacingType.fromId(this)
