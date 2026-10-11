package com.exmachina.xvrtracker.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.exmachina.xvrtracker.common.storage.cachedProperty
import com.exmachina.xvrtracker.common.storage.convert
import com.exmachina.xvrtracker.common.type.limits
import com.exmachina.xvrtracker.data.entity.AnalyzedDataPlotServerSettings
import com.exmachina.xvrtracker.data.storage.AppSettings
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyzedDataPlotServerSettingsRepository @Inject constructor(
    @AppSettings dataStore: DataStore<Preferences>,
) {

    companion object {

        private const val PREFIX = "analyzed_data_server"
    }

    val dataFlow
        get() = combine(
            isEnabled,
            port,
            ping,
            timeout,
        ) {
                isEnabled,
                port,
                ping,
                timeout,
            ->
            AnalyzedDataPlotServerSettings(
                isEnabled = isEnabled,
                port = port,
                ping = ping,
                timeout = timeout,
            )
        }

    val isEnabled = dataStore.cachedProperty(
        prefix = PREFIX,
        key = "isEnabled",
        defaultValue = false,
    )

    val port = dataStore.cachedProperty(
        prefix = PREFIX,
        key = "port",
        defaultValue = 8000,
        checkValid = { it in 0..65535 },
    ).convert(outputTransform = { it limits 0..65535 })

    val ping = dataStore.cachedProperty(
        prefix = PREFIX,
        key = "ping",
        defaultValue = 15,
        checkValid = { it in 0..600 },
    ).convert(outputTransform = { it limits 0..600 })

    val timeout = dataStore.cachedProperty(
        prefix = PREFIX,
        key = "timeout",
        defaultValue = 30,
        checkValid = { it in 0..600 },
    ).convert(outputTransform = { it limits 0..600 })

    suspend fun preload() {
        dataFlow.first()
    }

    suspend fun reset() {
        isEnabled.reset()
        port.reset()
        ping.reset()
        timeout.reset()
    }
}
