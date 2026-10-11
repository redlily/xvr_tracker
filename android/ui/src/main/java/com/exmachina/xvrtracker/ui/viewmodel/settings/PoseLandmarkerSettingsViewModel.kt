package com.exmachina.xvrtracker.ui.viewmodel.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exmachina.xvrtracker.data.repository.PoseLandmarkerSettingsRepository
import com.exmachina.xvrtracker.data.type.PoseLandmarkerComputeType
import com.exmachina.xvrtracker.data.type.PoseLandmarkerModelType
import com.exmachina.xvrtracker.ui.state.settings.PoseLandmarkerSettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.exmachina.xvrtracker.ui.state.settings.PoseLandmarkerSettingsUiState.Content as PoseLandmarkerContent

@HiltViewModel
class PoseLandmarkerSettingsViewModel @Inject constructor(
    private val poseLandmarkerSettingsRepository: PoseLandmarkerSettingsRepository,
) : ViewModel() {

    private val _uiStateFlow = MutableStateFlow(PoseLandmarkerSettingsUiState())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    private val poseLandmarkerSettingsFlow = poseLandmarkerSettingsRepository.dataFlow

    init {
        poseLandmarkerSettingsFlow
            .onEach { data ->
                _uiStateFlow.update {
                    it.copy(
                        content = PoseLandmarkerContent.Initialized(
                            poseLandmarkerSettings = data,
                        ),
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onPoseLandmarkerModelTypeUpdate(value: PoseLandmarkerModelType) {
        viewModelScope.launch {
            poseLandmarkerSettingsRepository.modelType(value)
        }
    }

    fun onPoseLandmarkerComputeTypeUpdate(value: PoseLandmarkerComputeType) {
        viewModelScope.launch {
            poseLandmarkerSettingsRepository.computeType(value)
        }
    }

    fun onPoseLandmarkerVisibilityThresholdUpdate(value: Float) {
        viewModelScope.launch {
            poseLandmarkerSettingsRepository.visibilityThreshold(value)
        }
    }

    fun onPoseLandmarkerPresenceThresholdUpdate(value: Float) {
        viewModelScope.launch {
            poseLandmarkerSettingsRepository.presenceThreshold(value)
        }
    }
}
