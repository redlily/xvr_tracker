package com.exmachina.xvrtracker.data.entity

import androidx.compose.runtime.Immutable
import com.exmachina.xvrtracker.common.type.RangedValue
import com.exmachina.xvrtracker.common.type.limits
import com.exmachina.xvrtracker.data.type.PoseLandmarkerComputeType
import com.exmachina.xvrtracker.data.type.PoseLandmarkerModelType
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class PoseLandmarkerSettings(
    val modelType: PoseLandmarkerModelType,
    val computeType: PoseLandmarkerComputeType,
    val visibilityThreshold: RangedValue<Float>,
    val presenceThreshold: RangedValue<Float>,
) {

    companion object {

        val visibilityThresholdRange = 0f..1f
        val presenceThresholdRange = 0f..1f

        val default = PoseLandmarkerSettings(
            modelType = PoseLandmarkerModelType.STANDARD,
            computeType = PoseLandmarkerComputeType.CPU,
            visibilityThreshold = 0.5f limits visibilityThresholdRange,
            presenceThreshold = 0.5f limits presenceThresholdRange,
        )
    }
}
