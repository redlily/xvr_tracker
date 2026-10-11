import Foundation

public struct PoseTrackerSettings: Sendable, Equatable {
    public var head: PoseTrackerMotionType
    public var chest: PoseTrackerMotionType
    public var waist: PoseTrackerMotionType
    public var elbow: PoseTrackerMotionType
    public var knee: PoseTrackerMotionType
    public var feet: PoseTrackerMotionType
    
    public static let `default` = PoseTrackerSettings(
        head: .positionAndRotation,
        chest: .positionAndRotation,
        waist: .positionAndRotation,
        elbow: .disable,
        knee: .disable,
        feet: .positionAndRotation
    )
}
