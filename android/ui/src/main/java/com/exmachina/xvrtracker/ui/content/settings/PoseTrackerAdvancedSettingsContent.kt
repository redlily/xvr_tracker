package com.exmachina.xvrtracker.ui.content.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.InsertChart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.exmachina.xvrtracker.common.type.toFloat
import com.exmachina.xvrtracker.data.entity.PoseModificationSettings
import com.exmachina.xvrtracker.ui.R
import com.exmachina.xvrtracker.ui.state.settings.PoseTrackerAdvancedSettingsUiState
import com.exmachina.xvrtracker.ui.theme.app.AppTheme
import com.exmachina.xvrtracker.ui.widget.TopAppBar
import com.exmachina.xvrtracker.ui.widget.setting.SettingItemList
import com.exmachina.xvrtracker.ui.state.settings.PoseTrackerAdvancedSettingsUiState.Content as PoseTrackerAdvancedContent

@Composable
fun PoseTrackerAdvancedSettingsContent(
    uiState: PoseTrackerAdvancedSettingsUiState,
    onBack: () -> Unit,
    onStandStabilizingCoefficientChange: (Float) -> Unit,
    onSitStabilizingCoefficientChange: (Float) -> Unit,
    onLieDownStabilizingCoefficientChange: (Float) -> Unit,
    onStabilizingModeChangeThresholdTimeChange: (Int) -> Unit,
    onStabilizingModeChangeSoundEnabledChange: (Boolean) -> Unit,
    onBodyPositiveSlopeChange: (Float) -> Unit,
    onBodyNegativeSlopeChange: (Float) -> Unit,
    onKneePositiveSlopeChange: (Float) -> Unit,
    onKneeNegativeSlopeChange: (Float) -> Unit,
    onFootPositiveSlopeChange: (Float) -> Unit,
    onFootNegativeSlopeChange: (Float) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                icon = Icons.Rounded.InsertChart,
                title = stringResource(R.string.settings_top_pose_tracker_advanced_settings),
                canNavigateBack = true,
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        when (uiState.content) {
            is PoseTrackerAdvancedContent.Initializing -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            is PoseTrackerAdvancedContent.Initialized -> {
                PoseTrackerAdvancedSettingsList(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    uiState = uiState.content,
                    onStandStabilizingCoefficientChange = onStandStabilizingCoefficientChange,
                    onSitStabilizingCoefficientChange = onSitStabilizingCoefficientChange,
                    onLieDownStabilizingCoefficientChange = onLieDownStabilizingCoefficientChange,
                    onStabilizingModeChangeThresholdTimeChange = onStabilizingModeChangeThresholdTimeChange,
                    onStabilizingModeChangeSoundEnabledChange = onStabilizingModeChangeSoundEnabledChange,
                    onBodyPositiveSlopeChange = onBodyPositiveSlopeChange,
                    onBodyNegativeSlopeChange = onBodyNegativeSlopeChange,
                    onKneePositiveSlopeChange = onKneePositiveSlopeChange,
                    onKneeNegativeSlopeChange = onKneeNegativeSlopeChange,
                    onFootPositiveSlopeChange = onFootPositiveSlopeChange,
                    onFootNegativeSlopeChange = onFootNegativeSlopeChange,
                )
            }
        }
    }
}

@Composable
private fun PoseTrackerAdvancedSettingsList(
    uiState: PoseTrackerAdvancedContent.Initialized,
    modifier: Modifier = Modifier,
    onStandStabilizingCoefficientChange: (Float) -> Unit,
    onSitStabilizingCoefficientChange: (Float) -> Unit,
    onLieDownStabilizingCoefficientChange: (Float) -> Unit,
    onStabilizingModeChangeThresholdTimeChange: (Int) -> Unit,
    onStabilizingModeChangeSoundEnabledChange: (Boolean) -> Unit,
    onBodyPositiveSlopeChange: (Float) -> Unit,
    onBodyNegativeSlopeChange: (Float) -> Unit,
    onKneePositiveSlopeChange: (Float) -> Unit,
    onKneeNegativeSlopeChange: (Float) -> Unit,
    onFootPositiveSlopeChange: (Float) -> Unit,
    onFootNegativeSlopeChange: (Float) -> Unit,
) {
    SettingItemList(
        modifier = modifier,
    ) {
        Group(
            title = stringResource(R.string.pose_tracker_advanced_settings_stabilizing_coefficients),
            description = stringResource(R.string.pose_tracker_advanced_settings_stabilizing_coefficients_description),
        )

        SliderItem(
            onValueApplyRequest = onStandStabilizingCoefficientChange,
            title = stringResource(R.string.pose_tracker_advanced_settings_stand_stabilizing_coefficient),
            valueString = { "%.2f".format(it) },
            value = uiState.poseModificationSettings.standStabilizingCoefficient,
        )

        SliderItem(
            onValueApplyRequest = onSitStabilizingCoefficientChange,
            title = stringResource(R.string.pose_tracker_advanced_settings_sit_stabilizing_coefficient),
            valueString = { "%.2f".format(it) },
            value = uiState.poseModificationSettings.sitStabilizingCoefficient,
        )

        SliderItem(
            onValueApplyRequest = onLieDownStabilizingCoefficientChange,
            title = stringResource(R.string.pose_tracker_advanced_settings_lie_down_stabilizing_coefficient),
            valueString = { "%.2f".format(it) },
            value = uiState.poseModificationSettings.lieDownStabilizingCoefficient,
        )

        Group(
            title = stringResource(R.string.pose_tracker_advanced_settings_mode_change_settings),
            description = stringResource(R.string.pose_tracker_advanced_settings_mode_change_settings_description),
        )

        SliderItem(
            onValueApplyRequest = { onStabilizingModeChangeThresholdTimeChange(it.toInt()) },
            title = stringResource(R.string.pose_tracker_advanced_settings_stabilizing_mode_change_threshold_time),
            valueString = { "${it.toInt()}ms" },
            value = uiState.poseModificationSettings.stabilizingModeChangeThresholdTime.toFloat(),
        )

        SwitchItem(
            title = stringResource(R.string.pose_tracker_advanced_settings_stabilizing_mode_change_sound),
            description = stringResource(R.string.pose_tracker_advanced_settings_stabilizing_mode_change_sound_description),
            value = uiState.poseModificationSettings.isStabilizingModeChangeSoundEnabled,
            onChange = onStabilizingModeChangeSoundEnabledChange,
        )

        Group(
            title = stringResource(R.string.pose_tracker_advanced_settings_slop_settings),
            description = stringResource(R.string.pose_tracker_advanced_settings_slop_settings_description),
        )

        SliderItem(
            onValueApplyRequest = onBodyPositiveSlopeChange,
            title = stringResource(R.string.pose_tracker_advanced_settings_body_positive_slop),
            description = stringResource(R.string.pose_tracker_advanced_settings_body_positive_slop_description),
            valueString = { "%.1f°".format(it) },
            value = uiState.poseModificationSettings.bodyPositiveSlope,
        )

        SliderItem(
            onValueApplyRequest = onBodyNegativeSlopeChange,
            title = stringResource(R.string.pose_tracker_advanced_settings_body_negative_slop),
            description = stringResource(R.string.pose_tracker_advanced_settings_body_negative_slop_description),
            valueString = { "%.1f°".format(it) },
            value = uiState.poseModificationSettings.bodyNegativeSlope,
        )

        SliderItem(
            onValueApplyRequest = onFootPositiveSlopeChange,
            title = stringResource(R.string.pose_tracker_advanced_settings_foot_positive_slop),
            description = stringResource(R.string.pose_tracker_advanced_settings_foot_positive_slop_description),
            valueString = { "%.1f°".format(it) },
            value = uiState.poseModificationSettings.footPositiveSlope,
        )

        SliderItem(
            onValueApplyRequest = onFootNegativeSlopeChange,
            title = stringResource(R.string.pose_tracker_advanced_settings_foot_negative_slop),
            description = stringResource(R.string.pose_tracker_advanced_settings_foot_negative_slop_description),
            valueString = { "%.1f°".format(it) },
            value = uiState.poseModificationSettings.footNegativeSlope,
        )

        SliderItem(
            onValueApplyRequest = onKneePositiveSlopeChange,
            title = stringResource(R.string.pose_tracker_advanced_settings_knee_positive_slop),
            description = stringResource(R.string.pose_tracker_advanced_settings_knee_positive_slop_description),
            valueString = { "%.1f°".format(it) },
            value = uiState.poseModificationSettings.kneePositiveSlope,
        )

        SliderItem(
            onValueApplyRequest = onKneeNegativeSlopeChange,
            title = stringResource(R.string.pose_tracker_advanced_settings_knee_negative_slop),
            description = stringResource(R.string.pose_tracker_advanced_settings_knee_negative_slop_description),
            valueString = { "%.1f°".format(it) },
            value = uiState.poseModificationSettings.kneeNegativeSlope,
        )
    }
}

@Preview
@Composable
private fun PoseTrackerAdvancedSettingsContentPreview() {
    AppTheme {
        PoseTrackerAdvancedSettingsContent(
            uiState = PoseTrackerAdvancedSettingsUiState(
                content = PoseTrackerAdvancedContent.Initialized(
                    poseModificationSettings = PoseModificationSettings.default,
                ),
            ),
            onBack = {},
            onStandStabilizingCoefficientChange = {},
            onSitStabilizingCoefficientChange = {},
            onLieDownStabilizingCoefficientChange = {},
            onStabilizingModeChangeThresholdTimeChange = {},
            onStabilizingModeChangeSoundEnabledChange = {},
            onBodyPositiveSlopeChange = {},
            onBodyNegativeSlopeChange = {},
            onKneePositiveSlopeChange = {},
            onKneeNegativeSlopeChange = {},
            onFootPositiveSlopeChange = {},
            onFootNegativeSlopeChange = {},
        )
    }
}
