package com.exmachina.xvrtracker.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.exmachina.xvrtracker.common.storage.cachedProperty
import com.exmachina.xvrtracker.common.storage.convert
import com.exmachina.xvrtracker.data.entity.CameraSettings
import com.exmachina.xvrtracker.data.storage.AppSettings
import com.exmachina.xvrtracker.data.type.CameraLensFacingType
import com.exmachina.xvrtracker.data.type.toCameraLensFacingType
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CameraSettingsRepository @Inject constructor(
    @AppSettings dataStore: DataStore<Preferences>,
) {

    companion object {

        private const val PREFIX = "camera"
    }

    val dataFlow
        get() =
            combine(
                lensFacingType,
                zoomRatio,
            ) {
                    lensFacingType,
                    zoomRatio,
                ->
                CameraSettings(
                    lensFacingType = lensFacingType,
                    zoomRatio = zoomRatio,
                )
            }

    val lensFacingType = dataStore.cachedProperty(
        prefix = PREFIX,
        key = "lens_facing_type",
        defaultValue = CameraLensFacingType.BACK.id,
    ).convert(
        inputTransform = CameraLensFacingType::id,
        outputTransform = Int::toCameraLensFacingType,
    )

    val zoomRatio = dataStore.cachedProperty(
        prefix = PREFIX,
        key = "zoom_ratio",
        defaultValue = 1f,
    )

    suspend fun preload() {
        dataFlow.first()
    }

    suspend fun reset() {
        lensFacingType.reset()
        zoomRatio.reset()
    }
}
