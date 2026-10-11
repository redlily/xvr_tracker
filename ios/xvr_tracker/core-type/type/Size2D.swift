import Foundation

public struct Size2D: Sendable, Equatable, Encodable {
    public var width: Double
    public var height: Double
    
    public init(width: Double, height: Double) {
        self.width = width
        self.height = height
    }
    
    public static let zero = Size2D(width: 0, height: 0)
}
