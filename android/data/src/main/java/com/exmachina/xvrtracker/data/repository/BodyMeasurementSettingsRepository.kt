package com.exmachina.xvrtracker.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.exmachina.xvrtracker.common.storage.cachedProperty
import com.exmachina.xvrtracker.common.storage.convert
import com.exmachina.xvrtracker.common.type.limits
import com.exmachina.xvrtracker.data.entity.BodyMeasurementsSettings
import com.exmachina.xvrtracker.data.entity.BodyMeasurementsSettings.Companion.bodyHeightRange
import com.exmachina.xvrtracker.data.entity.BodyMeasurementsSettings.Companion.partsOffsetRange
import com.exmachina.xvrtracker.data.entity.BodyMeasurementsSettings.Companion.partsRatioRange
import com.exmachina.xvrtracker.data.storage.AppSettings
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BodyMeasurementSettingsRepository @Inject constructor(
    @AppSettings dataStore: DataStore<Preferences>,
) {

    companion object {

        private const val PREFIX = "body_measurements"
    }

    val dataFlow
        get() = combine(
            bodyHeight,
            upperBodyHeightRatio,
            headHeightRatio,
            headZOffsetRatio,
            neckLengthRatio,
            shoulderWidthRatio,
            armLengthRatio,
            chestPositionRatio,
            waistPositionRatio,
            hipWidthRatio,
            ankleHeightRatio,
            footLengthRatio,
        ) {
            BodyMeasurementsSettings(
                bodyHeight = it[0],
                upperBodyHeightRatio = it[1],
                headHeightRatio = it[2],
                headZOffsetRatio = it[3],
                neckLengthRatio = it[4],
                shoulderWidthRatio = it[5],
                armLengthRatio = it[6],
                chestPositionRatio = it[7],
                waistPositionRatio = it[8],
                hipWidthRatio = it[9],
                ankleHeightRatio = it[10],
                footLengthRatio = it[11],
            )
        }

    val bodyHeight = dataStore.cachedProperty(
        prefix = PREFIX,
        key = "body_height",
        defaultValue = 1.75f,
        checkValid = { it in bodyHeightRange },
    ).convert(outputTransform = { it limits bodyHeightRange })

    val upperBodyHeightRatio = dataStore.cachedProperty(
        prefix = PREFIX,
        key = "upper_body_ratio",
        defaultValue = 1.0f,
        checkValid = { it in partsRatioRange },
    ).convert(outputTransform = { it limits partsRatioRange })

    val headHeightRatio = dataStore.cachedProperty(
        prefix = PREFIX,
        key = "head_height_ratio",
        defaultValue = 1.0f,
        checkValid = { it in partsRatioRange },
    ).convert(outputTransform = { it limits partsRatioRange })

    val headZOffsetRatio = dataStore.cachedProperty(
        prefix = PREFIX,
        key = "head_z_offset_ratio",
        defaultValue = 0f,
        checkValid = { it in partsOffsetRange },
    ).convert(outputTransform = { it limits partsOffsetRange })

    val neckLengthRatio = dataStore.cachedProperty(
        prefix = PREFIX,
        key = "neck_length_ratio",
        defaultValue = 1.0f,
        checkValid = { it in partsRatioRange },
    ).convert(outputTransform = { it limits partsRatioRange })

    val shoulderWidthRatio = dataStore.cachedProperty(
        prefix = PREFIX,
        key = "shoulder_width_ratio",
        defaultValue = 1.0f,
        checkValid = { it in partsRatioRange },
    ).convert(outputTransform = { it limits partsRatioRange })

    val armLengthRatio = dataStore.cachedProperty(
        prefix = PREFIX,
        key = "arm_length_ratio",
        defaultValue = 1.0f,
        checkValid = { it in partsRatioRange },
    ).convert(outputTransform = { it limits partsRatioRange })

    val chestPositionRatio = dataStore.cachedProperty(
        prefix = PREFIX,
        key = "chest_position_ratio",
        defaultValue = 1.0f,
        checkValid = { it in partsRatioRange },
    ).convert(outputTransform = { it limits partsRatioRange })

    val waistPositionRatio = dataStore.cachedProperty(
        prefix = PREFIX,
        key = "waist_position_ratio",
        defaultValue = 1.0f,
        checkValid = { it in partsRatioRange },
    ).convert(outputTransform = { it limits partsRatioRange })

    val hipWidthRatio = dataStore.cachedProperty(
        prefix = PREFIX,
        key = "hip_width_ratio",
        defaultValue = 1.0f,
        checkValid = { it in partsRatioRange },
    ).convert(outputTransform = { it limits partsRatioRange })

    val ankleHeightRatio = dataStore.cachedProperty(
        prefix = PREFIX,
        key = "ankle_height_ratio",
        defaultValue = 1.0f,
        checkValid = { it in partsRatioRange },
    ).convert(outputTransform = { it limits partsRatioRange })

    val footLengthRatio = dataStore.cachedProperty(
        prefix = PREFIX,
        key = "foot_length_ratio",
        defaultValue = 1.0f,
        checkValid = { it in partsRatioRange },
    ).convert(outputTransform = { it limits partsRatioRange })

    suspend fun preload() {
        dataFlow.first()
    }

    suspend fun reset() {
        upperBodyHeightRatio.reset()
        headHeightRatio.reset()
        headZOffsetRatio.reset()
        neckLengthRatio.reset()
        shoulderWidthRatio.reset()
        armLengthRatio.reset()
        chestPositionRatio.reset()
        waistPositionRatio.reset()
        hipWidthRatio.reset()
        ankleHeightRatio.reset()
        footLengthRatio.reset()
    }
}
