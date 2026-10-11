package com.exmachina.xvrtracker.ui.content.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.exmachina.xvrtracker.data.type.PoseLandmarkerComputeType
import com.exmachina.xvrtracker.data.type.PoseLandmarkerModelType
import com.exmachina.xvrtracker.ui.R
import com.exmachina.xvrtracker.ui.state.settings.PoseLandmarkerSettingsUiState
import com.exmachina.xvrtracker.ui.widget.TopAppBar
import com.exmachina.xvrtracker.ui.widget.setting.SettingItemList
import com.exmachina.xvrtracker.ui.state.settings.PoseLandmarkerSettingsUiState.Content as PoseLandmarkerContent

@Composable
fun PoseLandmarkerSettingsContent(
    uiState: PoseLandmarkerSettingsUiState,
    onBack: () -> Unit,
    onPoseLandmarkerModelTypeUpdate: (PoseLandmarkerModelType) -> Unit,
    onPoseLandmarkerComputeTypeUpdate: (PoseLandmarkerComputeType) -> Unit,
    onPoseLandmarkerVisibilityThresholdUpdate: (Float) -> Unit,
    onPoseLandmarkerPresenceThresholdUpdate: (Float) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                icon = Icons.Rounded.Memory,
                title = stringResource(R.string.settings_top_pose_analyzer),
                canNavigateBack = true,
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        when (uiState.content) {
            is PoseLandmarkerContent.Initializing -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            is PoseLandmarkerContent.Initialized -> {
                SettingsItemList(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    uiState = uiState.content,
                    onPoseLandmarkerModelTypeUpdate = onPoseLandmarkerModelTypeUpdate,
                    onPoseLandmarkerComputeTypeUpdate = onPoseLandmarkerComputeTypeUpdate,
                    onPoseLandmarkerVisibilityThresholdUpdate = onPoseLandmarkerVisibilityThresholdUpdate,
                    onPoseLandmarkerPresenceThresholdUpdate = onPoseLandmarkerPresenceThresholdUpdate,
                )
            }
        }
    }
}

@Composable
private fun SettingsItemList(
    uiState: PoseLandmarkerContent.Initialized,
    modifier: Modifier = Modifier,
    onPoseLandmarkerModelTypeUpdate: (PoseLandmarkerModelType) -> Unit,
    onPoseLandmarkerComputeTypeUpdate: (PoseLandmarkerComputeType) -> Unit,
    onPoseLandmarkerVisibilityThresholdUpdate: (Float) -> Unit,
    onPoseLandmarkerPresenceThresholdUpdate: (Float) -> Unit,
) {
    SettingItemList(
        modifier = modifier,
    ) {
        Group(title = stringResource(R.string.pose_landmarker_settings_image_analyzer))

        ChoiceItem(
            onValueApplyRequest = onPoseLandmarkerModelTypeUpdate,
            title = stringResource(R.string.pose_landmarker_settings_analyzer_model),
            description = stringResource(R.string.pose_landmarker_settings_analyzer_model_description),
            itemString = mapOf(
                PoseLandmarkerModelType.LITE to stringResource(R.string.pose_landmarker_settings_analyzer_model_lite),
                PoseLandmarkerModelType.STANDARD to stringResource(R.string.pose_landmarker_settings_analyzer_model_standard),
            ),
            items = PoseLandmarkerModelType.entries.toList(),
            selectedItem = uiState.poseLandmarkerSettings.modelType,
            enabled = true,
        )

        ChoiceItem(
            onValueApplyRequest = onPoseLandmarkerComputeTypeUpdate,
            title = stringResource(R.string.pose_landmarker_settings_compute_type),
            description = stringResource(R.string.pose_landmarker_settings_compute_type_description),
            itemString = mapOf(
                PoseLandmarkerComputeType.CPU to stringResource(R.string.pose_landmarker_settings_compute_type_cpu),
                PoseLandmarkerComputeType.GPU to stringResource(R.string.pose_landmarker_settings_compute_type_gpu),
            ),
            items = PoseLandmarkerComputeType.entries.toList(),
            selectedItem = uiState.poseLandmarkerSettings.computeType,
            enabled = true,
        )

        SliderItem(
            onValueApplyRequest = onPoseLandmarkerVisibilityThresholdUpdate,
            title = stringResource(R.string.pose_landmarker_settings_visibility_threshold),
            description = stringResource(R.string.pose_landmarker_settings_visibility_threshold_description),
            valueString = { "%.2f".format(it) },
            value = uiState.poseLandmarkerSettings.visibilityThreshold,
            enabled = true,
        )

        SliderItem(
            onValueApplyRequest = onPoseLandmarkerPresenceThresholdUpdate,
            title = stringResource(R.string.pose_landmarker_settings_presence_threshold),
            description = stringResource(R.string.pose_landmarker_settings_presence_threshold_description),
            valueString = { "%.2f".format(it) },
            value = uiState.poseLandmarkerSettings.presenceThreshold,
            enabled = true,
        )
    }
}
