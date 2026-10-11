package com.exmachina.xvrtracker.data.type

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class Size2D(
    val width: Float,
    val height: Float,
) {

    companion object {

        val zero = Size2D(width = 0f, height = 0f)
    }
}
