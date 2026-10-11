import Foundation
import data

public enum PoseTrackerType: String, Encodable, CodingKeyRepresentable, Sendable {
    case head = "head"
    case chest = "chest"
    case waist = "waist"
    case leftElbow = "left_elbow"
    case rightElbow = "right_elbow"
    case leftKnee = "left_knee"
    case rightKnee = "right_knee"
    case leftFeet = "left_feet"
    case rightFeet = "right_feet"
    
    public var alignment: HorizontalPosition {
        switch self {
        case .head, .chest, .waist:
            return .center
        case .leftElbow, .leftKnee, .leftFeet:
            return .left
        case .rightElbow, .rightKnee, .rightFeet:
            return .right
        }
    }
}

