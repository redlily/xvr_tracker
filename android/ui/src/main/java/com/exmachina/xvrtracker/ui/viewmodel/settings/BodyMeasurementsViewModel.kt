package com.exmachina.xvrtracker.ui.viewmodel.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exmachina.xvrtracker.data.repository.BodyMeasurementSettingsRepository
import com.exmachina.xvrtracker.ui.state.settings.BodyMeasurementsSettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.exmachina.xvrtracker.ui.state.settings.BodyMeasurementsSettingsUiState.Content as BodyMeasurementsContent

@HiltViewModel
class BodyMeasurementsViewModel @Inject constructor(
    private val bodyMeasurementsSettingsRepository: BodyMeasurementSettingsRepository,
) : ViewModel() {

    private val _uiStateFlow = MutableStateFlow(BodyMeasurementsSettingsUiState())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    private val bodyMeasurementsSettingsFlow = bodyMeasurementsSettingsRepository.dataFlow

    init {
        bodyMeasurementsSettingsFlow
            .onEach { data ->
                _uiStateFlow.update {
                    it.copy(
                        content = BodyMeasurementsContent.Initialized(
                            bodyMeasurementsSettings = data,
                        ),
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onBodyHeightUpdate(value: Float) {
        viewModelScope.launch {
            bodyMeasurementsSettingsRepository.bodyHeight(value)
        }
    }

    fun onUpperBodyHeightRatioUpdate(value: Float) {
        viewModelScope.launch {
            bodyMeasurementsSettingsRepository.upperBodyHeightRatio(value)
        }
    }

    fun onHeadHeightRatioUpdate(value: Float) {
        viewModelScope.launch {
            bodyMeasurementsSettingsRepository.headHeightRatio(value)
        }
    }

    fun onHeadZOffsetRatioUpdate(value: Float) {
        viewModelScope.launch {
            bodyMeasurementsSettingsRepository.headZOffsetRatio(value)
        }
    }

    fun onNeckLengthRatioUpdate(value: Float) {
        viewModelScope.launch {
            bodyMeasurementsSettingsRepository.neckLengthRatio(value)
        }
    }

    fun onShoulderWightRatioUpdate(value: Float) {
        viewModelScope.launch {
            bodyMeasurementsSettingsRepository.shoulderWidthRatio(value)
        }
    }

    fun onArmLengthRatioUpdate(value: Float) {
        viewModelScope.launch {
            bodyMeasurementsSettingsRepository.armLengthRatio(value)
        }
    }

    fun onChestPotionRatioUpdate(value: Float) {
        viewModelScope.launch {
            bodyMeasurementsSettingsRepository.chestPositionRatio(value)
        }
    }

    fun onAbdomenPositionRatioUpdate(value: Float) {
        viewModelScope.launch {
            bodyMeasurementsSettingsRepository.waistPositionRatio(value)
        }
    }

    fun onHipWightRatioUpdate(value: Float) {
        viewModelScope.launch {
            bodyMeasurementsSettingsRepository.hipWidthRatio(value)
        }
    }

    fun onAnkleHeightRatioUpdate(value: Float) {
        viewModelScope.launch {
            bodyMeasurementsSettingsRepository.armLengthRatio(value)
        }
    }

    fun onFootLengthRatioUpdate(value: Float) {
        viewModelScope.launch {
            bodyMeasurementsSettingsRepository.footLengthRatio(value)
        }
    }

    fun onRatioReset() {
        viewModelScope.launch {
            bodyMeasurementsSettingsRepository.reset()
        }
    }
}
