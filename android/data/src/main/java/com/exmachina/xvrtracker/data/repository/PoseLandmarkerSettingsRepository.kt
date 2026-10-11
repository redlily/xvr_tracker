package com.exmachina.xvrtracker.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.exmachina.xvrtracker.common.storage.cachedProperty
import com.exmachina.xvrtracker.common.storage.convert
import com.exmachina.xvrtracker.common.type.limits
import com.exmachina.xvrtracker.data.entity.PoseLandmarkerSettings
import com.exmachina.xvrtracker.data.storage.AppSettings
import com.exmachina.xvrtracker.data.type.PoseLandmarkerComputeType
import com.exmachina.xvrtracker.data.type.PoseLandmarkerModelType
import com.exmachina.xvrtracker.data.type.toPoseLandmarkAnalyzerModel
import com.exmachina.xvrtracker.data.type.toPoseLandmarkerComputeType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PoseLandmarkerSettingsRepository
@Inject
constructor(
    @AppSettings dataStore: DataStore<Preferences>,
) {

    companion object {

        private const val PREFIX = "pose_landmarker"
    }

    val dataFlow: Flow<PoseLandmarkerSettings>
        get() =
            combine(
                modelType,
                computeType,
                visibilityThreshold,
                presenceThreshold,
            ) {
                    modelType,
                    computeType,
                    visibilityThreshold,
                    presenceThreshold,
                ->
                PoseLandmarkerSettings(
                    modelType = modelType,
                    computeType = computeType,
                    visibilityThreshold = visibilityThreshold,
                    presenceThreshold = presenceThreshold,
                )
            }

    val modelType = dataStore.cachedProperty(
        prefix = PREFIX,
        key = "model_type",
        defaultValue = PoseLandmarkerSettings.default.modelType.id,
    ).convert(
        inputTransform = PoseLandmarkerModelType::id,
        outputTransform = Int::toPoseLandmarkAnalyzerModel,
    )

    val computeType = dataStore.cachedProperty(
        prefix = PREFIX,
        key = "compute_type",
        defaultValue = PoseLandmarkerSettings.default.computeType.id,
    ).convert(
        inputTransform = PoseLandmarkerComputeType::id,
        outputTransform = Int::toPoseLandmarkerComputeType,
    )

    val visibilityThreshold = dataStore.cachedProperty(
        prefix = PREFIX,
        key = "visibility_threshold",
        defaultValue = PoseLandmarkerSettings.default.visibilityThreshold.value,
    ).convert(
        outputTransform = { it limits PoseLandmarkerSettings.default.visibilityThreshold.range },
    )

    val presenceThreshold = dataStore.cachedProperty(
        prefix = PREFIX,
        key = "presence_threshold",
        defaultValue = PoseLandmarkerSettings.default.presenceThreshold.value,
    ).convert(
        outputTransform = { it limits PoseLandmarkerSettings.default.presenceThreshold.range },
    )

    suspend fun preload() {
        dataFlow.first()
    }

    suspend fun reset() {
        modelType.reset()
        computeType.reset()
        visibilityThreshold.reset()
        presenceThreshold.reset()
    }
}
