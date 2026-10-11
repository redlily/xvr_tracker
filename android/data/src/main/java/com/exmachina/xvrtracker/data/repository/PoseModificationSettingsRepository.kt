package com.exmachina.xvrtracker.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.exmachina.xvrtracker.common.storage.cachedProperty
import com.exmachina.xvrtracker.common.storage.convert
import com.exmachina.xvrtracker.common.type.RangedValue
import com.exmachina.xvrtracker.common.type.limits
import com.exmachina.xvrtracker.data.entity.PoseModificationSettings
import com.exmachina.xvrtracker.data.storage.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PoseModificationSettingsRepository @Inject constructor(
    @AppSettings dataStore: DataStore<Preferences>,
) {

    companion object {

        private const val PREFIX = "pose_modification"
    }

    @Suppress("UNCHECKED_CAST")
    val dataFlow: Flow<PoseModificationSettings>
        get() =
            combine(
                standStabilizingCoefficient,
                sitStabilizingCoefficient,
                lieDownStabilizingCoefficient,
                stabilizingModeChangeThresholdTime,
                isStabilizingModeChangeSoundEnabled,
                bodyPositiveSlope,
                bodyNegativeSlope,
                footPositiveSlope,
                footNegativeSlope,
                kneePositiveDirectionSlope,
                kneeNegativeDirectionSlope,
            ) {
                PoseModificationSettings(
                    standStabilizingCoefficient = it[0] as RangedValue<Float>,
                    sitStabilizingCoefficient = it[1] as RangedValue<Float>,
                    lieDownStabilizingCoefficient = it[2] as RangedValue<Float>,
                    stabilizingModeChangeThresholdTime = it[3] as RangedValue<Int>,
                    isStabilizingModeChangeSoundEnabled = it[4] as Boolean,
                    bodyPositiveSlope = it[5] as RangedValue<Float>,
                    bodyNegativeSlope = it[6] as RangedValue<Float>,
                    footPositiveSlope = it[7] as RangedValue<Float>,
                    footNegativeSlope = it[8] as RangedValue<Float>,
                    kneePositiveSlope = it[9] as RangedValue<Float>,
                    kneeNegativeSlope = it[10] as RangedValue<Float>,
                )
            }

    val standStabilizingCoefficient = dataStore.cachedProperty(
        prefix = PREFIX,
        key = "stand_stabilizing_coefficient",
        defaultValue = PoseModificationSettings.default.standStabilizingCoefficient.value,
        checkValid = { it in PoseModificationSettings.default.standStabilizingCoefficient.range },
    ).convert(outputTransform = { it limits PoseModificationSettings.default.standStabilizingCoefficient.range })

    val sitStabilizingCoefficient = dataStore.cachedProperty(
        prefix = PREFIX,
        key = "sit_stabilizing_coefficient",
        defaultValue = PoseModificationSettings.default.sitStabilizingCoefficient.value,
        checkValid = { it in PoseModificationSettings.default.sitStabilizingCoefficient.range },
    ).convert(outputTransform = { it limits PoseModificationSettings.default.sitStabilizingCoefficient.range })

    val lieDownStabilizingCoefficient = dataStore.cachedProperty(
        prefix = PREFIX,
        key = "lie_down_stabilizing_coefficient",
        defaultValue = PoseModificationSettings.default.lieDownStabilizingCoefficient.value,
        checkValid = { it in PoseModificationSettings.default.lieDownStabilizingCoefficient.range },
    ).convert(outputTransform = { it limits PoseModificationSettings.default.lieDownStabilizingCoefficient.range })

    val stabilizingModeChangeThresholdTime = dataStore.cachedProperty(
        prefix = PREFIX,
        key = "stabilizing_modeChange_threshold_time",
        defaultValue = PoseModificationSettings.default.stabilizingModeChangeThresholdTime.value,
        checkValid = { it in PoseModificationSettings.default.stabilizingModeChangeThresholdTime.range },
    ).convert(outputTransform = { it limits PoseModificationSettings.default.stabilizingModeChangeThresholdTime.range })

    val isStabilizingModeChangeSoundEnabled = dataStore.cachedProperty(
        prefix = PREFIX,
        key = "is_stabilizing_mode_change_sound_enabled",
        defaultValue = PoseModificationSettings.default.isStabilizingModeChangeSoundEnabled,
    )

    val bodyPositiveSlope = dataStore.cachedProperty(
        prefix = PREFIX,
        key = "body_positive_slope",
        defaultValue = PoseModificationSettings.default.bodyPositiveSlope.value,
    ).convert(outputTransform = { it limits PoseModificationSettings.default.bodyPositiveSlope.range })

    val bodyNegativeSlope = dataStore.cachedProperty(
        prefix = PREFIX,
        key = "body_negative_slope",
        defaultValue = PoseModificationSettings.default.bodyNegativeSlope.value,
    ).convert(outputTransform = { it limits PoseModificationSettings.default.bodyNegativeSlope.range })

    val kneePositiveDirectionSlope = dataStore.cachedProperty(
        prefix = PREFIX,
        key = "knee_positive_direction_slope",
        defaultValue = PoseModificationSettings.default.kneePositiveSlope.value,
    ).convert(outputTransform = { it limits PoseModificationSettings.default.kneePositiveSlope.range })

    val kneeNegativeDirectionSlope = dataStore.cachedProperty(
        prefix = PREFIX,
        key = "knee_negative_direction_slope",
        defaultValue = PoseModificationSettings.default.kneeNegativeSlope.value,
    ).convert(outputTransform = { it limits PoseModificationSettings.default.kneeNegativeSlope.range })

    val footPositiveSlope = dataStore.cachedProperty(
        prefix = PREFIX,
        key = "foot_positive_slope",
        defaultValue = PoseModificationSettings.default.footPositiveSlope.value,
    ).convert(outputTransform = { it limits PoseModificationSettings.default.footPositiveSlope.range })

    val footNegativeSlope = dataStore.cachedProperty(
        prefix = PREFIX,
        key = "foot_negative_slope",
        defaultValue = PoseModificationSettings.default.footNegativeSlope.value,
    ).convert(outputTransform = { it limits PoseModificationSettings.default.footNegativeSlope.range })

    suspend fun preload() {
        dataFlow.first()
    }

    suspend fun reset() {
        standStabilizingCoefficient.reset()
        sitStabilizingCoefficient.reset()
        lieDownStabilizingCoefficient.reset()
        stabilizingModeChangeThresholdTime.reset()
        isStabilizingModeChangeSoundEnabled.reset()
        bodyPositiveSlope.reset()
        bodyNegativeSlope.reset()
        footPositiveSlope.reset()
        footNegativeSlope.reset()
        kneePositiveDirectionSlope.reset()
        kneeNegativeDirectionSlope.reset()
    }
}
