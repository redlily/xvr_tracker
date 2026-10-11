package com.exmachina.xvrtracker.ui.state.settings

import com.exmachina.xvrtracker.data.entity.BodyMeasurementsSettings

data class BodyMeasurementsSettingsUiState(
    val content: Content = Content.Initializing,
) {

    sealed interface Content {
        data object Initializing : Content
        data class Initialized(
            val bodyMeasurementsSettings: BodyMeasurementsSettings,
        ) : Content
    }
}
