import Foundation
import Observation

struct MainUiState {
    var navigationPath: [MainRoute] = []
    
    enum MainRoute: Hashable {
        case vrcConnectionSettings
        case bodyMeasurementsSettings
        case PoseLandmarkerSettings
        case poseTrackerSettings
        case poseTrackerAdvancedSettings
        case debugSettings
        case ossLicenses
    }
}
