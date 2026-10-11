import Foundation

public enum PoseMotionType: String, Sendable, Codable, CodingKeyRepresentable {
    case stand = "stand"
    case sit = "sit"
    case lieDown = "lie_down"
}
