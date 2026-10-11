package com.exmachina.xvrtracker.ui.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Accessibility
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Checklist
import androidx.compose.material.icons.rounded.DeviceHub
import androidx.compose.material.icons.rounded.Devices
import androidx.compose.material.icons.rounded.FormatListNumbered
import androidx.compose.material.icons.rounded.InsertChart
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Videocam
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.exmachina.xvrtracker.ui.R
import com.exmachina.xvrtracker.ui.state.settings.SettingTopUiState
import com.exmachina.xvrtracker.ui.theme.app.AppTheme
import com.exmachina.xvrtracker.ui.widget.Button
import com.exmachina.xvrtracker.ui.widget.TopAppBar
import com.exmachina.xvrtracker.ui.widget.setting.Item
import com.exmachina.xvrtracker.ui.widget.setting.SettingItemList

@Composable
fun TopContent(
    uiState: SettingTopUiState,
    onVrcConnectionSettingsClick: () -> Unit,
    onBodyMeasurementsSettingsClick: () -> Unit,
    onPoseAnalyzerSettingsClick: () -> Unit,
    onPoseTrackerSettingsClick: () -> Unit,
    onPoseTrackerAdvancedSettingsClick: () -> Unit,
    onDebugSettingsClick: () -> Unit,
    onOssLicensesClick: () -> Unit,
    onOnboardingClick: () -> Unit,
    onLaunchCameraTrackerClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                icon = Icons.Rounded.Build,
                title = stringResource(R.string.settings_top_title),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            SettingsItemList(
                uiState,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1.0f),
                onVrcConnectionClick = onVrcConnectionSettingsClick,
                onBodyMeasurementsClick = onBodyMeasurementsSettingsClick,
                onPoseAnalyzerClick = onPoseAnalyzerSettingsClick,
                onPoseTrackerClick = onPoseTrackerSettingsClick,
                onPoseTrackerAdvancedSettingsClick = onPoseTrackerAdvancedSettingsClick,
                onDebugClick = onDebugSettingsClick,
                onOnboardingClick = onOnboardingClick,
                onOssInfoClick = onOssLicensesClick,
            )

            Box(
                modifier = Modifier.padding(vertical = 8.dp),
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Rounded.Videocam,
                    text = stringResource(R.string.settings_top_launch_tracker),
                    onClick = onLaunchCameraTrackerClick,
                )
            }
        }
    }
}

@Composable
private fun SettingsItemList(
    uiState: SettingTopUiState,
    modifier: Modifier = Modifier,
    onVrcConnectionClick: () -> Unit,
    onBodyMeasurementsClick: () -> Unit,
    onPoseAnalyzerClick: () -> Unit,
    onPoseTrackerClick: () -> Unit,
    onPoseTrackerAdvancedSettingsClick: () -> Unit,
    onDebugClick: () -> Unit,
    onOnboardingClick: () -> Unit,
    onOssInfoClick: () -> Unit,
) {
    SettingItemList(modifier = modifier) {
        Group(
            title = stringResource(R.string.settings_top_game),
        )

        Item(
            title = stringResource(R.string.settings_top_vrc_connection),
            description = stringResource(R.string.settings_top_vrc_connection_description),
            leadingIcon = Icons.Rounded.Devices,
            onClick = onVrcConnectionClick,
        )

        Group(
            title = stringResource(R.string.settings_top_analysis),
        )

        Item(
            title = stringResource(R.string.settings_top_body_measurements),
            description = stringResource(R.string.settings_top_body_measurements_description),
            leadingIcon = Icons.Rounded.Accessibility,
            onClick = onBodyMeasurementsClick,
        )

        Item(
            title = stringResource(R.string.settings_top_pose_analyzer),
            description = stringResource(R.string.settings_top_pose_analyzer_description),
            leadingIcon = Icons.Rounded.Memory,
            onClick = onPoseAnalyzerClick,
        )

        Group(
            title = stringResource(R.string.settings_top_pose_tracker),
        )

        Item(
            title = stringResource(R.string.settings_top_pose_tracker),
            description = stringResource(R.string.settings_top_pose_tracker_description),
            leadingIcon = Icons.Rounded.DeviceHub,
            onClick = onPoseTrackerClick,
        )

        Item(
            title = stringResource(R.string.settings_top_pose_tracker_advanced_settings),
            description = stringResource(R.string.settings_top_pose_tracker_advanced_settings_description),
            leadingIcon = Icons.Rounded.InsertChart,
            onClick = onPoseTrackerAdvancedSettingsClick,
        )

        Group(
            title = stringResource(R.string.settings_top_other),
        )

        Item(
            title = stringResource(R.string.settings_top_debug),
            description = stringResource(R.string.settings_top_debug_description),
            leadingIcon = Icons.Rounded.BugReport,
            onClick = onDebugClick,
        )

        Item(
            title = stringResource(R.string.settings_top_oss_info),
            description = stringResource(R.string.settings_top_oss_info_description),
            leadingIcon = Icons.Rounded.FormatListNumbered,
            onClick = onOssInfoClick,
        )

        Item(
            title = stringResource(R.string.settings_top_onboarding),
            description = stringResource(R.string.settings_top_onboarding_description),
            leadingIcon = Icons.Rounded.Checklist,
            onClick = onOnboardingClick,
        )

        Item(
            title = stringResource(R.string.settings_top_version),
            description = stringResource(R.string.settings_top_version_description, uiState.appVersion),
            leadingIcon = Icons.Rounded.Android,
        )
    }
}

@Preview
@Composable
private fun SettingsTopContentPreview() {
    AppTheme {
        TopContent(
            uiState = SettingTopUiState(
                "0.0.0 - preview",
            ),
            onVrcConnectionSettingsClick = {},
            onBodyMeasurementsSettingsClick = {},
            onPoseAnalyzerSettingsClick = {},
            onPoseTrackerSettingsClick = {},
            onPoseTrackerAdvancedSettingsClick = {},
            onDebugSettingsClick = {},
            onOssLicensesClick = {},
            onOnboardingClick = {},
            onLaunchCameraTrackerClick = {},
        )
    }
}
