import SwiftUI
import di

public struct AppScreen: View {
    private let isStatusBarHidden = false
    
    @StateObject var viewModel: AppViewModel = AppViewModel()
    
    public init() {
    }
    
    public var body: some View {
        ZStack {
            ForEach(Array(viewModel.uiState.navigationPath.enumerated()), id: \.offset) { index, path in
                let value = screenView(path: path)
                    .background(Color(.systemBackground))
                    .zIndex(Double(index))
                
                if index < viewModel.uiState.navigationPath.count - 1 {
                    value
                        .hidden()
                        .allowsTightening(false)
                        .accessibilityHidden(true)
                }
                
                value
            }
        }
        .statusBarHidden(isStatusBarHidden)
    }
    
    @ViewBuilder
    private func screenView(path: AppUiState.AppRoute) -> some View {
        switch path {
        case .launch:
            LaunchScreen(
                onOnboardingNavigate: viewModel.onOnboardingNavitate,
                onTopNavigate: viewModel.onMainNavigate
            )
            
        case .onboarding:
            OnboardingScreen(
                onOnboardingExit: {
                    viewModel.onBackNavigate()
                }
            )
            .transition(.move(edge: .bottom))
        
        case .top:
            TopScreen(
                onOnboardingNavigate: {
                    viewModel.onOnboardingNavitate()
                },
                onCameraTrackerNavigate: {
                    viewModel.onCameraTrackerNavigate()
                }
            )
            .transition(.move(edge: .bottom))
            
        case .cameraTracker:
            CameraTrackerScreen(
                isTransiting: viewModel.uiState.isTransiting,
                onBackNavigate: {
                    viewModel.onBackNavigate()
                }
            )
            .transition(.move(edge: .bottom))
        }
    }
}
