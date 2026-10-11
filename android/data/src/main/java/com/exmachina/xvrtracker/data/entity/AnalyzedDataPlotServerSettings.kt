package com.exmachina.xvrtracker.data.entity

import androidx.compose.runtime.Immutable
import com.exmachina.xvrtracker.common.type.RangedValue
import com.exmachina.xvrtracker.common.type.limits
import com.exmachina.xvrtracker.common.util.toPort
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class AnalyzedDataPlotServerSettings(
    val isEnabled: Boolean,
    val port: RangedValue<Int>,
    val ping: RangedValue<Int>,
    val timeout: RangedValue<Int>,
) {

    companion object {

        val pingRange = 0..1000
        val timeoutRange = 0..10000

        val default = AnalyzedDataPlotServerSettings(
            isEnabled = false,
            port = 8000.toPort(),
            ping = 100 limits pingRange,
            timeout = 1000 limits timeoutRange,
        )
    }
}
