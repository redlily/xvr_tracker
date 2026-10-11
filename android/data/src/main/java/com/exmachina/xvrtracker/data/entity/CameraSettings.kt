package com.exmachina.xvrtracker.data.entity

import androidx.compose.runtime.Immutable
import com.exmachina.xvrtracker.data.type.CameraLensFacingType
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class CameraSettings(
    val lensFacingType: CameraLensFacingType,
    val zoomRatio: Float,
)
