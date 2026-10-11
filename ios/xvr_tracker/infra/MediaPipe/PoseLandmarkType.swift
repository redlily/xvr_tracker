import Foundation

public enum PoseLandmarkType: String, Sendable, Codable, CodingKeyRepresentable {
    case nose = "nose"
    case leftEyeInner = "left_eye_inner"
    case leftEye = "left_eye"
    case leftEyeOuter = "left_eye_outer"
    case rightEyeInner = "right_eye_inner"
    case rightEye = "right_eye"
    case rightEyeOuter = "right_eye_outer"
    case leftEar = "left_ear"
    case rightEar = "right_ear"
    case mouthLeft = "mouth_left"
    case mouthRight = "mouth_right"
    case leftShoulder = "left_shoulder"
    case rightShoulder = "right_shoulder"
    case leftElbow = "left_elbow"
    case rightElbow = "right_elbow"
    case leftWrist = "left_wrist"
    case rightWrist = "right_wrist"
    case leftPinky = "left_pinky"
    case rightPinky = "right_pinky"
    case leftIndex = "left_index"
    case rightIndex = "right_index"
    case leftThumb = "left_thumb"
    case rightThumb = "right_thumb"
    case leftHip = "left_hip"
    case rightHip = "right_hip"
    case leftKnee = "left_knee"
    case rightKnee = "right_knee"
    case leftAnkle = "left_ankle"
    case rightAnkle = "right_ankle"
    case leftHeel = "left_heel"
    case rightHeel = "right_heel"
    case leftFootIndex = "left_foot_index"
    case rightFootIndex = "right_foot_index"
}

extension PoseLandmarkType {
    static public func fromId(_ id: Int) -> PoseLandmarkType? {
        switch id {
        case 0: return .nose
        case 1: return .leftEyeInner
        case 2: return .leftEye
        case 3: return .leftEyeOuter
        case 4: return .rightEyeInner
        case 5: return .rightEye
        case 6: return .rightEyeOuter
        case 7: return .leftEar
        case 8: return .rightEar
        case 9: return .mouthLeft
        case 10: return .mouthRight
        case 11: return .leftShoulder
        case 12: return .rightShoulder
        case 13: return .leftElbow
        case 14: return .rightElbow
        case 15: return .leftWrist
        case 16: return .rightWrist
        case 17: return .leftPinky
        case 18: return .rightPinky
        case 19: return .leftIndex
        case 20: return .rightIndex
        case 21: return .leftThumb
        case 22: return .rightThumb
        case 23: return .leftHip
        case 24: return .rightHip
        case 25: return .leftKnee
        case 26: return .rightKnee
        case 27: return .leftAnkle
        case 28: return .rightAnkle
        case 29: return .leftHeel
        case 30: return .rightHeel
        case 31: return .leftFootIndex
        case 32: return .rightFootIndex
        default: return nil
        }
    }
    
    public var id: Int {
        switch self {
        case .nose: return 0
        case .leftEyeInner: return 1
        case .leftEye: return 2
        case .leftEyeOuter: return 3
        case .rightEyeInner: return 4
        case .rightEye: return 5
        case .rightEyeOuter: return 6
        case .leftEar: return 7
        case .rightEar: return 8
        case .mouthLeft: return 9
        case .mouthRight: return 10
        case .leftShoulder: return 11
        case .rightShoulder: return 12
        case .leftElbow: return 13
        case .rightElbow: return 14
        case .leftWrist: return 15
        case .rightWrist: return 16
        case .leftPinky: return 17
        case .rightPinky: return 18
        case .leftIndex: return 19
        case .rightIndex: return 20
        case .leftThumb: return 21
        case .rightThumb: return 22
        case .leftHip: return 23
        case .rightHip: return 24
        case .leftKnee: return 25
        case .rightKnee: return 26
        case .leftAnkle: return 27
        case .rightAnkle: return 28
        case .leftHeel: return 29
        case .rightHeel: return 30
        case .leftFootIndex: return 31
        case .rightFootIndex: return 32
        }
    }
    
    public var parent: PoseLandmarkType? {
        switch self {
        case .leftElbow:
            return .leftShoulder
        case .rightElbow:
            return .rightShoulder
        case .leftWrist:
            return .leftElbow
        case .rightWrist:
            return .rightElbow
        case .leftPinky, .leftIndex, .leftThumb:
            return .leftWrist
        case .rightPinky, .rightIndex, .rightThumb:
            return .rightWrist
        case .leftKnee:
            return .leftHip
        case .rightKnee:
            return .rightHip
        case .leftAnkle:
            return .leftKnee
        case .rightAnkle:
            return .rightKnee
        case .leftHeel, .leftFootIndex:
            return .leftAnkle
        case .rightHeel, .rightFootIndex:
            return .rightAnkle
        default:
            return nil
        }
    }
}

extension Int {
    public func toPoseLandmarkType() -> PoseLandmarkType? {
        return PoseLandmarkType.fromId(self)
    }
}
