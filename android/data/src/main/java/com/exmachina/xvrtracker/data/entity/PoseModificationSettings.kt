package com.exmachina.xvrtracker.data.entity

import androidx.compose.runtime.Immutable
import com.exmachina.xvrtracker.common.type.RangedValue
import com.exmachina.xvrtracker.common.type.limits

@Immutable
data class PoseModificationSettings(
    val standStabilizingCoefficient: RangedValue<Float>,
    val sitStabilizingCoefficient: RangedValue<Float>,
    val lieDownStabilizingCoefficient: RangedValue<Float>,
    val stabilizingModeChangeThresholdTime: RangedValue<Int>,
    val isStabilizingModeChangeSoundEnabled: Boolean,
    val bodyPositiveSlope: RangedValue<Float>,
    val bodyNegativeSlope: RangedValue<Float>,
    val footPositiveSlope: RangedValue<Float>,
    val footNegativeSlope: RangedValue<Float>,
    val kneePositiveSlope: RangedValue<Float>,
    val kneeNegativeSlope: RangedValue<Float>,
) {

    companion object {

        val stabilizingCoefficientRange = 0f..1f
        val stabilizingModeChangeThresholdTimeRange = 0..5000
        val bodySlopeRange = -30f..30f
        val kneeSlopeRange = -30f..30f
        val footSlopeRange = -60f..60f

        val default = PoseModificationSettings(
            standStabilizingCoefficient = 0.05f limits stabilizingCoefficientRange,
            sitStabilizingCoefficient = 0.15f limits stabilizingCoefficientRange,
            lieDownStabilizingCoefficient = 0.5f limits stabilizingCoefficientRange,
            stabilizingModeChangeThresholdTime = 500 limits stabilizingModeChangeThresholdTimeRange,
            isStabilizingModeChangeSoundEnabled = true,
            bodyPositiveSlope = 12f limits bodySlopeRange,
            bodyNegativeSlope = 0f limits bodySlopeRange,
            footPositiveSlope = 0f limits footSlopeRange,
            footNegativeSlope = 30f limits footSlopeRange,
            kneePositiveSlope = 17f limits kneeSlopeRange,
            kneeNegativeSlope = 0f limits kneeSlopeRange,
        )
    }
}
