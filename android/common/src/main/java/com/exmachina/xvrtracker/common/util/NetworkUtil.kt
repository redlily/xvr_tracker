package com.exmachina.xvrtracker.common.util

import android.content.Context
import android.net.ConnectivityManager.NetworkCallback
import android.net.InetAddresses
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Patterns
import com.exmachina.xvrtracker.common.type.LocalNetworkRoute
import com.exmachina.xvrtracker.common.type.limits
import kotlinx.coroutines.suspendCancellableCoroutine
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress
import java.net.UnknownHostException
import kotlin.coroutines.resume
import kotlin.experimental.and

fun isValidIpAddress(value: String) = InetAddresses.isNumericAddress(value)

fun isValidDomain(value: String) = Patterns.DOMAIN_NAME.matcher(value).matches()

fun isValidIpAddressOrDomain(value: String) = isValidIpAddress(value) || isValidDomain(value)

val portRange = 0..65535

fun isValidIpPort(value: Int) = value in portRange

fun Int.toPort() = this limits portRange

fun isPrivateIpAddress(address: InetAddress): Boolean {
    if (address is Inet4Address) {
        val octets = address.address
        val o0 = octets[0].toInt() and 0xFF
        val o1 = octets[1].toInt() and 0xFF
        return (o0 == 192 && o1 == 168) ||
            (o0 == 172 && o1 in 16..31) ||
            (o0 == 10)
    } else if (address is Inet6Address) {
        val octets = address.address
        val o0 = octets[0].toInt() and 0xFF
        val o1 = octets[1].toInt() and 0xFF
        return ((o0 == 0xfe) && ((o1 and 0xc0) == 0x80)) ||
            ((o0 and 0xfe) == 0xfc)
    }
    return false
}

fun isSameNetwork(
    a: InetAddress,
    b: InetAddress,
    prefix: Int,
): Boolean {
    val aa = a.address
    val bb = b.address
    if (aa.size != bb.size) return false
    var remain = prefix
    for (i in aa.indices) {
        if (remain >= 8) {
            if (aa[i] != bb[i]) return false
            remain -= 8
        } else if (remain > 0) {
            val mask = (0xff shl (8 - remain)).toByte()
            if ((aa[i] and mask) != (bb[i] and mask)) return false
            remain = 0
        } else {
            break
        }
    }
    return true
}

fun resolveIpAddress(host: String): InetAddress? =
    try {
        InetAddress.getByName(host)
    } catch (_: UnknownHostException) {
        null
    }

suspend fun Context.getLocalAddresses(): List<LocalNetworkRoute> {
    return suspendCancellableCoroutine { cont ->
        val callback = object : NetworkCallback() {
            override fun onLinkPropertiesChanged(
                network: Network,
                linkProperties: LinkProperties,
            ) {
                val info = connectivityManager.getNetworkCapabilities(network)?.let { capabilities ->
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                    ) {
                        linkProperties.linkAddresses.mapNotNull { linkAddress ->
                            if (isPrivateIpAddress(linkAddress.address)) {
                                LocalNetworkRoute(
                                    network = network,
                                    address = linkAddress.address,
                                    prefix = linkAddress.prefixLength,
                                )
                            } else {
                                null
                            }
                        }.sortedWith { a, b ->
                            if (a.address.address.size == b.address.address.size) {
                                return@sortedWith a.address.address.size - b.address.address.size
                            } else {
                                return@sortedWith a.prefix - b.prefix
                            }
                        }
                    } else {
                        null
                    }
                }

                cont.resume(info ?: emptyList())
                connectivityManager.unregisterNetworkCallback(this)
            }

            override fun onUnavailable() {
                cont.resume(emptyList())
                connectivityManager.unregisterNetworkCallback(this)
            }
        }
        connectivityManager.registerDefaultNetworkCallback(callback)
        cont.invokeOnCancellation {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }
}

fun makeIpPrefix(
    address: InetAddress,
    prefix: Int,
): String =
    when (address) {
        is Inet4Address -> makeIpv4Prefix(address, prefix)
        is Inet6Address -> makeIpv6Previs(address, prefix)
        else -> ""
    }

private fun makeIpv4Prefix(
    address: Inet4Address,
    prefix: Int,
): String {
    if (prefix <= 0 || prefix > 32) return ""
    val data = address.address
    val stringBuilder = StringBuilder()
    for (i in 0 until prefix / 8) {
        stringBuilder.append(0xff and data[i].toInt())
        stringBuilder.append('.')
    }
    return stringBuilder.toString()
}

private fun makeIpv6Previs(
    address: Inet6Address,
    prefix: Int,
): String {
    if (prefix <= 0 || prefix > 128) return ""
    val data = address.address
    val stringBuilder = StringBuilder()
    for (i in 0 until prefix / 16) {
        val high = data[i * 2].toInt() and 0xff
        val low = data[i * 2 + 1].toInt() and 0xff
        val block = (high shl 8) or low
        stringBuilder.append(block.toString(16))
        stringBuilder.append(':')
    }
    return stringBuilder.toString()
}
