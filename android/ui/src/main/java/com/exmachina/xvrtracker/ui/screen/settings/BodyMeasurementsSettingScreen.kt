package com.exmachina.xvrtracker.ui.screen.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exmachina.xvrtracker.ui.content.settings.BodyMeasurementsSettingsContent
import com.exmachina.xvrtracker.ui.viewmodel.settings.BodyMeasurementsViewModel

@Composable
fun BodyMeasurementsSettingScreen(
    onBack: () -> Unit,
    viewModel: BodyMeasurementsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()

    BodyMeasurementsSettingsContent(
        uiState = uiState,
        onBack = onBack,
        onBodyHeightChange = viewModel::onBodyHeightUpdate,
        onUpperBodyHeightRatioChange = viewModel::onUpperBodyHeightRatioUpdate,
        onHeadHeightRationChange = viewModel::onHeadHeightRatioUpdate,
        onHeadZOffsetRatioChange = viewModel::onHeadZOffsetRatioUpdate,
        onNeckLengthRatioChange = viewModel::onNeckLengthRatioUpdate,
        onShoulderHeightRationChange = viewModel::onShoulderWightRatioUpdate,
        onArmLengthRatioChange = viewModel::onArmLengthRatioUpdate,
        onChestPositionRatioChange = viewModel::onChestPotionRatioUpdate,
        onWaistPositionRationChange = viewModel::onAbdomenPositionRatioUpdate,
        onHipWidthRationChange = viewModel::onHipWightRatioUpdate,
        onAnkleHeightChange = viewModel::onAnkleHeightRatioUpdate,
        onFootHeightChange = viewModel::onFootLengthRatioUpdate,
        onRatioReset = viewModel::onRatioReset,
    )
}
