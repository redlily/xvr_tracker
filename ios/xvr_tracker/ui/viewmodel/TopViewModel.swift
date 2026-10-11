import SwiftUI
import Combine
import di
import data

@MainActor
class TopViewModel: ObservableObject {
    @Published var uiState: MainUiState = MainUiState()

    func navigateToVrcConnectionSettings() {
        uiState.navigationPath.append(.vrcConnectionSettings)
    }

    func navigateToBodyMeasurementsSettings() {
        uiState.navigationPath.append(.bodyMeasurementsSettings)
    }

    func navigateToPoseAnalyzerSettings() {
        uiState.navigationPath.append(.PoseLandmarkerSettings)
    }

    func navigateToPoseTrackerSettings() {
        uiState.navigationPath.append(.poseTrackerSettings)
    }

    func navigateToPoseTrackerAdvancedSettings() {
        uiState.navigationPath.append(.poseTrackerAdvancedSettings)
    }

    func navigateToDebugSettings() {
        uiState.navigationPath.append(.debugSettings)
    }

    func navigateToOssLicenses() {
        uiState.navigationPath.append(.ossLicenses)
    }

    func navigateBack() {
        if !uiState.navigationPath.isEmpty {
            uiState.navigationPath.removeLast()
        }
    }
}
