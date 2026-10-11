package com.exmachina.xvrtracker.ui.viewmodel.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exmachina.xvrtracker.common.storage.invoke
import com.exmachina.xvrtracker.common.util.getLocalAddresses
import com.exmachina.xvrtracker.common.util.makeIpPrefix
import com.exmachina.xvrtracker.data.repository.VrcConnectionSettingsRepository
import com.exmachina.xvrtracker.ui.state.settings.VrcConnectionSettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.exmachina.xvrtracker.ui.state.settings.VrcConnectionSettingsUiState.Content as VrcConnectionContent

@HiltViewModel
class VrcConnectionViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val vrcConnectionSettingsRepository: VrcConnectionSettingsRepository,
) : ViewModel() {

    private val _uiStateFlow = MutableStateFlow(VrcConnectionSettingsUiState())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        viewModelScope.launch {
            _uiStateFlow.update { uiState ->
                uiState.copy(
                    defaultSendingIpPrefix = context.getLocalAddresses().firstOrNull()?.let {
                        makeIpPrefix(
                            address = it.address,
                            prefix = it.prefix,
                        )
                    } ?: "",
                )
            }
        }

        vrcConnectionSettingsRepository.dataFlow
            .onEach { data ->
                _uiStateFlow.update {
                    it.copy(
                        content = VrcConnectionContent.Initialized(
                            sendingIpOrDomain = data.ipOrDomain,
                            sendingPort = data.port,
                        ),
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onSendingIpOrDomainChage(ipOrDomain: String): Boolean {
        return vrcConnectionSettingsRepository.sendingIpOrDomain(
            value = ipOrDomain,
            scope = viewModelScope,
        )
    }

    fun onSendingPortChage(port: Int): Boolean {
        return vrcConnectionSettingsRepository.sendingPort(
            value = port, scope = viewModelScope,
        )
    }
}
