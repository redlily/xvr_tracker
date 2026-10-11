import Foundation
import UIKit
import AVFoundation

public struct CameraState: Equatable, @unchecked Sendable {
    public var lensFacingType: CameraLensFacingType
    public var zoomRatioRange: ClosedRange<Double>
    public var zoomRatio: Double
    public var orientation: UIDeviceOrientation
    public var isBound: Bool
    public var device: AVCaptureDevice?
    public var session: AVCaptureSession?
    
    public init(
        lensFacingType: CameraLensFacingType = .back,
        zoomRatioRange: ClosedRange<Double> = 1.0...1.0,
        zoomRatio: Double = 1.0,
        orientation: UIDeviceOrientation = .unknown,
        isBound: Bool = false,
        device: AVCaptureDevice? = nil,
        session: AVCaptureSession? = nil
    ) {
        self.lensFacingType = lensFacingType
        self.zoomRatioRange = zoomRatioRange
        self.zoomRatio = zoomRatio
        self.orientation = orientation
        self.isBound = isBound
        self.device = device
        self.session = session
    }
}
