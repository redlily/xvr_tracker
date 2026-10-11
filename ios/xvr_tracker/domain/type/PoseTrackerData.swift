import Foundation
import core_type
import common
import data

public struct PoseTrackerData: Sendable, Equatable, Encodable {
    public var dataType: PoseTrackerMotionType
    public var attitude: Matrix4x4
    public var position: Vector3
    public var axis: Axis3?
    
    public init(
        dataType: PoseTrackerMotionType,
        attitude: Matrix4x4,
        position: Vector3 = .zero,
        axis: Axis3? = nil
    ) {
        self.dataType = dataType
        self.attitude = attitude
        self.position = position
        self.axis = axis
    }
}

public struct PoseTrackersData: Sendable, Equatable, Encodable {
    public var trackers: [[PoseTrackerType: PoseTrackerData]]
    public var modificationMatrix: Matrix4x4
    public var timestamp: Int64
    
    public init(
        trackers: [[PoseTrackerType: PoseTrackerData]],
        modificationMatrix: Matrix4x4,
        timestamp: Int64
    ) {
        self.trackers = trackers
        self.modificationMatrix = modificationMatrix
        self.timestamp = timestamp
    }
    
    public static let empty = PoseTrackersData(
        trackers: [],
        modificationMatrix: .identity,
        timestamp: 0
    )
}

