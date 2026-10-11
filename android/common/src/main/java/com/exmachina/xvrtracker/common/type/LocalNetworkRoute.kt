package com.exmachina.xvrtracker.common.type

import android.net.Network
import java.net.InetAddress

data class LocalNetworkRoute(
    val network: Network,
    val address: InetAddress,
    val prefix: Int,
)
