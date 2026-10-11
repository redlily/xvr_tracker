package com.exmachina.xvrtracker.ui.screen.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exmachina.xvrtracker.ui.content.settings.DebugSettingsContent
import com.exmachina.xvrtracker.ui.viewmodel.settings.DebugSettingsViewModel

@Composable
fun DebugSettingsScreen(
    onBack: () -> Unit,
    viewModel: DebugSettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()
    val context = LocalContext.current

    DebugSettingsContent(
        uiState = uiState,
        onBack = onBack,
        onAnalyzedDataPlotServerEnabled = viewModel::onAnalyzedDataPlotServerEnabledChange,
        onAnalyzedDataPlotServerPortChange = viewModel::onAnalyzedDataPlotServerPortChange,
        onAnalyzedDataPlotServerPing = viewModel::onAnalyzedDataPlotServerPing,
        onAnalyzedDataPlotServerTimeoutChange = viewModel::onAnalyzedDataPlotServerTimeoutChange,
        onCopyAnalyzedDataPlotServerUrl = { text ->
            val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("Server URL", text)
            clipboardManager.setPrimaryClip(clipData)
        },
    )
}
