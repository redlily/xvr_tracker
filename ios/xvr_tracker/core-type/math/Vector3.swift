import Foundation

public struct Vector3: Equatable, Encodable, Sendable {
    public static let zero = Vector3(x: 0, y: 0, z: 0)
    public static let x = Vector3(x: 1, y: 0, z: 0)
    public static let y = Vector3(x: 0, y: 1, z: 0)
    public static let z = Vector3(x: 0, y: 0, z: 1)
    
    public var x: Double
    public var y: Double
    public var z: Double
    
    public init(x: Double, y: Double, z: Double) {
        self.x = x
        self.y = y
        self.z = z
    }
}
