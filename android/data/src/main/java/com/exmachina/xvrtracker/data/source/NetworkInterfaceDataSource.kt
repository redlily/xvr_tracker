package com.exmachina.xvrtracker.data.source

import android.content.Context
import android.net.ConnectivityManager.NetworkCallback
import android.net.LinkProperties
import android.net.Network
import com.exmachina.xvrtracker.common.util.connectivityManager
import com.exmachina.xvrtracker.common.util.toImmutableSet
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkInterfaceDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {

    private val _networksFlow = MutableStateFlow<Set<Network>>(emptySet())
    val networksFlow = _networksFlow.asStateFlow()

    private val callback: NetworkCallback? = null

    fun start() {
        val networks = mutableSetOf<Network>()
        val cm = context.connectivityManager
        callback?.let { callback ->
            cm.unregisterNetworkCallback(callback)
        }
        val callback = object : NetworkCallback() {
            override fun onLinkPropertiesChanged(
                network: Network,
                linkProperties: LinkProperties,
            ) {
                Timber.d("onLinkPropertiesChanged: network=$network")
                networks += network
                _networksFlow.update { networks.toImmutableSet() }
            }

            override fun onLost(network: Network) {
                Timber.d("onLost: network=$network")
                networks -= network
                _networksFlow.update { networks.toImmutableSet() }
            }
        }
        cm.registerDefaultNetworkCallback(callback)
    }

    fun stop() {
        val cm = context.connectivityManager
        callback?.let { callback ->
            cm.registerDefaultNetworkCallback(callback)
        }
        _networksFlow.update { emptySet() }
    }
}
