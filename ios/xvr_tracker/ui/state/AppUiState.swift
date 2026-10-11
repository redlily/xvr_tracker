import Foundation
import Observation

struct AppUiState {
    var navigationPath: [AppRoute] = [.launch]
    var isTransiting: Bool = false
    
    enum AppRoute: Hashable {
        case launch
        case onboarding
        case top
        case cameraTracker
    }
}
