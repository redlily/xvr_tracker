package com.exmachina.xvrtracker.ui.content.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Devices
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.exmachina.xvrtracker.common.type.limits
import com.exmachina.xvrtracker.common.util.positiveIntFilter
import com.exmachina.xvrtracker.ui.R
import com.exmachina.xvrtracker.ui.state.settings.VrcConnectionSettingsUiState
import com.exmachina.xvrtracker.ui.theme.app.AppTheme
import com.exmachina.xvrtracker.ui.widget.TopAppBar
import com.exmachina.xvrtracker.ui.widget.setting.IntSettingItem
import com.exmachina.xvrtracker.ui.widget.setting.SettingItemList
import com.exmachina.xvrtracker.ui.widget.setting.StringSettingItem
import com.exmachina.xvrtracker.ui.state.settings.VrcConnectionSettingsUiState.Content as VrcConnectionContent

@Composable
fun VrcConnectionSettingsContent(
    uiState: VrcConnectionSettingsUiState,
    onBack: () -> Unit,
    onSendingIpOrDomainCheng: (String) -> Boolean,
    onSendingPortChange: (Int) -> Boolean,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                icon = Icons.Rounded.Devices,
                title = stringResource(R.string.vrc_connection_settings_title),
                canNavigateBack = true,
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        when (uiState.content) {
            is VrcConnectionContent.Initializing -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            is VrcConnectionContent.Initialized -> {
                SettingsItemList(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    uiState = uiState,
                    content = uiState.content,
                    onSendingIpOrDomainChange = onSendingIpOrDomainCheng,
                    onSendingPortChange = onSendingPortChange,
                )
            }
        }
    }
}

@Composable
private fun SettingsItemList(
    uiState: VrcConnectionSettingsUiState,
    content: VrcConnectionContent.Initialized,
    modifier: Modifier = Modifier,
    onSendingIpOrDomainChange: (String) -> Boolean,
    onSendingPortChange: (Int) -> Boolean,
) {
    SettingItemList(
        modifier = modifier,
    ) {
        Group(
            title = stringResource(R.string.vrc_connection_settings_connection_destination),
        )

        StringSettingItem(
            title = stringResource(R.string.vrc_connection_settings_sending_ip_or_domain),
            value = content.sendingIpOrDomain.ifEmpty { stringResource(R.string.unset) },
            editorValue = content.sendingIpOrDomain.ifEmpty { uiState.defaultSendingIpPrefix },
            editorError = stringResource(R.string.vrc_connection_settings_sending_ip_or_domain_error),
            onEditorShowRequest = onEditorOpen,
            onEditorCloseRequest = onEditorClose,
            onValueApplyRequest = onSendingIpOrDomainChange,
        )

        IntSettingItem(
            title = stringResource(R.string.vrc_connection_settings_sending_port),
            description = stringResource(R.string.vrc_connection_settings_sending_port_description),
            value = "${content.sendingPort.value}",
            editorError = stringResource(R.string.vrc_connection_settings_sending_port_error),
            onEditorShowRequest = onEditorOpen,
            onEditorCloseRequest = onEditorClose,
            onValueApplyRequest = onSendingPortChange,
            onValueChange = ::positiveIntFilter,
        )
    }
}

@Preview
@Composable
private fun VrcConnectionSettingContentInitializedPreview() {
    AppTheme {
        VrcConnectionSettingsContent(
            uiState = VrcConnectionSettingsUiState(
                defaultSendingIpPrefix = "192.168.10.",
                content = VrcConnectionContent.Initialized(
                    sendingIpOrDomain = "192.168.10.2",
                    sendingPort = 9000 limits 0..65535,
                ),
            ),
            onBack = {},
            onSendingIpOrDomainCheng = { false },
            onSendingPortChange = { false },
        )
    }
}

@Preview
@Composable
private fun VrcConnectionSettingContentInitializingPreview() {
    AppTheme {
        VrcConnectionSettingsContent(
            uiState = VrcConnectionSettingsUiState(
                defaultSendingIpPrefix = "192.168.10.",
                content = VrcConnectionContent.Initializing,
            ),
            onBack = {},
            onSendingIpOrDomainCheng = { false },
            onSendingPortChange = { false },
        )
    }
}
