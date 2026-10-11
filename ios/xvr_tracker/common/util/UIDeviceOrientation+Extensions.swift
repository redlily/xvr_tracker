import Foundation
import UIKit

extension UIDeviceOrientation {
    public var interfaceMask: UIInterfaceOrientationMask? {
        switch self {
        case .portrait: return .portrait
        case .portraitUpsideDown: return .portraitUpsideDown
        case .landscapeLeft: return .landscapeRight
        case .landscapeRight: return .landscapeLeft
        default: return nil
        }
    }
    
    public var angleDegrees: Float {
        switch self {
        case .portrait: return 90
        case .landscapeLeft: return 0
        case .portraitUpsideDown: return 270
        case .landscapeRight: return 180
        default: return 0
        }
    }
}
