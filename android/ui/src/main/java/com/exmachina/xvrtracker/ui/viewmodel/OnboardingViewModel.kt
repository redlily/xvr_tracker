package com.exmachina.xvrtracker.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exmachina.xvrtracker.common.util.getLocalAddresses
import com.exmachina.xvrtracker.common.util.makeIpPrefix
import com.exmachina.xvrtracker.data.repository.BodyMeasurementSettingsRepository
import com.exmachina.xvrtracker.data.repository.OnboardingSettingsRepository
import com.exmachina.xvrtracker.data.repository.VrcConnectionSettingsRepository
import com.exmachina.xvrtracker.ui.state.OnboardingUiState
import com.exmachina.xvrtracker.ui.state.OnboardingUiState.Content
import com.exmachina.xvrtracker.ui.state.OnboardingUiState.DialogState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    @param:ApplicationContext val context: Context,
    private val onboardingSettingsRepository: OnboardingSettingsRepository,
    private val bodyMeasurementSettingsRepository: BodyMeasurementSettingsRepository,
    private val vrcConnectionSettingsRepository: VrcConnectionSettingsRepository,
) : ViewModel() {

    private val _uiStateFlow = MutableStateFlow(OnboardingUiState())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    private val _onboardingExitFlow = MutableSharedFlow<Unit>()
    val onboardingExitFlow = _onboardingExitFlow.asSharedFlow()

    init {
        viewModelScope.launch {
            val content = Content.Initialized(
                bodyHeight = bodyMeasurementSettingsRepository.bodyHeight(),
                connectionAddress = vrcConnectionSettingsRepository.sendingIpOrDomain().ifEmpty {
                    context.getLocalAddresses().firstOrNull()?.let {
                        makeIpPrefix(it.address, it.prefix)
                    } ?: ""
                },
            )
            _uiStateFlow.update {
                it.copy(content = content)
            }
        }
    }

    fun onBodyHeightUpdate(height: Float?): Boolean =
        uiStateFlow.value.content
            .let { it as? Content.Initialized }
            ?.let { content ->
                if (height != null && bodyMeasurementSettingsRepository.bodyHeight.checkValid(height)) {
                    viewModelScope.launch {
                        bodyMeasurementSettingsRepository.bodyHeight(height)
                    }
                    true
                } else {
                    _uiStateFlow.update {
                        it.copy(
                            dialogState = DialogState.BodyHeightOutOfRangeDialogState(
                                limits = content.bodyHeight.range,
                            ),
                        )
                    }
                    false
                }
            } == true

    fun onConnectionAddressUpdate(address: String): Boolean =
        uiStateFlow.value.content
            .let { it as? Content.Initialized }
            ?.let { content ->
                if (vrcConnectionSettingsRepository.sendingIpOrDomain.checkValid(address)) {
                    viewModelScope.launch {
                        vrcConnectionSettingsRepository.sendingIpOrDomain(address)
                    }
                    true
                } else {
                    _uiStateFlow.update {
                        it.copy(dialogState = DialogState.IncorrectNetworkAddressDialogState)
                    }
                    false
                }
            } == true

    fun onOnboardingExit() {
        viewModelScope.launch {
            onboardingSettingsRepository.done(true)
            _onboardingExitFlow.emit(Unit)
        }
    }

    fun onPermissionSetupImageDisplay() {
        _uiStateFlow.update {
            it.copy(dialogState = DialogState.PermissionSetupDialogState)
        }
    }

    fun onEnableOscInVrcImageDisplay() {
        _uiStateFlow.update {
            it.copy(dialogState = DialogState.EnableOscInVrcDialogState)
        }
    }

    fun onTestOscConnectionInVrcImageDisplay() {
        _uiStateFlow.update {
            it.copy(dialogState = DialogState.TestOscConnectionInVrcDialogState)
        }
    }

    fun onCalibrateTrackerInVrcImageDisplay() {
        _uiStateFlow.update {
            it.copy(dialogState = DialogState.CalibrateTrackerInVrcDialogState)
        }
    }

    fun onDialogClose() {
        _uiStateFlow.update {
            it.copy(dialogState = null)
        }
    }
}
