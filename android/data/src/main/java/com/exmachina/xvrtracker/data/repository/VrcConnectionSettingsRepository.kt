package com.exmachina.xvrtracker.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.exmachina.xvrtracker.common.storage.cachedProperty
import com.exmachina.xvrtracker.common.storage.convert
import com.exmachina.xvrtracker.common.type.limits
import com.exmachina.xvrtracker.common.util.isValidIpAddressOrDomain
import com.exmachina.xvrtracker.common.util.isValidIpPort
import com.exmachina.xvrtracker.data.entity.VrcConnectionSettings
import com.exmachina.xvrtracker.data.storage.AppSettings
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VrcConnectionSettingsRepository @Inject constructor(
    @AppSettings dataStore: DataStore<Preferences>,
) {

    companion object {

        private const val PREFIX = "vrc_connection"
    }

    val dataFlow
        get() = combine(
            sendingIpOrDomain,
            sendingPort,
        ) {
                ipOrDomain,
                port,
            ->
            VrcConnectionSettings(
                ipOrDomain = ipOrDomain,
                port = port,
            )
        }

    val sendingIpOrDomain = dataStore.cachedProperty(
        prefix = PREFIX,
        key = "sending_ip_or_domain",
        defaultValue = "",
        checkValid = { isValidIpAddressOrDomain(it) },
    )

    val sendingPort = dataStore.cachedProperty(
        prefix = PREFIX,
        key = "sending_port",
        defaultValue = 9000,
        checkValid = { isValidIpPort(it) },
    ).convert(
        outputTransform = { it limits 0..65535 },
    )

    suspend fun preload() {
        dataFlow.first()
    }

    suspend fun reset() {
        sendingIpOrDomain.reset()
        sendingPort.reset()
    }
}
