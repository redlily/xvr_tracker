package com.exmachina.xvrtracker.ui.viewmodel.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exmachina.xvrtracker.common.util.getLocalAddresses
import com.exmachina.xvrtracker.data.repository.AnalyzedDataPlotServerSettingsRepository
import com.exmachina.xvrtracker.ui.state.settings.DebugSettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.InetAddress
import javax.inject.Inject

@HiltViewModel
class DebugSettingsViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val analyzedDataPlotServerSettingsRepository: AnalyzedDataPlotServerSettingsRepository,
) : ViewModel() {

    private val _uiStateFlow = MutableStateFlow(DebugSettingsUiState())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    private val analyzedDataPlotServerSettingsFlow = analyzedDataPlotServerSettingsRepository.dataFlow

    init {
        combine(
            analyzedDataPlotServerSettingsFlow,
            flow { emit(context.getLocalAddresses()) },
        ) { settings, privateIpAddresses ->
            DebugSettingsUiState.Content.Initialized(
                analyzedDataPlotServerSettings = settings,
                localIpAddress = privateIpAddresses.firstOrNull()?.address?.hostAddress,
            )
        }.onEach { content ->
            _uiStateFlow.update {
                it.copy(content = content)
            }
        }.launchIn(viewModelScope)
    }

    private fun isPrivateIpAddress(ipAddress: String): Boolean {
        return try {
            val inetAddress = InetAddress.getByName(ipAddress)
            inetAddress.isSiteLocalAddress
        } catch (e: Exception) {
            false
        }
    }

    fun onAnalyzedDataPlotServerEnabledChange(value: Boolean) {
        viewModelScope.launch {
            analyzedDataPlotServerSettingsRepository.isEnabled(value)
        }
    }

    fun onAnalyzedDataPlotServerPortChange(value: Int) {
        viewModelScope.launch {
            analyzedDataPlotServerSettingsRepository.port(value)
        }
    }

    fun onAnalyzedDataPlotServerPing(value: Int) {
        viewModelScope.launch {
            analyzedDataPlotServerSettingsRepository.ping(value)
        }
    }

    fun onAnalyzedDataPlotServerTimeoutChange(value: Int) {
        viewModelScope.launch {
            analyzedDataPlotServerSettingsRepository.timeout(value)
        }
    }
}
