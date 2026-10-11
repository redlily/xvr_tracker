import Foundation

public enum CameraLensFacingType: Int, Sendable, CaseIterable {
    case back = 0
    case front = 1
    case unknown = -1
    
    var value: Int {
        switch self {
        case .back:
            return 0
        case .front:
            return 1
        case .unknown:
            return -1
        }
    }
    
    static func fromId(_ id: Int) -> CameraLensFacingType {
        return CameraLensFacingType(rawValue: id) ?? .back
    }
}

extension Int {
    func toCameraLensFacingType() -> CameraLensFacingType {
        return CameraLensFacingType.fromId(self)
    }
}
