import SwiftUI

struct TopScreen: View {
    @StateObject var viewModel = TopViewModel()
    
    let onOnboardingNavigate: () -> Void
    let onCameraTrackerNavigate: () -> Void
    
    var body: some View {
        NavigationStack(path: $viewModel.uiState.navigationPath) {
            TopContent(
                onVrcConnectionSettingsClick: {
                    viewModel.navigateToVrcConnectionSettings()
                },
                onBodyMeasurementsSettingsClick: {
                    viewModel.navigateToBodyMeasurementsSettings()
                },
                onPoseAnalyzerSettingsClick: {
                    viewModel.navigateToPoseAnalyzerSettings()
                },
                onPoseTrackerSettingsClick: {
                    viewModel.navigateToPoseTrackerSettings()
                },
                onPoseTrackerAdvancedSettingsClick: {
                    viewModel.navigateToPoseTrackerAdvancedSettings()
                },
                onDebugSettingsClick: {
                    viewModel.navigateToDebugSettings()
                },
                onOssLicensesClick: {
                    viewModel.navigateToOssLicenses()
                },
                onOnboardingClick: onOnboardingNavigate,
                onLaunchCameraTrackerClick: onCameraTrackerNavigate
            )
            .navigationTitle(Text(localized: "settings_top_title"))
            .navigationDestination(for: MainUiState.MainRoute.self) { route in
                switch route {
                case .vrcConnectionSettings:
                    VrcConnectionSettingsScreen()
                case .bodyMeasurementsSettings:
                    BodyMeasurementsSettingsScreen()
                case .PoseLandmarkerSettings:
                    PoseLandmarkerSettingsScreen()
                case .poseTrackerSettings:
                    PoseTrackerSettingsScreen()
                case .poseTrackerAdvancedSettings:
                    PoseTrackerAdvancedSettingsScreen()
                case .debugSettings:
                    DebugSettingsScreen()
                case .ossLicenses:
                    OssLicensesScreen()
                }
            }
        }
    }
}

#Preview {
    TopScreen(
        onOnboardingNavigate: {},
        onCameraTrackerNavigate: {}
    )
}
