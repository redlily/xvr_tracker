package com.exmachina.xvrtracker.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exmachina.xvrtracker.ui.content.TopContent
import com.exmachina.xvrtracker.ui.viewmodel.settings.TopViewModel

@Composable
fun TopScreen(
    onVrcConnectionSettingsClick: () -> Unit,
    onBodyMeasurementsSettingsClick: () -> Unit,
    onPoseAnalyzerSettingsClick: () -> Unit,
    onPoseTrackerSettingsClick: () -> Unit,
    onPoseTrackerAdvancedSettingsClick: () -> Unit,
    onDebugSettingsClick: () -> Unit,
    onOssLicensesClick: () -> Unit,
    onOnboardingClick: () -> Unit,
    onLaunchCameraTrackerClick: () -> Unit,
    viewModel: TopViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()

    TopContent(
        uiState = uiState,
        onVrcConnectionSettingsClick = onVrcConnectionSettingsClick,
        onBodyMeasurementsSettingsClick = onBodyMeasurementsSettingsClick,
        onPoseAnalyzerSettingsClick = onPoseAnalyzerSettingsClick,
        onPoseTrackerSettingsClick = onPoseTrackerSettingsClick,
        onPoseTrackerAdvancedSettingsClick = onPoseTrackerAdvancedSettingsClick,
        onDebugSettingsClick = onDebugSettingsClick,
        onOssLicensesClick = onOssLicensesClick,
        onOnboardingClick = onOnboardingClick,
        onLaunchCameraTrackerClick = onLaunchCameraTrackerClick,
    )
}
