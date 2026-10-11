package com.exmachina.xvrtracker.data.type

import android.net.Network

data class NetworkInterface(
    val network: Network,
    val name: String? = null,
)

