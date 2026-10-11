package com.exmachina.xvrtracker.common.net

import com.exmachina.xvrtracker.common.util.assertNotMainThread
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okio.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.nio.ByteBuffer
import java.nio.ByteOrder

class SimpleOscClient {

    private val _isSetupFlow = MutableStateFlow(false)
    val isSetupFlow = _isSetupFlow.asStateFlow()

    private val _isNetworkBrokenFlow = MutableStateFlow(false)
    val isNetworkBrokenFlow = _isNetworkBrokenFlow.asStateFlow()

    private var socket: DatagramSocket? = null
    private var address: InetAddress? = null
    private var port: Int? = null

    private val currentTime get() = System.currentTimeMillis()
    private var lastErrorTime = 0L
    private val coolDownTime = 1000L

    private val mutex = Mutex()

    suspend fun setup(
        address: InetAddress,
        port: Int,
    ) {
        assertNotMainThread()
        mutex.withLock {
            if (isSetupFlow.value) {
                internalTeardown()
            }

            this@SimpleOscClient.address = address
            this@SimpleOscClient.port = port
            socket = DatagramSocket()
            _isSetupFlow.value = true
        }
    }

    suspend fun setup(
        hostname: String,
        port: Int,
    ) {
        setup(
            address = InetAddress.getByName(hostname),
            port = port,
        )
    }

    suspend fun teardown() {
        assertNotMainThread()
        mutex.withLock {
            internalTeardown()
            _isSetupFlow.value = false
        }
    }

    private fun internalTeardown() {
        socket?.close()
        socket = null
        _isNetworkBrokenFlow.value = false
    }

    suspend fun send(
        path: String,
        params: List<Float>,
    ) {
        assertNotMainThread()
        mutex.withLock {
            if (!isSetupFlow.value)
                return@withLock

            if (isNetworkBrokenFlow.value) {
                if (currentTime - lastErrorTime < coolDownTime)
                    return@withLock

                _isNetworkBrokenFlow.value = false
                lastErrorTime = currentTime
            }

            if (socket == null) {
                socket = DatagramSocket()
            }

            try {
                val packetData = createOscPacket(
                    path,
                    params,
                )
                val packet = DatagramPacket(
                    packetData,
                    packetData.size,
                    address!!,
                    port!!,
                )
                socket!!.send(packet)
            } catch (_: IOException) {
                _isNetworkBrokenFlow.value = true
                lastErrorTime = currentTime
                socket?.close()
                socket = null
            }
        }
    }

    private fun oscPadString(str: String): ByteArray {
        val bytes = str.toByteArray(Charsets.UTF_8)
        val length = bytes.size + 1
        val padLength = (4 - (length % 4)) % 4
        val padded = ByteArray(length + padLength)
        System.arraycopy(bytes, 0, padded, 0, bytes.size)
        padded[bytes.size] = 0
        return padded
    }

    private fun createOscPacket(
        address: String,
        param: List<Float>,
    ): ByteArray {
        val addressBytes = oscPadString(address)
        val typeTag = "," + "f".repeat(param.size)
        val typeTagBytes = oscPadString(typeTag)
        val dataBuffer = ByteBuffer.allocate(4 * param.size)
        dataBuffer.order(ByteOrder.BIG_ENDIAN)
        for (f in param) {
            dataBuffer.putFloat(f)
        }
        return addressBytes + typeTagBytes + dataBuffer.array()
    }
}
