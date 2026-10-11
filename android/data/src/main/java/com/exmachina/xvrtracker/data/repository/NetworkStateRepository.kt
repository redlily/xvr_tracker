package com.exmachina.xvrtracker.data.repository

import android.net.Network
import com.exmachina.xvrtracker.data.source.NetworkInterfaceDataSource
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkStateRepository @Inject constructor(
    dataSource: NetworkInterfaceDataSource,
) {

    val networksFlow: StateFlow<Set<Network>> = dataSource.networksFlow
}
