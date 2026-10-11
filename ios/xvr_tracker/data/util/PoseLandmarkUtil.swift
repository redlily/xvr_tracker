import Foundation
import infra
import core_type
import common

extension PoseLandmarkType {
    public var category: BodyPartCategory {
        switch self {
        case .nose,
                .leftEyeInner,
                .leftEye,
                .leftEyeOuter,
                .rightEyeInner,
                .rightEye,
                .rightEyeOuter,
                .leftEar,
                .rightEar,
                .mouthLeft,
                .mouthRight:
            return .head
            
        case .leftShoulder,
                .rightShoulder,
                .leftHip,
                .rightHip:
            return .body
            
        case .leftElbow,
                .rightElbow,
                .leftWrist,
                .rightWrist,
                .leftPinky,
                .rightPinky,
                .leftIndex,
                .rightIndex,
                .leftThumb,
                .rightThumb:
            return .arm
            
        case .leftKnee,
                .rightKnee,
                .leftAnkle,
                .rightAnkle,
                .leftHeel,
                .rightHeel,
                .leftFootIndex,
                .rightFootIndex:
            return .leg
        @unknown default:
            fatalError()
        }
    }
    
    public var position: HorizontalPosition {
        switch self {
        case .nose,
                .leftEyeInner,
                .leftEye,
                .leftEyeOuter,
                .leftEar,
                .mouthLeft,
                .leftShoulder,
                .leftElbow,
                .leftWrist,
                .leftPinky,
                .leftIndex,
                .leftThumb,
                .leftHip,
                .leftKnee,
                .leftAnkle,
                .leftHeel,
                .leftFootIndex:
            return .left
            
        case .rightEyeInner,
                .rightEye,
                .rightEyeOuter,
                .rightEar,
                .mouthRight,
                .rightShoulder,
                .rightElbow,
                .rightWrist,
                .rightPinky,
                .rightIndex,
                .rightThumb,
                .rightHip,
                .rightKnee,
                .rightAnkle,
                .rightHeel,
                .rightFootIndex:
            return .right
        default:
            fatalError()
        }
    }
    
}
