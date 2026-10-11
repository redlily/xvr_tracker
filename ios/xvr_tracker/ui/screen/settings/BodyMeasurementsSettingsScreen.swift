import SwiftUI

struct BodyMeasurementsSettingsScreen: View {
    @StateObject private var viewModel = BodyMeasurementsSettingsViewModel()
    
    var body: some View {
        BodyMeasurementsSettingsContent(
            uiState: viewModel.uiState,
            onBodyHeightChange: viewModel.onBodyHeightChange,
            onUpperBodyHeightRatioChange: viewModel.onUpperBodyHeightRatioChange,
            onHeadHeightRatioChange: viewModel.onHeadHeightRatioChange,
            onHeadZOffsetRatioChange: viewModel.onHeadZOffsetRatioChange,
            onNeckLengthRatioChange: viewModel.onNeckLengthRatioChange,
            onShoulderWidthRatioChange: viewModel.onShoulderWidthRatioChange,
            onArmLengthRatioChange: viewModel.onArmLengthRatioChange,
            onChestPositionRatioChange: viewModel.onChestPositionRatioChange,
            onWaistPositionRatioChange: viewModel.onWaistPositionRatioChange,
            onHipWidthRatioChange: viewModel.onHipWidthRatioChange,
            onAnkleHeightRatioChange: viewModel.onAnkleHeightRatioChange,
            onFootLengthRatioChange: viewModel.onFootLengthRatioChange,
            onRatioReset: viewModel.onRatioReset
        )
    }
}