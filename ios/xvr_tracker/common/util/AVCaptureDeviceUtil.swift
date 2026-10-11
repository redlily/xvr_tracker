import Foundation
import AVFoundation

public extension AVCaptureDevice {
    @inlinable
    func withLockForConfiguration(
        _ operation: (AVCaptureDevice) throws -> Void
    ) throws -> Void {
        try lockForConfiguration()
        defer {
            unlockForConfiguration()
        }
        try operation(self)
    }
}
