import SwiftUI
import Combine
import common
import data

@MainActor
class AppViewModel: ObservableObject {
    @Published var uiState: AppUiState = AppUiState()
    
    func onMainNavigate() {
        transiteScreen {
            uiState.navigationPath = [.top]
        }
    }
    
    func onOnboardingNavitate() {
        transiteScreen {
            if uiState.navigationPath.last == .launch {
                uiState.navigationPath = [.top, .onboarding]
            } else {
                uiState.navigationPath.append(.onboarding)
            }
        }
    }
    
    func onCameraTrackerNavigate() {
        transiteScreen {
            uiState.navigationPath.append(.cameraTracker)
        }
    }
    
    func onBackNavigate() {
        transiteScreen {
            if uiState.navigationPath.count <= 1 {
                uiState.navigationPath = [.top]
            } else {
                uiState.navigationPath.removeLast()
            }
        }
    }
    
    private func transiteScreen(action: () -> Void) {
        uiState.isTransiting = true
        withAnimation(.easeInOut) {
            action()
        } completion: { [weak self] in
            self?.uiState.isTransiting = false
        }
    }
}
