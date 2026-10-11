package com.exmachina.xvrtracker.data.type

import com.exmachina.xvrtracker.common.util.toImmutableMap
import com.google.mediapipe.tasks.core.Delegate
import kotlinx.serialization.Serializable

@Serializable
enum class PoseLandmarkerComputeType(
    val id: Int,
    val delegate: Delegate,
) {

    CPU(
        id = 0,
        delegate = Delegate.CPU,
    ),
    GPU(
        id = 1,
        delegate = Delegate.GPU,
    ),
    ;

    companion object {

        private val entryMap = entries.associateBy { it.id }.toImmutableMap()

        fun fromId(id: Int): PoseLandmarkerComputeType = entryMap[id] ?: CPU
    }
}

fun Int.toPoseLandmarkerComputeType() = PoseLandmarkerComputeType.fromId(this)
