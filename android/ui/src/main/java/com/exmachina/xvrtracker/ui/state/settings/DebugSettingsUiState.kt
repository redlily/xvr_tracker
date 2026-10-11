package com.exmachina.xvrtracker.ui.state.settings

import com.exmachina.xvrtracker.data.entity.AnalyzedDataPlotServerSettings

data class DebugSettingsUiState(
    val content: Content = Content.Initializing,
) {

    sealed interface Content {
        data object Initializing : Content

        data class Initialized(
            val analyzedDataPlotServerSettings: AnalyzedDataPlotServerSettings,
            val localIpAddress: String? = null,
        ) : Content
    }
}
