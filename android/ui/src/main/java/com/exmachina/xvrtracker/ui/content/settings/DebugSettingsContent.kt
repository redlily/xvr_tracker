package com.exmachina.xvrtracker.ui.content.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.exmachina.xvrtracker.common.util.onTrue
import com.exmachina.xvrtracker.common.util.positiveIntFilter
import com.exmachina.xvrtracker.data.entity.AnalyzedDataPlotServerSettings
import com.exmachina.xvrtracker.ui.R
import com.exmachina.xvrtracker.ui.state.settings.DebugSettingsUiState
import com.exmachina.xvrtracker.ui.theme.app.AppTheme
import com.exmachina.xvrtracker.ui.widget.TopAppBar
import com.exmachina.xvrtracker.ui.widget.setting.SettingItemList
import com.exmachina.xvrtracker.ui.state.settings.DebugSettingsUiState.Content as DebugContent

@Composable
fun DebugSettingsContent(
    uiState: DebugSettingsUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onAnalyzedDataPlotServerEnabled: (Boolean) -> Unit,
    onAnalyzedDataPlotServerPortChange: (Int) -> Unit,
    onAnalyzedDataPlotServerPing: (Int) -> Unit,
    onAnalyzedDataPlotServerTimeoutChange: (Int) -> Unit,
    onCopyAnalyzedDataPlotServerUrl: (String) -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                icon = Icons.Rounded.BugReport,
                title = stringResource(R.string.settings_top_debug),
                canNavigateBack = true,
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        when (uiState.content) {
            is DebugContent.Initializing -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            is DebugContent.Initialized -> {
                DebugSettingsList(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    uiState = uiState.content,
                    onAnalyzedDataPlotServerEnabled = onAnalyzedDataPlotServerEnabled,
                    onAnalyzedDataPlotServerPortChange = onAnalyzedDataPlotServerPortChange,
                    onAnalyzedDataPlotServerPing = onAnalyzedDataPlotServerPing,
                    onAnalyzedDataPlotServerTimeoutChange = onAnalyzedDataPlotServerTimeoutChange,
                    onCopyAnalyzedDataPlotServerUrl = onCopyAnalyzedDataPlotServerUrl,
                )
            }
        }
    }
}

@Composable
private fun DebugSettingsList(
    uiState: DebugContent.Initialized,
    modifier: Modifier = Modifier,
    onAnalyzedDataPlotServerEnabled: (Boolean) -> Unit,
    onAnalyzedDataPlotServerPortChange: (Int) -> Unit,
    onAnalyzedDataPlotServerPing: (Int) -> Unit,
    onAnalyzedDataPlotServerTimeoutChange: (Int) -> Unit,
    onCopyAnalyzedDataPlotServerUrl: (String) -> Unit,
) {
    val urlFormat = stringResource(R.string.debug_settings_analyzed_data_plot_server_url_format)
    val urlUnavailable = stringResource(R.string.debug_settings_analyzed_data_plot_server_url_unavailable)
    val analyzedDataPlotServerUrl =
        remember(uiState.localIpAddress, uiState.analyzedDataPlotServerSettings.port.value, urlFormat, urlUnavailable) {
            if (uiState.localIpAddress != null) {
                urlFormat.format(uiState.localIpAddress, uiState.analyzedDataPlotServerSettings.port.value)
            } else {
                urlUnavailable
            }
        }

    SettingItemList(
        modifier = modifier,
    ) {
        SwitchItem(
            title = stringResource(R.string.debug_settings_analyzed_data_plot_server_group),
            description = stringResource(R.string.debug_settings_analyzed_data_plot_server_group_description),
            value = uiState.analyzedDataPlotServerSettings.isEnabled,
            onChange = onAnalyzedDataPlotServerEnabled,
        )

        IntItem(
            title = stringResource(R.string.debug_settings_analyzed_data_plot_server_port),
            description = stringResource(R.string.debug_settings_analyzed_data_plot_server_port_description),
            value = "${uiState.analyzedDataPlotServerSettings.port.value}",
            editorError = stringResource(
                R.string.debug_settings_analyzed_data_plot_server_port_error,
                uiState.analyzedDataPlotServerSettings.port.range.start,
                uiState.analyzedDataPlotServerSettings.port.range.endInclusive,
            ),
            onValueChange = ::positiveIntFilter,
            onValueApplyRequest = {
                uiState.analyzedDataPlotServerSettings.port.range.contains(it).onTrue {
                    onAnalyzedDataPlotServerPortChange(it)
                }
            },
            enabled = uiState.analyzedDataPlotServerSettings.isEnabled,
        )

        IntItem(
            title = stringResource(R.string.debug_settings_analyzed_data_plot_server_ping),
            description = stringResource(R.string.debug_settings_analyzed_data_plot_server_ping_description),
            value = "${uiState.analyzedDataPlotServerSettings.ping.value}",
            editorError = stringResource(
                R.string.debug_settings_analyzed_data_plot_server_ping_error,
                uiState.analyzedDataPlotServerSettings.ping.range.start,
                uiState.analyzedDataPlotServerSettings.ping.range.endInclusive,
            ),
            onValueChange = ::positiveIntFilter,
            onValueApplyRequest = {
                uiState.analyzedDataPlotServerSettings.ping.range.contains(it).onTrue {
                    onAnalyzedDataPlotServerPing(it)
                }
            },
            enabled = uiState.analyzedDataPlotServerSettings.isEnabled,
        )

        IntItem(
            title = stringResource(R.string.debug_settings_analyzed_data_plot_server_timeout),
            description = stringResource(R.string.debug_settings_analyzed_data_plot_server_timeout_description),
            value = "${uiState.analyzedDataPlotServerSettings.timeout.value}",
            editorError = stringResource(
                R.string.debug_settings_analyzed_data_plot_server_timeout_error,
                uiState.analyzedDataPlotServerSettings.timeout.range.start,
                uiState.analyzedDataPlotServerSettings.timeout.range.endInclusive,
            ),
            onValueChange = ::positiveIntFilter,
            onValueApplyRequest = {
                uiState.analyzedDataPlotServerSettings.timeout.range.contains(it).onTrue {
                    onAnalyzedDataPlotServerTimeoutChange(it)
                }
            },
            enabled = uiState.analyzedDataPlotServerSettings.isEnabled,
        )

        Item(
            title = stringResource(R.string.debug_settings_analyzed_data_plot_server_url),
            value = analyzedDataPlotServerUrl,
            onClick = {
                onCopyAnalyzedDataPlotServerUrl(analyzedDataPlotServerUrl)
            },
            enabled = uiState.localIpAddress != null && uiState.analyzedDataPlotServerSettings.isEnabled,
        )
    }
}

@Preview
@Composable
private fun DebugSettingsContentPreview() {
    AppTheme {
        DebugSettingsContent(
            uiState = DebugSettingsUiState(
                content = DebugContent.Initialized(
                    analyzedDataPlotServerSettings = AnalyzedDataPlotServerSettings.default,
                    localIpAddress = "192.168.11.5",
                ),
            ),
            onBack = {},
            onAnalyzedDataPlotServerEnabled = {},
            onAnalyzedDataPlotServerPortChange = {},
            onAnalyzedDataPlotServerPing = {},
            onAnalyzedDataPlotServerTimeoutChange = {},
        )
    }
}
