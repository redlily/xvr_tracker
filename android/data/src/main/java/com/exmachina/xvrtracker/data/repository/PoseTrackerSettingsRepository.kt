package com.exmachina.xvrtracker.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.exmachina.xvrtracker.common.storage.cachedProperty
import com.exmachina.xvrtracker.common.storage.convert
import com.exmachina.xvrtracker.data.entity.PoseTrackerSettings
import com.exmachina.xvrtracker.data.storage.AppSettings
import com.exmachina.xvrtracker.data.type.PoseTrackerMotionType
import com.exmachina.xvrtracker.data.type.toPoseTrackerDataType
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PoseTrackerSettingsRepository
@Inject
constructor(
    @AppSettings dataStore: DataStore<Preferences>,
) {

    companion object {

        private const val PREFIX = "pose_tracker"
    }

    val dataFlow
        get() =
            combine(
                headTrackerDataType,
                chestTrackerDataType,
                waistTrackerDataType,
                elbowTrackerDataType,
                kneeTrackerDataType,
                feetTrackerDataType,
            ) {
                PoseTrackerSettings(
                    head = it[0],
                    chest = it[1],
                    waist = it[2],
                    elbow = it[3],
                    knee = it[4],
                    feet = it[5],
                )
            }

    val headTrackerDataType = dataStore.cachedProperty(
        prefix = PREFIX,
        key = "head_data_type",
        defaultValue = PoseTrackerSettings.default.head.id,
    ).convert(
        inputTransform = PoseTrackerMotionType::id,
        outputTransform = Int::toPoseTrackerDataType,
    )

    val chestTrackerDataType = dataStore.cachedProperty(
        prefix = PREFIX,
        key = "chest_data_type",
        defaultValue = PoseTrackerSettings.default.chest.id,
    ).convert(
        inputTransform = PoseTrackerMotionType::id,
        outputTransform = Int::toPoseTrackerDataType,
    )

    val waistTrackerDataType = dataStore.cachedProperty(
        prefix = PREFIX,
        key = "waist_data_type",
        defaultValue = PoseTrackerSettings.default.waist.id,
    ).convert(
        inputTransform = PoseTrackerMotionType::id,
        outputTransform = Int::toPoseTrackerDataType,
    )

    val elbowTrackerDataType = dataStore.cachedProperty(
        prefix = PREFIX,
        key = "elbow_data_type",
        defaultValue = PoseTrackerSettings.default.elbow.id,
    ).convert(
        inputTransform = PoseTrackerMotionType::id,
        outputTransform = Int::toPoseTrackerDataType,
    )

    val kneeTrackerDataType = dataStore.cachedProperty(
        prefix = PREFIX,
        key = "knee_data_type",
        defaultValue = PoseTrackerSettings.default.knee.id,
    ).convert(
        inputTransform = PoseTrackerMotionType::id,
        outputTransform = Int::toPoseTrackerDataType,
    )

    val feetTrackerDataType = dataStore.cachedProperty(
        prefix = PREFIX,
        key = "feet_data_type",
        defaultValue = PoseTrackerSettings.default.feet.id,
    ).convert(
        inputTransform = PoseTrackerMotionType::id,
        outputTransform = Int::toPoseTrackerDataType,
    )

    suspend fun preload() {
        dataFlow.first()
    }

    suspend fun reset() {
        headTrackerDataType.reset()
        chestTrackerDataType.reset()
        waistTrackerDataType.reset()
        elbowTrackerDataType.reset()
        kneeTrackerDataType.reset()
        feetTrackerDataType.reset()
    }
}
