package com.exmachina.xvrtracker.domain.usecase

import android.content.Context
import android.net.NetworkCapabilities
import com.exmachina.xvrtracker.common.type.LocalNetworkRoute
import com.exmachina.xvrtracker.common.util.connectivityManager
import com.exmachina.xvrtracker.data.repository.NetworkStateRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GetPrivateNetworkRoutesUseCase @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val networkStateRepository: NetworkStateRepository,
) {

    operator fun invoke(): List<LocalNetworkRoute> {
        val cm = context.connectivityManager
        val linkProperties = networkStateRepository.networksFlow.value
            .filter { network ->
                val capabilities = cm.getNetworkCapabilities(network)
                capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true ||
                    capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true
            }

        return linkProperties
            .flatMap { network ->
                val linkProperties = cm.getLinkProperties(network)
                linkProperties?.linkAddresses?.map { linkAddress ->
                    LocalNetworkRoute(
                        network = network,
                        address = linkAddress.address,
                        prefix = linkAddress.prefixLength,
                    )
                } ?: emptyList()
            }
            .sortedWith { a, b ->
                val sizeDiff = a.address.address.size - b.address.address.size
                return@sortedWith if (sizeDiff != 0) sizeDiff else a.prefix - b.prefix
            }
    }
}
