package com.exmachina.xvrtracker.ui.content.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Accessibility
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.exmachina.xvrtracker.common.util.positiveFloatFilter
import com.exmachina.xvrtracker.data.entity.BodyMeasurementsSettings
import com.exmachina.xvrtracker.domain.type.bodyMeasurements
import com.exmachina.xvrtracker.ui.R
import com.exmachina.xvrtracker.ui.state.settings.BodyMeasurementsSettingsUiState
import com.exmachina.xvrtracker.ui.theme.app.AppTheme
import com.exmachina.xvrtracker.ui.util.NumberOnly
import com.exmachina.xvrtracker.ui.widget.TopAppBar
import com.exmachina.xvrtracker.ui.widget.setting.ConfirmSettingItem
import com.exmachina.xvrtracker.ui.widget.setting.FloatSettingItem
import com.exmachina.xvrtracker.ui.widget.setting.SettingItem
import com.exmachina.xvrtracker.ui.widget.setting.SettingItemList
import com.exmachina.xvrtracker.ui.state.settings.BodyMeasurementsSettingsUiState.Content as BodyMeasurementsContent

@Composable
fun BodyMeasurementsSettingsContent(
    uiState: BodyMeasurementsSettingsUiState,
    onBack: () -> Unit,
    onBodyHeightChange: (Float) -> Unit,
    onUpperBodyHeightRatioChange: (Float) -> Unit,
    onHeadHeightRationChange: (Float) -> Unit,
    onHeadZOffsetRatioChange: (Float) -> Unit,
    onNeckLengthRatioChange: (Float) -> Unit,
    onShoulderHeightRationChange: (Float) -> Unit,
    onArmLengthRatioChange: (Float) -> Unit,
    onChestPositionRatioChange: (Float) -> Unit,
    onWaistPositionRationChange: (Float) -> Unit,
    onHipWidthRationChange: (Float) -> Unit,
    onAnkleHeightChange: (Float) -> Unit,
    onFootHeightChange: (Float) -> Unit,
    onRatioReset: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                icon = Icons.Rounded.Accessibility,
                title = stringResource(R.string.body_measurements_settings_title),
                canNavigateBack = true,
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        when (uiState.content) {
            is BodyMeasurementsContent.Initializing -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            is BodyMeasurementsContent.Initialized -> {
                SettingsItemList(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    uiState = uiState.content,
                    onBodyHeightChange = onBodyHeightChange,
                    onUpperBodyHeightRatioChange = onUpperBodyHeightRatioChange,
                    onHeadHeightRationChange = onHeadHeightRationChange,
                    onHeadZOffsetRatioChange = onHeadZOffsetRatioChange,
                    onNeckLengthRatioChange = onNeckLengthRatioChange,
                    onShoulderHeightRationChange = onShoulderHeightRationChange,
                    onArmLengthRatioChange = onArmLengthRatioChange,
                    onChestPositionRatioChange = onChestPositionRatioChange,
                    onWaistPositionRationChange = onWaistPositionRationChange,
                    onHipWidthRationChange = onHipWidthRationChange,
                    onAnkleHeightChange = onAnkleHeightChange,
                    onFootHeightChange = onFootHeightChange,
                    onRatioReset = onRatioReset,
                )
            }
        }
    }
}

@Composable
private fun SettingsItemList(
    uiState: BodyMeasurementsContent.Initialized,
    modifier: Modifier = Modifier,
    onBodyHeightChange: (Float) -> Unit,
    onUpperBodyHeightRatioChange: (Float) -> Unit,
    onHeadHeightRationChange: (Float) -> Unit,
    onHeadZOffsetRatioChange: (Float) -> Unit,
    onNeckLengthRatioChange: (Float) -> Unit,
    onShoulderHeightRationChange: (Float) -> Unit,
    onArmLengthRatioChange: (Float) -> Unit,
    onChestPositionRatioChange: (Float) -> Unit,
    onWaistPositionRationChange: (Float) -> Unit,
    onHipWidthRationChange: (Float) -> Unit,
    onAnkleHeightChange: (Float) -> Unit,
    onFootHeightChange: (Float) -> Unit,
    onRatioReset: () -> Unit,
) {
    SettingItemList(
        modifier = modifier,
    ) {
        Group(
            title = stringResource(R.string.body_measurements_settings_base),
        )

        FloatSettingItem(
            title = stringResource(R.string.body_measurements_settings_body_height),
            value = uiState.bodyMeasurementsSettings.bodyHeight.value,
            valueString = {
                getLengthAsString(it)
            },
            editorValueString = { (it * 100f).toString() },
            editorError = stringResource(
                R.string.body_measurements_settings_body_height_error,
                uiState.bodyMeasurementsSettings.bodyHeight.range.start * 100f,
                uiState.bodyMeasurementsSettings.bodyHeight.range.endInclusive * 100f,
            ),
            onValueChange = ::positiveFloatFilter,
            onEditorShowRequest = onEditorOpen,
            onEditorCloseRequest = onEditorClose,
            onValueApplyRequest = {
                val value = it / 100f
                val test = value in uiState.bodyMeasurementsSettings.bodyHeight.range
                if (test) onBodyHeightChange(value)
                test
            },
            keyboardOptions = KeyboardOptions.NumberOnly,
        )

        SliderItem(
            onValueApplyRequest = onUpperBodyHeightRatioChange,
            title = stringResource(R.string.body_measurements_settings_upper_body_height),
            valueString = {
                getRatioAndLengthAsString(
                    ratio = it,
                    value = uiState.bodyMeasurementsSettings
                        .bodyMeasurements(
                            upperBodyHeightRatio = it,
                        ).upperBodyHeight,
                )
            },
            value = uiState.bodyMeasurementsSettings.upperBodyHeightRatio,
            enabled = true,
        )

        Group(
            title = stringResource(R.string.body_measurements_settings_head),
        )

        SliderItem(
            onValueApplyRequest = onHeadHeightRationChange,
            title = stringResource(R.string.body_measurements_settings_head_height),
            description = stringResource(R.string.body_measurements_settings_head_height_description),
            valueString = {
                getRatioAndLengthAsString(
                    ratio = it,
                    value = uiState.bodyMeasurementsSettings
                        .bodyMeasurements(
                            headHeightRatio = it,
                        ).headHeight,
                )
            },
            value = uiState.bodyMeasurementsSettings.headHeightRatio,
            enabled = true,
        )

        SliderItem(
            onValueApplyRequest = onHeadZOffsetRatioChange,
            title = stringResource(R.string.body_measurements_settings_head_z_offset),
            description = stringResource(R.string.body_measurements_settings_head_z_offset_description),
            valueString = {
                getRatioAndLengthAsString(
                    ratio = it,
                    value = uiState.bodyMeasurementsSettings
                        .bodyMeasurements(
                            headZOffsetRatio = it,
                        ).headZOffset,
                )
            },
            value = uiState.bodyMeasurementsSettings.headZOffsetRatio,
            enabled = true,
        )

        SliderItem(
            onValueApplyRequest = onNeckLengthRatioChange,
            title = stringResource(R.string.body_measurements_settings_neck_length),
            description = stringResource(R.string.body_measurements_settings_neck_length_description),
            valueString = {
                getRatioAndLengthAsString(
                    ratio = it,
                    value = uiState.bodyMeasurementsSettings
                        .bodyMeasurements(
                            neckLengthRatio = it,
                        ).neckLength,
                )
            },
            value = uiState.bodyMeasurementsSettings.neckLengthRatio,
            enabled = true,
        )

        Group(
            title = stringResource(R.string.body_measurements_settings_body),
        )

        SliderItem(
            onValueApplyRequest = onShoulderHeightRationChange,
            title = stringResource(R.string.body_measurements_settings_shoulder_width),
            description = stringResource(R.string.body_measurements_settings_shoulder_width_description),
            valueString = {
                getRatioAndLengthAsString(
                    ratio = it,
                    value = uiState.bodyMeasurementsSettings
                        .bodyMeasurements(
                            shoulderWidthRatio = it,
                        ).shoulderWidth,
                )
            },
            value = uiState.bodyMeasurementsSettings.shoulderWidthRatio,
            enabled = true,
        )

        SliderItem(
            onValueApplyRequest = onArmLengthRatioChange,
            title = stringResource(R.string.body_measurements_settings_arm_length),
            description = stringResource(R.string.body_measurements_settings_arm_length_description),
            valueString = {
                getRatioAndLengthAsString(
                    ratio = it,
                    value = uiState.bodyMeasurementsSettings
                        .bodyMeasurements(
                            armLengthRatio = it,
                        ).armLength,
                )
            },
            value = uiState.bodyMeasurementsSettings.armLengthRatio,
            enabled = true,
        )

        SliderItem(
            onValueApplyRequest = onChestPositionRatioChange,
            title = stringResource(R.string.body_measurements_settings_chest_position),
            description = stringResource(R.string.body_measurements_settings_chest_position_description),
            valueString = {
                getRatioAndLengthAsString(
                    ratio = it,
                    value = uiState.bodyMeasurementsSettings
                        .bodyMeasurements(
                            armLengthRatio = it,
                        ).shoulderToChestLength,
                )
            },
            value = uiState.bodyMeasurementsSettings.chestPositionRatio,
            enabled = true,
        )

        SliderItem(
            onValueApplyRequest = onWaistPositionRationChange,
            title = stringResource(R.string.body_measurements_settings_waist_position),
            description = stringResource(R.string.body_measurements_settings_waist_position_description),
            valueString = {
                getRatioAndLengthAsString(
                    ratio = it,
                    value = uiState.bodyMeasurementsSettings
                        .bodyMeasurements(
                            waistPositionRatio = it,
                        ).waistToHipLength,
                )
            },
            value = uiState.bodyMeasurementsSettings.waistPositionRatio,
            enabled = true,
        )

        Group(
            title = stringResource(R.string.body_measurements_settings_lower_body),
        )

        SliderItem(
            onValueApplyRequest = onHipWidthRationChange,
            title = stringResource(R.string.body_measurements_settings_hip_width),
            description = stringResource(R.string.body_measurements_settings_hip_width_description),
            valueString = {
                getRatioAndLengthAsString(
                    ratio = it,
                    value = uiState.bodyMeasurementsSettings
                        .bodyMeasurements(
                            waistPositionRatio = it,
                        ).hipWidth,
                )
            },
            value = uiState.bodyMeasurementsSettings.hipWidthRatio,
            enabled = true,
        )

        SettingItem(
            title = stringResource(R.string.body_measurements_settings_leg_length),
            description = stringResource(R.string.body_measurements_settings_leg_length_description),
            value = getLengthAsString(
                uiState.bodyMeasurementsSettings.bodyMeasurements().legLength,
            ),
            enabled = false,
        )

        SliderItem(
            onValueApplyRequest = onAnkleHeightChange,
            title = stringResource(R.string.body_measurements_settings_ankle_position),
            description = stringResource(R.string.body_measurements_settings_ankle_position_description),
            valueString = {
                getRatioAndLengthAsString(
                    ratio = it,
                    value = uiState.bodyMeasurementsSettings
                        .bodyMeasurements(
                            ankleHeightRatio = it,
                        ).ankleToHeelLength,
                )
            },
            value = uiState.bodyMeasurementsSettings.ankleHeightRatio,
            enabled = true,
        )

        SliderItem(
            onValueApplyRequest = onFootHeightChange,
            title = stringResource(R.string.body_measurements_settings_foot_length),
            description = stringResource(R.string.body_measurements_settings_foot_length_description),
            valueString = {
                getRatioAndLengthAsString(
                    ratio = it,
                    value = uiState.bodyMeasurementsSettings
                        .bodyMeasurements(
                            footLengthRatio = it,
                        ).footLength,
                )
            },
            value = uiState.bodyMeasurementsSettings.footLengthRatio,
            enabled = true,
        )

        Group(
            title = stringResource(R.string.other),
        )

        ConfirmSettingItem(
            title = stringResource(R.string.body_measurements_settings_reset_ratio),
            makeSureMessage = stringResource(R.string.body_measurements_settings_reset_ratio_message),
            makeSureAccept = stringResource(R.string.confirm),
            onMakeSureShowRequest = onEditorOpen,
            onMakeSureCloseRequest = onEditorClose,
            onMakeSureAccept = onRatioReset,
        )
    }
}

@Composable
private fun getLengthAsString(value: Float): String {
    return stringResource(
        R.string.body_measurements_settings_value,
        value * 100f,
    )
}

@Composable
private fun getRatioAndLengthAsString(ratio: Float, value: Float): String {
    return stringResource(
        R.string.body_measurements_settings_ratio_and_value,
        ratio * 100f,
        value * 100f,
    )
}

@Preview
@Composable
private fun BodyMeasurementsSettingsContentInitializedPreview() {
    AppTheme {
        BodyMeasurementsSettingsContent(
            uiState = BodyMeasurementsSettingsUiState(
                content = BodyMeasurementsContent.Initialized(
                    bodyMeasurementsSettings = BodyMeasurementsSettings.default,
                ),
            ),
            onBack = {},
            onBodyHeightChange = {},
            onUpperBodyHeightRatioChange = {},
            onHeadHeightRationChange = {},
            onHeadZOffsetRatioChange = {},
            onNeckLengthRatioChange = {},
            onShoulderHeightRationChange = {},
            onArmLengthRatioChange = {},
            onChestPositionRatioChange = {},
            onWaistPositionRationChange = {},
            onHipWidthRationChange = {},
            onAnkleHeightChange = {},
            onFootHeightChange = {},
            onRatioReset = {},
        )
    }
}
