package com.exmachina.xvrtracker.common

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.exmachina.xvrtracker.common.util.resolveIpAddress
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NetworkUtilTest {

    @Test
    fun testResolveIpAddressByHostName() {
        val ipAddress = resolveIpAddress("example.com")
        assert(ipAddress != null)
    }

    @Test
    fun testResolveIpAddressByWrongHostName() {
        val ipAddress = resolveIpAddress("aaa.example.com")
        assert(ipAddress == null)
    }

    @Test
    fun testResolveIpAddressByAddress() {
        val ipAddress = resolveIpAddress("192.168.0.1")
        assert(ipAddress != null)
    }

    @Test
    fun testResolveIpAddressByWrongAddress() {
        val ipAddress = resolveIpAddress("192.168.0.")
        assert(ipAddress == null)
    }
}
