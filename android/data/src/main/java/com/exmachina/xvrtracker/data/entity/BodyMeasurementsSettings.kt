package com.exmachina.xvrtracker.data.entity

import androidx.compose.runtime.Immutable
import com.exmachina.xvrtracker.common.type.RangedValue
import com.exmachina.xvrtracker.common.type.limits
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class BodyMeasurementsSettings(
    val bodyHeight: RangedValue<Float>,
    val upperBodyHeightRatio: RangedValue<Float>,
    val headHeightRatio: RangedValue<Float>,
    val headZOffsetRatio: RangedValue<Float>,
    val neckLengthRatio: RangedValue<Float>,
    val shoulderWidthRatio: RangedValue<Float>,
    val armLengthRatio: RangedValue<Float>,
    val chestPositionRatio: RangedValue<Float>,
    val waistPositionRatio: RangedValue<Float>,
    val hipWidthRatio: RangedValue<Float>,
    val ankleHeightRatio: RangedValue<Float>,
    val footLengthRatio: RangedValue<Float>,
) {

    companion object {

        val bodyHeightRange = 1.0f..2.5f
        val partsRatioRange = 0.5f..1.5f
        val partsOffsetRange = -1f..1f

        val default = BodyMeasurementsSettings(
            bodyHeight = 1.78f limits bodyHeightRange,
            upperBodyHeightRatio = 1f limits partsRatioRange,
            headHeightRatio = 1f limits partsRatioRange,
            headZOffsetRatio = 1f limits partsOffsetRange,
            neckLengthRatio = 1f limits partsRatioRange,
            shoulderWidthRatio = 1f limits partsRatioRange,
            armLengthRatio = 1f limits partsRatioRange,
            chestPositionRatio = 1f limits partsRatioRange,
            waistPositionRatio = 1f limits partsRatioRange,
            hipWidthRatio = 1f limits partsRatioRange,
            ankleHeightRatio = 1f limits partsRatioRange,
            footLengthRatio = 1f limits partsRatioRange,
        )
    }
}
