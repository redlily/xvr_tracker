package com.exmachina.xvrtracker.ui.state.settings

import com.exmachina.xvrtracker.data.entity.PoseTrackerSettings

data class PoseTrackerSettingsUiState(
    val content: Content = Content.Initializing,
) {

    sealed interface Content {
        data object Initializing : Content
        data class Initialized(
            val poseTrackerSettings: PoseTrackerSettings,
        ) : Content
    }
}
