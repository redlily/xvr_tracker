package com.exmachina.xvrtracker.ui.viewmodel.settings

import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import com.exmachina.xvrtracker.ui.state.settings.SettingTopUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class TopViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : ViewModel() {

    private val _uiStateFlow = MutableStateFlow(
        SettingTopUiState(
            appVersion = getAppVersion(),
        ),
    )
    val uiStateFlow = _uiStateFlow.asStateFlow()

    private fun getAppVersion(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "0.0.0"
        } catch (e: PackageManager.NameNotFoundException) {
            "0.0.0"
        }
    }
}
