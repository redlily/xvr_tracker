package com.exmachina.xvrtracker.ui.viewmodel.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exmachina.xvrtracker.data.repository.PoseModificationSettingsRepository
import com.exmachina.xvrtracker.ui.state.settings.PoseTrackerAdvancedSettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.exmachina.xvrtracker.ui.state.settings.PoseTrackerAdvancedSettingsUiState.Content as PoseTrackerAdvancedContent

@HiltViewModel
class PoseTrackerAdvancedSettingsViewModel @Inject constructor(
    private val poseModificationSettingsRepository: PoseModificationSettingsRepository,
) : ViewModel() {

    private val _uiStateFlow = MutableStateFlow(PoseTrackerAdvancedSettingsUiState())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    private val poseModificationSettingsFlow = poseModificationSettingsRepository.dataFlow

    init {
        poseModificationSettingsFlow
            .onEach { data ->
                _uiStateFlow.update {
                    it.copy(
                        content = PoseTrackerAdvancedContent.Initialized(
                            poseModificationSettings = data,
                        ),
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onStandStabilizingCoefficientChange(value: Float) {
        viewModelScope.launch {
            poseModificationSettingsRepository.standStabilizingCoefficient(value)
        }
    }

    fun onSitStabilizingCoefficientChange(value: Float) {
        viewModelScope.launch {
            poseModificationSettingsRepository.sitStabilizingCoefficient(value)
        }
    }

    fun onLieDownStabilizingCoefficientChange(value: Float) {
        viewModelScope.launch {
            poseModificationSettingsRepository.lieDownStabilizingCoefficient(value)
        }
    }

    fun onStabilizingModeChangeThresholdTimeChange(value: Int) {
        viewModelScope.launch {
            poseModificationSettingsRepository.stabilizingModeChangeThresholdTime(value)
        }
    }

    fun onStabilizingModeChangeSoundEnabledChange(value: Boolean) {
        viewModelScope.launch {
            poseModificationSettingsRepository.isStabilizingModeChangeSoundEnabled(value)
        }
    }

    fun onBodyPositiveSlopeChange(value: Float) {
        viewModelScope.launch {
            poseModificationSettingsRepository.bodyPositiveSlope(value)
        }
    }

    fun onBodyNegativeSlopeChange(value: Float) {
        viewModelScope.launch {
            poseModificationSettingsRepository.bodyNegativeSlope(value)
        }
    }

    fun onKneePositiveSlopeChange(value: Float) {
        viewModelScope.launch {
            poseModificationSettingsRepository.kneePositiveDirectionSlope(value)
        }
    }

    fun onKneeNegativeSlopeChange(value: Float) {
        viewModelScope.launch {
            poseModificationSettingsRepository.kneeNegativeDirectionSlope(value)
        }
    }

    fun onFootPositiveSlopeChange(value: Float) {
        viewModelScope.launch {
            poseModificationSettingsRepository.footPositiveSlope(value)
        }
    }

    fun onFootNegativeSlopeChange(value: Float) {
        viewModelScope.launch {
            poseModificationSettingsRepository.footNegativeSlope(value)
        }
    }
}
