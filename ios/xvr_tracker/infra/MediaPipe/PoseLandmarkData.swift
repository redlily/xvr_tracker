import Foundation
import core_type

public struct PoseLandmarkData : Sendable, Equatable, Encodable {
    public var normalizedLandmark: Vector3
    public var worldLandmark: Vector3
    public var visibility: Double
    public var presence: Double
    
    public init(
        normalizedLandmark: Vector3,
        worldLandmark: Vector3,
        visibility: Double,
        presence: Double
    ) {
        self.normalizedLandmark = normalizedLandmark
        self.worldLandmark = worldLandmark
        self.visibility = visibility
        self.presence = presence
    }
}

public struct PoseLandmarksData : Sendable, Equatable, Encodable {
    public var imageSize: Size2D
    public var landmarks: [[PoseLandmarkType: PoseLandmarkData]]
    public var timestamp: Int
    
    public init(
        imageSize: Size2D,
        landmarks: [[PoseLandmarkType : PoseLandmarkData]],
        timestamp: Int
    ) {
        self.imageSize = imageSize
        self.landmarks = landmarks
        self.timestamp = timestamp
    }
    
    public static let empty = PoseLandmarksData(
        imageSize: .zero,
        landmarks: [],
        timestamp: 0
    )
}
