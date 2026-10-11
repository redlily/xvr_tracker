package com.exmachina.xvrtracker.ui.screen.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.exmachina.xvrtracker.ui.content.settings.PoseTrackerSettingsContent
import com.exmachina.xvrtracker.ui.viewmodel.settings.PoseTrackerSettingsViewModel

@Composable
fun PoseTrackerSettingsScreen(
    onBack: () -> Unit,
    viewModel: PoseTrackerSettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsState()

    PoseTrackerSettingsContent(
        uiState = uiState,
        onBack = onBack,
        onHeadTrackerDataTypeUpdate = viewModel::onHeadTrackerDataTypeUpdate,
        onElbowTrackerDataTypeUpdate = viewModel::onElbowTrackerDataTypeUpdate,
        onChestTrackerDataTypeUpdate = viewModel::onChestTrackerDataTypeUpdate,
        onWaistTrackerDataTypeUpdate = viewModel::onWaistTrackerDataTypeUpdate,
        onKneeTrackerDataTypeUpdate = viewModel::onKneeTrackerDataTypeUpdate,
        onFeetTrackerDataTypeUpdate = viewModel::onFeetTrackerDataTypeUpdate,
        onPresetTrackingSettingsChose = viewModel::onPresetTrackingSettingsChose,
    )
}
