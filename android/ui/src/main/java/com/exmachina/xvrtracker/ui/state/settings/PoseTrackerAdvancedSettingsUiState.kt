package com.exmachina.xvrtracker.ui.state.settings

import com.exmachina.xvrtracker.data.entity.PoseModificationSettings

data class PoseTrackerAdvancedSettingsUiState(
    val content: Content = Content.Initializing,
) {

    sealed interface Content {
        data object Initializing : Content
        data class Initialized(
            val poseModificationSettings: PoseModificationSettings,
        ) : Content
    }
}
