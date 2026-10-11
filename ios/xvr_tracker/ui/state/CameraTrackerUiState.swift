import Foundation
import AVFoundation
import infra
import data
import domain

struct CameraTrackerUiState: Sendable, Equatable {
    var lensFacingType: CameraLensFacingType = .back
    var zoomRatio: Double? = nil
    var maxZoomRatio: Double = 10.0
    var contentState: ContentState = .initializing
    var dialogState: DialogState? = nil
    
    enum ContentState: Sendable, Equatable {
        case initializing
        case cameraActive(content: CameraActiveContent)
        case cameraPermissionDenied
    }
    
    enum DialogState: Sendable, Equatable {
        case cameraPermissionDenied
        case privateNetworkNotConnected
        case destinationAddressResolutionFailed
        case globalAddressSpecified
        case localNetworkDenied
        case unknownNetworkError
    }
}

struct CameraActiveContent: Sendable, Equatable {
    var poseLandmarksData: PoseLandmarksData = PoseLandmarksData.empty
    var poseTrackersData: PoseTrackersData = PoseTrackersData.empty
    var visibilityThreshold: Double = 0
    var presenceThreshold: Double = 0
}
