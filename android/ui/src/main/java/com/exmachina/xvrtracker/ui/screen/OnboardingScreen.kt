package com.exmachina.xvrtracker.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exmachina.xvrtracker.ui.content.OnboardingContent
import com.exmachina.xvrtracker.ui.viewmodel.OnboardingViewModel

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    onBoardingExit: () -> Unit,
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.onboardingExitFlow.collect {
            onBoardingExit()
        }
    }

    OnboardingContent(
        uiState = uiState,
        onHeightUpdate = viewModel::onBodyHeightUpdate,
        onConnectionAddressUpdate = viewModel::onConnectionAddressUpdate,
        onComplete = viewModel::onOnboardingExit,
        onSkip = viewModel::onOnboardingExit,
        onPermissionSetupImageDisplay = viewModel::onPermissionSetupImageDisplay,
        onEnableOscInVrcImageDisplay = viewModel::onEnableOscInVrcImageDisplay,
        onTestOscConnectionInVrcImageDisplay = viewModel::onTestOscConnectionInVrcImageDisplay,
        onCalibrateTrackerInVrcImageDisplay = viewModel::onCalibrateTrackerInVrcImageDisplay,
        onDialogClose = viewModel::onDialogClose,
    )
}
