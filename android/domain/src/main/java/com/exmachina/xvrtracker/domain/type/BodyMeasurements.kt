package com.exmachina.xvrtracker.domain.type

import androidx.compose.runtime.Immutable
import com.exmachina.xvrtracker.data.entity.BodyMeasurementsSettings

@Immutable
data class BodyMeasurements(
    val bodyHeight: Float,
    val upperBodyHeight: Float,
    val headHeight: Float,
    val headZOffset: Float,
    val neckLength: Float,
    val headToShoulderLength: Float,
    val shoulderWidth: Float,
    val armLength: Float,
    val shoulderToChestLength: Float,
    val chestToWaistLength: Float,
    val waistToHipLength: Float,
    val hipWidth: Float,
    val legLength: Float,
    val ankleToHeelLength: Float,
    val footLength: Float,
) {

    companion object {

        val default = make(bodyHeight = 1.78f)

        fun make(
            bodyHeight: Float,
            upperBodyHeightRatio: Float = 1f,
            headHeightRatio: Float = 1f,
            headZOffsetRatio: Float = 1f,
            neckLengthRatio: Float = 1f,
            shoulderWidthRatio: Float = 1f,
            armLengthRatio: Float = 1f,
            chestPositionRatio: Float = 1f,
            waistPositionRatio: Float = 1f,
            hipWidthRatio: Float = 1f,
            anklePositionRatio: Float = 1f,
            footLengthRatio: Float = 1f,
        ): BodyMeasurements {
            // upper body
            val upperBodyHeight = bodyHeight * 0.5f * upperBodyHeightRatio

            // head parts
            val headHeight = upperBodyHeight * (4f / 14f) * headHeightRatio * headHeightRatio
            val headZOffset = headHeight * headZOffsetRatio
            val neckLength = upperBodyHeight * (1f / 14f) * headHeightRatio * neckLengthRatio
            val headToShoulderLength = headHeight / 2f + neckLength

            // body and arm parts
            val shoulderWidth = upperBodyHeight * 0.40f * shoulderWidthRatio
            val shoulderToHipLength = upperBodyHeight - headHeight - neckLength
            val armLength = shoulderToHipLength * (7f / 8f) * armLengthRatio
            val shoulderToChestLength = shoulderToHipLength * (1f / 8f) * chestPositionRatio
            val waistToHipLength = shoulderToHipLength * (1f / 8f) * waistPositionRatio
            val chestToWaistLength = shoulderToHipLength - shoulderToChestLength - waistToHipLength

            // lower body
            val lowerBodyHeight = bodyHeight - upperBodyHeight
            val hipWidth = upperBodyHeight * 0.30f * hipWidthRatio
            val ankleToHeelLength = lowerBodyHeight * (1f / 8f) * anklePositionRatio
            val legLength = lowerBodyHeight - ankleToHeelLength
            val footLength = ankleToHeelLength * 2.4f * footLengthRatio

            return BodyMeasurements(
                bodyHeight = bodyHeight,
                upperBodyHeight = upperBodyHeight,
                headHeight = headHeight,
                headZOffset = headZOffset,
                neckLength = neckLength,
                headToShoulderLength = headToShoulderLength,
                shoulderWidth = shoulderWidth,
                armLength = armLength,
                shoulderToChestLength = shoulderToChestLength,
                chestToWaistLength = chestToWaistLength,
                waistToHipLength = waistToHipLength,
                hipWidth = hipWidth,
                legLength = legLength,
                ankleToHeelLength = ankleToHeelLength,
                footLength = footLength,
            )
        }
    }

    val hipToShoulderLength: Float =
        waistToHipLength + chestToWaistLength + shoulderToChestLength
}

fun BodyMeasurementsSettings.bodyMeasurements(
    bodyHeight: Float = this.bodyHeight.value,
    upperBodyHeightRatio: Float = this.upperBodyHeightRatio.value,
    headHeightRatio: Float = this.headHeightRatio.value,
    headZOffsetRatio: Float = this.headZOffsetRatio.value,
    neckLengthRatio: Float = this.neckLengthRatio.value,
    shoulderWidthRatio: Float = this.shoulderWidthRatio.value,
    armLengthRatio: Float = this.armLengthRatio.value,
    chestPositionRatio: Float = this.chestPositionRatio.value,
    waistPositionRatio: Float = this.waistPositionRatio.value,
    hipWidthRatio: Float = this.hipWidthRatio.value,
    ankleHeightRatio: Float = this.ankleHeightRatio.value,
    footLengthRatio: Float = this.footLengthRatio.value,
) = BodyMeasurements.make(
    bodyHeight = bodyHeight,
    upperBodyHeightRatio = upperBodyHeightRatio,
    headHeightRatio = headHeightRatio,
    headZOffsetRatio = headZOffsetRatio,
    neckLengthRatio = neckLengthRatio,
    shoulderWidthRatio = shoulderWidthRatio,
    armLengthRatio = armLengthRatio,
    chestPositionRatio = chestPositionRatio,
    waistPositionRatio = waistPositionRatio,
    hipWidthRatio = hipWidthRatio,
    anklePositionRatio = ankleHeightRatio,
    footLengthRatio = footLengthRatio,
)
