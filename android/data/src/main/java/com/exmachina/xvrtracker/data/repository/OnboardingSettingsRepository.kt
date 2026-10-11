package com.exmachina.xvrtracker.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.exmachina.xvrtracker.common.storage.cachedProperty
import com.exmachina.xvrtracker.data.entity.OnboardingSettings
import com.exmachina.xvrtracker.data.storage.AppSettings
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnboardingSettingsRepository @Inject constructor(
    @AppSettings dataStore: DataStore<Preferences>,
) {

    companion object {

        private const val PREFIX = "onboarding"
    }

    val dataFlow
        get() = combine(
            done,
        ) {
            OnboardingSettings(
                done = it[0],
            )
        }

    val done = dataStore.cachedProperty(
        prefix = PREFIX,
        key = "done",
        defaultValue = false,
    )

    suspend fun preload() {
        dataFlow.first()
    }

    suspend fun reset() {
        done.reset()
    }
}
