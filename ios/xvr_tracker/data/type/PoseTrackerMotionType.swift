import Foundation

public enum PoseTrackerMotionType: String, Encodable, CaseIterable, Sendable {
    case disable = "disable"
    case position = "position"
    case rotation = "rotation"
    case positionAndRotation = "position_and_rotation"
}

extension PoseTrackerMotionType {
    public var id: Int {
        switch self {
        case .disable:
            return 0
        case .position:
            return 1
        case .rotation:
            return 2
        case .positionAndRotation:
            return 3
        }
    }
    
    public var isPositionEnabled: Bool {
        switch self {
        case .disable, .rotation:
            return false
        case .position, .positionAndRotation:
            return true
        }
    }
    
    public var isRotationEnabled: Bool {
        switch self {
        case .disable, .position:
            return false
        case .rotation, .positionAndRotation:
            return true
        }
    }
    
    static public func fromId(_ id: Int) -> PoseTrackerMotionType {
        switch id {
        case 0:
            return .disable
        case 1:
            return .position
        case 2:
            return .rotation
        case 3:
            return .positionAndRotation
        default:
            return .disable
        }
    }
}

extension Int {
    public func toPoseTrackerMotionType() -> PoseTrackerMotionType {
        return PoseTrackerMotionType.fromId(self)
    }
}
