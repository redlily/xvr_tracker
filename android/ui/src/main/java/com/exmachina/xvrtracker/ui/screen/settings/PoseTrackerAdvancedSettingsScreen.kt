package com.exmachina.xvrtracker.ui.screen.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exmachina.xvrtracker.ui.content.settings.PoseTrackerAdvancedSettingsContent
import com.exmachina.xvrtracker.ui.viewmodel.settings.PoseTrackerAdvancedSettingsViewModel

@Composable
fun PoseTrackerAdvancedSettingsScreen(
    onBack: () -> Unit,
    viewModel: PoseTrackerAdvancedSettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()

    PoseTrackerAdvancedSettingsContent(
        uiState = uiState,
        onBack = onBack,
        onStandStabilizingCoefficientChange = viewModel::onStandStabilizingCoefficientChange,
        onSitStabilizingCoefficientChange = viewModel::onSitStabilizingCoefficientChange,
        onLieDownStabilizingCoefficientChange = viewModel::onLieDownStabilizingCoefficientChange,
        onStabilizingModeChangeThresholdTimeChange = viewModel::onStabilizingModeChangeThresholdTimeChange,
        onStabilizingModeChangeSoundEnabledChange = viewModel::onStabilizingModeChangeSoundEnabledChange,
        onBodyPositiveSlopeChange = viewModel::onBodyPositiveSlopeChange,
        onBodyNegativeSlopeChange = viewModel::onBodyNegativeSlopeChange,
        onKneePositiveSlopeChange = viewModel::onKneePositiveSlopeChange,
        onKneeNegativeSlopeChange = viewModel::onKneeNegativeSlopeChange,
        onFootPositiveSlopeChange = viewModel::onFootPositiveSlopeChange,
        onFootNegativeSlopeChange = viewModel::onFootNegativeSlopeChange,
    )
}
