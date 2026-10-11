package com.exmachina.xvrtracker.ui.screen.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.exmachina.xvrtracker.ui.content.settings.VrcConnectionSettingsContent
import com.exmachina.xvrtracker.ui.viewmodel.settings.VrcConnectionViewModel

@Composable
fun VrcConnectionSettingScreen(
    onBack: () -> Unit,
    viewModel: VrcConnectionViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsState()

    VrcConnectionSettingsContent(
        uiState = uiState,
        onBack = onBack,
        onSendingIpOrDomainCheng = viewModel::onSendingIpOrDomainChage,
        onSendingPortChange = viewModel::onSendingPortChage,
    )
}
