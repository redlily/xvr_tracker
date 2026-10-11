import Foundation
@_implementationOnly internal import MediaPipeTasksVision

public enum PoseLandmarkerComputeType: Int, CaseIterable, Sendable {
    case cpu = 0
    case gpu = 1
    
    static public func fromId(_ id: Int) -> PoseLandmarkerComputeType {
        return PoseLandmarkerComputeType(rawValue: id) ?? .cpu
    }
}

extension PoseLandmarkerComputeType {
    var nativeType: Delegate {
        switch self {
        case .cpu:
            return .CPU
        case .gpu:
            return .GPU
        @unknown default:
            return .CPU
        }
    }
}

extension Int {
    public func toPoseLandmarkerComputeType() -> PoseLandmarkerComputeType {
        return PoseLandmarkerComputeType.fromId(self)
    }
}
