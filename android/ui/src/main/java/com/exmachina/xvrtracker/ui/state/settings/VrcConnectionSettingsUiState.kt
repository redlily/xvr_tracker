package com.exmachina.xvrtracker.ui.state.settings

import com.exmachina.xvrtracker.common.type.RangedValue

data class VrcConnectionSettingsUiState(
    val defaultSendingIpPrefix: String = "",
    val content: Content = Content.Initializing,
) {

    sealed interface Content {
        data object Initializing : Content
        data class Initialized(
            val sendingIpOrDomain: String,
            val sendingPort: RangedValue<Int>,
        ) : Content
    }
}
