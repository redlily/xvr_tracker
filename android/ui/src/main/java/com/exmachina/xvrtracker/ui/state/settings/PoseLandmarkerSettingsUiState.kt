package com.exmachina.xvrtracker.ui.state.settings

import com.exmachina.xvrtracker.data.entity.PoseLandmarkerSettings

data class PoseLandmarkerSettingsUiState(
    val content: Content = Content.Initializing,
) {

    sealed interface Content {
        data object Initializing : Content
        data class Initialized(
            val poseLandmarkerSettings: PoseLandmarkerSettings,
        ) : Content
    }
}
