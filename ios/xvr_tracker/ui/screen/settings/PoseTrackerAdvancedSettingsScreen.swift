import SwiftUI

struct PoseTrackerAdvancedSettingsScreen: View {
    @StateObject private var viewModel = PoseTrackerAdvancedSettingsViewModel()
    
    var body: some View {
        PoseTrackerAdvancedSettingsContent(
            uiState: viewModel.uiState,
            onStandStabilizingCoefficientChange: viewModel.onStandStabilizingCoefficientChange,
            onSitStabilizingCoefficientChange: viewModel.onSitStabilizingCoefficientChange,
            onLieDownStabilizingCoefficientChange: viewModel.onLieDownStabilizingCoefficientChange,
            onStabilizingModeChangeThresholdTimeChange: viewModel.onStabilizingModeChangeThresholdTimeChange,
            onStabilizingModeChangeSoundEnabledChange: viewModel.onStabilizingModeChangeSoundEnabledChange,
            onBodyPositiveSlopeChange: viewModel.onBodyPositiveSlopeChange,
            onBodyNegativeSlopeChange: viewModel.onBodyNegativeSlopeChange,
            onFootPositiveSlopeChange: viewModel.onFootPositiveSlopeChange,
            onFootNegativeSlopeChange: viewModel.onFootNegativeSlopeChange,
            onKneePositiveSlopeChange: viewModel.onKneePositiveSlopeChange,
            onKneeNegativeSlopeChange: viewModel.onKneeNegativeSlopeChange
        )
    }
}

#Preview {
    NavigationStack {
        PoseTrackerAdvancedSettingsScreen()
    }
}
