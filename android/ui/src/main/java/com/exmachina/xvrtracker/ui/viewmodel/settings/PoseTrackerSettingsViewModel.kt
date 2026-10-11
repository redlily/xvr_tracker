package com.exmachina.xvrtracker.ui.viewmodel.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exmachina.xvrtracker.data.repository.PoseTrackerSettingsRepository
import com.exmachina.xvrtracker.data.type.PoseTrackerMotionType
import com.exmachina.xvrtracker.ui.state.settings.PoseTrackerSettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.exmachina.xvrtracker.ui.state.settings.PoseTrackerSettingsUiState.Content as PoseTrackerContent

@HiltViewModel
class PoseTrackerSettingsViewModel @Inject constructor(
    private val poseTrackerSettingsRepository: PoseTrackerSettingsRepository,
) : ViewModel() {

    enum class PresetPoseTrackerSettings {
        Point7,
        Point9,
        Point10
    }

    private val _uiStateFlow = MutableStateFlow(PoseTrackerSettingsUiState())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    private val poseTrackerSettingsFlow = poseTrackerSettingsRepository.dataFlow

    init {
        poseTrackerSettingsFlow
            .onEach { data ->
                _uiStateFlow.update {
                    it.copy(
                        content = PoseTrackerContent.Initialized(
                            poseTrackerSettings = data,
                        ),
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onHeadTrackerDataTypeUpdate(value: PoseTrackerMotionType) {
        viewModelScope.launch {
            poseTrackerSettingsRepository.headTrackerDataType(value)
        }
    }

    fun onChestTrackerDataTypeUpdate(value: PoseTrackerMotionType) {
        viewModelScope.launch {
            poseTrackerSettingsRepository.chestTrackerDataType(value)
        }
    }

    fun onWaistTrackerDataTypeUpdate(value: PoseTrackerMotionType) {
        viewModelScope.launch {
            poseTrackerSettingsRepository.waistTrackerDataType(value)
        }
    }

    fun onElbowTrackerDataTypeUpdate(value: PoseTrackerMotionType) {
        viewModelScope.launch {
            poseTrackerSettingsRepository.elbowTrackerDataType(value)
        }
    }

    fun onKneeTrackerDataTypeUpdate(value: PoseTrackerMotionType) {
        viewModelScope.launch {
            poseTrackerSettingsRepository.kneeTrackerDataType(value)
        }
    }

    fun onFeetTrackerDataTypeUpdate(value: PoseTrackerMotionType) {
        viewModelScope.launch {
            poseTrackerSettingsRepository.feetTrackerDataType(value)
        }
    }

    fun onPresetTrackingSettingsChose(preset: PresetPoseTrackerSettings?) {
        if (preset == null) return
        viewModelScope.launch {
            when (preset) {
                PresetPoseTrackerSettings.Point7 -> {
                    poseTrackerSettingsRepository.headTrackerDataType(
                        PoseTrackerMotionType.POSITION_AND_ROTATION,
                    )
                    poseTrackerSettingsRepository.chestTrackerDataType(
                        PoseTrackerMotionType.POSITION_AND_ROTATION,
                    )
                    poseTrackerSettingsRepository.waistTrackerDataType(
                        PoseTrackerMotionType.POSITION_AND_ROTATION,
                    )
                    poseTrackerSettingsRepository.elbowTrackerDataType(
                        PoseTrackerMotionType.DISABLE,
                    )
                    poseTrackerSettingsRepository.kneeTrackerDataType(
                        PoseTrackerMotionType.DISABLE,
                    )
                    poseTrackerSettingsRepository.feetTrackerDataType(
                        PoseTrackerMotionType.POSITION_AND_ROTATION,
                    )
                }

                PresetPoseTrackerSettings.Point9 -> {
                    poseTrackerSettingsRepository.headTrackerDataType(
                        PoseTrackerMotionType.POSITION_AND_ROTATION,
                    )
                    poseTrackerSettingsRepository.chestTrackerDataType(
                        PoseTrackerMotionType.POSITION_AND_ROTATION,
                    )
                    poseTrackerSettingsRepository.waistTrackerDataType(
                        PoseTrackerMotionType.POSITION_AND_ROTATION,
                    )
                    poseTrackerSettingsRepository.elbowTrackerDataType(
                        PoseTrackerMotionType.POSITION_AND_ROTATION,
                    )
                    poseTrackerSettingsRepository.kneeTrackerDataType(
                        PoseTrackerMotionType.DISABLE,
                    )
                    poseTrackerSettingsRepository.feetTrackerDataType(
                        PoseTrackerMotionType.POSITION_AND_ROTATION,
                    )
                }

                PresetPoseTrackerSettings.Point10 -> {
                    poseTrackerSettingsRepository.headTrackerDataType(
                        PoseTrackerMotionType.POSITION_AND_ROTATION,
                    )
                    poseTrackerSettingsRepository.chestTrackerDataType(
                        PoseTrackerMotionType.POSITION_AND_ROTATION,
                    )
                    poseTrackerSettingsRepository.waistTrackerDataType(
                        PoseTrackerMotionType.POSITION_AND_ROTATION,
                    )
                    poseTrackerSettingsRepository.elbowTrackerDataType(
                        PoseTrackerMotionType.POSITION_AND_ROTATION,
                    )
                    poseTrackerSettingsRepository.kneeTrackerDataType(
                        PoseTrackerMotionType.POSITION_AND_ROTATION,
                    )
                    poseTrackerSettingsRepository.feetTrackerDataType(
                        PoseTrackerMotionType.POSITION_AND_ROTATION,
                    )
                }
            }
        }
    }
}
