import SwiftUI

struct DebugSettingsScreen: View {
    @StateObject private var viewModel = DebugSettingsViewModel()
    
    var body: some View {
        DebugSettingsContent(
            uiState: viewModel.uiState,
            onAnalyzedDataPlotServerEnabledChange: viewModel.onAnalyzedDataPlotServerEnabledChange,
            onAnalyzedDataPlotServerPortChange: viewModel.onAnalyzedDataPlotServerPortChange,
            onAnalyzedDataPlotServerPingChange: viewModel.onAnalyzedDataPlotServerPingChange,
            onAnalyzedDataPlotServerTimeoutChange: viewModel.onAnalyzedDataPlotServerTimeoutChange,
            onCopyAnalyzedDataPlotServerUrl: viewModel.onCopyAnalyzedDataPlotServerUrl,
            showCopiedAlert: $viewModel.showCopiedAlert
        )
    }
}

#Preview {
    NavigationStack {
        DebugSettingsScreen()
    }
}