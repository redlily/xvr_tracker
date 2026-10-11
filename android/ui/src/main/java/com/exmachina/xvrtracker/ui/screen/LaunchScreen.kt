package com.exmachina.xvrtracker.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.exmachina.xvrtracker.ui.content.LaunchContent
import com.exmachina.xvrtracker.ui.viewmodel.LaunchViewModel

@Composable
fun LaunchScreen(
    viewModel: LaunchViewModel = hiltViewModel(),
    onOnboarding: () -> Unit,
    onTop: () -> Unit,
) {
    LaunchedEffect(viewModel) {
        viewModel.onBoardingNavigateFlow.collect {
            onOnboarding()
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.onTopNavigateFlow.collect {
            onTop()
        }
    }

    LaunchContent()
}
