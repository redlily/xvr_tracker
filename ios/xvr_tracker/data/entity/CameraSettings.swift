import Foundation

public struct CameraSettings: Sendable, Equatable {
    public var lensFacingType: CameraLensFacingType
    public var zoomRatio: Double
    
    public static let `default` = CameraSettings(
        lensFacingType: .back,
        zoomRatio: 1.0
    )
}
