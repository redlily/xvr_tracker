import SwiftUI

struct PoseLandmarkerSettingsScreen: View {
    @StateObject private var viewModel = PoseLandmarkerSettingsViewModel()
    
    var body: some View {
        PoseLandmarkerSettingsContent(
            uiState: viewModel.uiState,
            onModelTypeChange: viewModel.onModelTypeChange,
            onComputeTypeChange: viewModel.onComputeTypeChange,
            onVisibilityThresholdChange: viewModel.onVisibilityThresholdChange,
            onPresenceThresholdChange: viewModel.onPresenceThresholdChange
        )
    }
}

#Preview {
    NavigationStack {
        PoseLandmarkerSettingsScreen()
    }
}