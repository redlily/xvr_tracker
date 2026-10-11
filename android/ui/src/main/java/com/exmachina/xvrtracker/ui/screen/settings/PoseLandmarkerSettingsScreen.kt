package com.exmachina.xvrtracker.ui.screen.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.exmachina.xvrtracker.ui.content.settings.PoseLandmarkerSettingsContent
import com.exmachina.xvrtracker.ui.viewmodel.settings.PoseLandmarkerSettingsViewModel

@Composable
fun PoseLandmarkerSettingsScreen(
    onBack: () -> Unit,
    viewModel: PoseLandmarkerSettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsState()

    PoseLandmarkerSettingsContent(
        uiState = uiState,
        onBack = onBack,
        onPoseLandmarkerModelTypeUpdate = viewModel::onPoseLandmarkerModelTypeUpdate,
        onPoseLandmarkerComputeTypeUpdate = viewModel::onPoseLandmarkerComputeTypeUpdate,
        onPoseLandmarkerVisibilityThresholdUpdate = viewModel::onPoseLandmarkerVisibilityThresholdUpdate,
        onPoseLandmarkerPresenceThresholdUpdate = viewModel::onPoseLandmarkerPresenceThresholdUpdate,
    )
}
