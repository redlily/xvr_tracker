import SwiftUI

struct PoseTrackerSettingsScreen: View {
    @StateObject private var viewModel = PoseTrackerSettingsViewModel()
    
    var body: some View {
        PoseTrackerSettingsContent(
            uiState: viewModel.uiState,
            onHeadTrackerDataTypeChange: viewModel.onHeadTrackerDataTypeChange,
            onChestTrackerDataTypeChange: viewModel.onChestTrackerDataTypeChange,
            onWaistTrackerDataTypeChange: viewModel.onWaistTrackerDataTypeChange,
            onElbowTrackerDataTypeChange: viewModel.onElbowTrackerDataTypeChange,
            onKneeTrackerDataTypeChange: viewModel.onKneeTrackerDataTypeChange,
            onFeetTrackerDataTypeChange: viewModel.onFeetTrackerDataTypeChange,
            onPresetTrackingSettingsChose: viewModel.onPresetTrackingSettingsChose
        )
    }
}

#Preview {
    NavigationStack {
        PoseTrackerSettingsScreen()
    }
}