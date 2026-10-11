package com.exmachina.xvrtracker.data.entity

import androidx.compose.runtime.Immutable
import com.exmachina.xvrtracker.common.type.RangedValue
import com.exmachina.xvrtracker.common.util.toPort
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class VrcConnectionSettings(
    val ipOrDomain: String,
    val port: RangedValue<Int>,
) {

    companion object {

        val default = VrcConnectionSettings(
            ipOrDomain = "192.168.10.1",
            port = 9000.toPort(),
        )
    }
}
