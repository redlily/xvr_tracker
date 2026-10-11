package com.exmachina.xvrtracker.ui.content.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeviceHub
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.exmachina.xvrtracker.data.type.PoseTrackerMotionType
import com.exmachina.xvrtracker.ui.R
import com.exmachina.xvrtracker.ui.state.settings.PoseTrackerSettingsUiState
import com.exmachina.xvrtracker.ui.viewmodel.settings.PoseTrackerSettingsViewModel.PresetPoseTrackerSettings
import com.exmachina.xvrtracker.ui.widget.TopAppBar
import com.exmachina.xvrtracker.ui.widget.setting.SettingItemList
import com.exmachina.xvrtracker.ui.state.settings.PoseTrackerSettingsUiState.Content as PoseTrackerContent

@Composable
fun PoseTrackerSettingsContent(
    uiState: PoseTrackerSettingsUiState,
    onBack: () -> Unit,
    onHeadTrackerDataTypeUpdate: (PoseTrackerMotionType) -> Unit,
    onChestTrackerDataTypeUpdate: (PoseTrackerMotionType) -> Unit,
    onWaistTrackerDataTypeUpdate: (PoseTrackerMotionType) -> Unit,
    onElbowTrackerDataTypeUpdate: (PoseTrackerMotionType) -> Unit,
    onKneeTrackerDataTypeUpdate: (PoseTrackerMotionType) -> Unit,
    onFeetTrackerDataTypeUpdate: (PoseTrackerMotionType) -> Unit,
    onPresetTrackingSettingsChose: (PresetPoseTrackerSettings?) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                icon = Icons.Rounded.DeviceHub,
                title = stringResource(R.string.pose_tracker_settings_title),
                canNavigateBack = true,
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        when (uiState.content) {
            is PoseTrackerContent.Initializing -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            is PoseTrackerContent.Initialized -> {
                SettingItemList(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    uiState = uiState.content,
                    onHeadTrackerDataTypeUpdate = onHeadTrackerDataTypeUpdate,
                    onChestTrackerDataTypeUpdate = onChestTrackerDataTypeUpdate,
                    onWaistTrackerDataTypeUpdate = onWaistTrackerDataTypeUpdate,
                    onElbowTrackerDataTypeUpdate = onElbowTrackerDataTypeUpdate,
                    onKneeTrackerDataTypeUpdate = onKneeTrackerDataTypeUpdate,
                    onFeetTrackerDataTypeUpdate = onFeetTrackerDataTypeUpdate,
                    onPresetTrackingSettingsChose = onPresetTrackingSettingsChose,
                )
            }
        }
    }
}

@Composable
private fun SettingItemList(
    uiState: PoseTrackerContent.Initialized,
    onHeadTrackerDataTypeUpdate: (PoseTrackerMotionType) -> Unit,
    onChestTrackerDataTypeUpdate: (PoseTrackerMotionType) -> Unit,
    onWaistTrackerDataTypeUpdate: (PoseTrackerMotionType) -> Unit,
    onElbowTrackerDataTypeUpdate: (PoseTrackerMotionType) -> Unit,
    onKneeTrackerDataTypeUpdate: (PoseTrackerMotionType) -> Unit,
    onFeetTrackerDataTypeUpdate: (PoseTrackerMotionType) -> Unit,
    onPresetTrackingSettingsChose: (PresetPoseTrackerSettings?) -> Unit,
    modifier: Modifier,
) {
    val selectableTrackerDataTypes = listOf(
        PoseTrackerMotionType.DISABLE,
        PoseTrackerMotionType.POSITION,
        PoseTrackerMotionType.POSITION_AND_ROTATION,
    )
    val selectableTrackerDataTypeStrMap = mapOf(
        PoseTrackerMotionType.DISABLE to
            stringResource(R.string.pose_tracker_settings_tracker_data_type_disable),
        PoseTrackerMotionType.POSITION to
            stringResource(R.string.pose_tracker_settings_tracker_data_type_position),
        PoseTrackerMotionType.POSITION_AND_ROTATION to
            stringResource(R.string.pose_tracker_settings_tracker_data_type_position_rotation),
    )

    SettingItemList(
        modifier = modifier,
    ) {
        Group(
            title = stringResource(R.string.pose_tracker_settings_tracker_data_type),
            description = stringResource(R.string.pose_tracker_settings_tracker_data_type_description),
        )

        ChoiceItem(
            onValueApplyRequest = onHeadTrackerDataTypeUpdate,
            title = stringResource(R.string.pose_tracker_settings_head_tracker),
            itemString = selectableTrackerDataTypeStrMap,
            items = selectableTrackerDataTypes,
            selectedItem = uiState.poseTrackerSettings.head,
            enabled = true,
        )

        ChoiceItem(
            onValueApplyRequest = onChestTrackerDataTypeUpdate,
            title = stringResource(R.string.pose_tracker_settings_chest_tracker),
            itemString = selectableTrackerDataTypeStrMap,
            items = selectableTrackerDataTypes,
            selectedItem = uiState.poseTrackerSettings.chest,
            enabled = true,
        )

        ChoiceItem(
            onValueApplyRequest = onWaistTrackerDataTypeUpdate,
            title = stringResource(R.string.pose_tracker_settings_hip_tracker),
            itemString = selectableTrackerDataTypeStrMap,
            items = selectableTrackerDataTypes,
            selectedItem = uiState.poseTrackerSettings.waist,
            enabled = true,
        )

        ChoiceItem(
            onValueApplyRequest = onElbowTrackerDataTypeUpdate,
            title = stringResource(R.string.pose_tracker_settings_elbow_tracker),
            itemString = selectableTrackerDataTypeStrMap,
            items = selectableTrackerDataTypes,
            selectedItem = uiState.poseTrackerSettings.elbow,
            enabled = true,
        )

        ChoiceItem(
            onValueApplyRequest = onKneeTrackerDataTypeUpdate,
            title = stringResource(R.string.pose_tracker_settings_knee_tracker),
            itemString = selectableTrackerDataTypeStrMap,
            items = selectableTrackerDataTypes,
            selectedItem = uiState.poseTrackerSettings.knee,
            enabled = true,
        )

        ChoiceItem(
            onValueApplyRequest = onFeetTrackerDataTypeUpdate,
            title = stringResource(R.string.pose_tracker_settings_feet_tracker),
            itemString = selectableTrackerDataTypeStrMap,
            items = selectableTrackerDataTypes,
            selectedItem = uiState.poseTrackerSettings.feet,
            enabled = true,
        )

        Group(
            title = stringResource(R.string.pose_tracker_settings_other),
        )

        ChoiceItem(
            onValueApplyRequest = onPresetTrackingSettingsChose,
            title = stringResource(R.string.pose_tracker_settings_preset),
            description = stringResource(R.string.pose_tracker_settings_preset_description),
            itemString = mapOf(
                PresetPoseTrackerSettings.Point7 to
                    stringResource(R.string.pose_tracker_settings_preset_7_point),
                PresetPoseTrackerSettings.Point9 to
                    stringResource(R.string.pose_tracker_settings_preset_9_point),
                PresetPoseTrackerSettings.Point10 to
                    stringResource(R.string.pose_tracker_settings_preset_11_point),
            ),
            items = listOf(
                PresetPoseTrackerSettings.Point7,
                PresetPoseTrackerSettings.Point9,
                PresetPoseTrackerSettings.Point10,
            ),
            selectedItem = null,
            radioButtonEnabled = false,
            enabled = true,
        )
    }
}
