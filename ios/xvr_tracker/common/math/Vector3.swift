import Foundation
import Atomics
import core_type

public extension Vector3 {
    public var isZero: Bool {
        x == 0 && y == 0 && z == 0
    }
    
    public var length: Double {
        sqrt(x * x + y * y + z * z)
    }
    
    public func normalized() -> Vector3 {
        let len = length
        guard len > 0 else { return .zero }
        return Vector3(x: x / len, y: y / len, z: z / len)
    }
    
    public func dot(_ other: Vector3) -> Double {
        x * other.x + y * other.y + z * other.z
    }
    
    public func cross(_ other: Vector3) -> Vector3 {
        Vector3(
            x: y * other.z - z * other.y,
            y: z * other.x - x * other.z,
            z: x * other.y - y * other.x
        )
    }
    
    public static func + (lhs: Vector3, rhs: Vector3) -> Vector3 {
        Vector3(x: lhs.x + rhs.x, y: lhs.y + rhs.y, z: lhs.z + rhs.z)
    }
    
    public static prefix func + (value: Vector3) -> Vector3 {
        value
    }
    
    public static func - (lhs: Vector3, rhs: Vector3) -> Vector3 {
        Vector3(x: lhs.x - rhs.x, y: lhs.y - rhs.y, z: lhs.z - rhs.z)
    }
    
    public static prefix func - (value: Vector3) -> Vector3 {
        Vector3(x: -value.x, y: -value.y, z: -value.z)
    }
    
    public static func * (lhs: Vector3, rhs: Double) -> Vector3 {
        Vector3(x: lhs.x * rhs, y: lhs.y * rhs, z: lhs.z * rhs)
    }
    
    public static func * (lhs: Double, rhs: Vector3) -> Vector3 {
        Vector3(x: lhs * rhs.x, y: lhs * rhs.y, z: lhs * rhs.z)
    }
    
    public static func / (lhs: Vector3, rhs: Double) -> Vector3 {
        Vector3(x: lhs.x / rhs, y: lhs.y / rhs, z: lhs.z / rhs)
    }
}

extension Vector3 {
    public struct DotCross: Equatable, Sendable {
        public let dot: Double
        public let cross: Vector3
    }
    
    public static func center(_ a: Vector3, _ b: Vector3) -> Vector3 {
        (a + b) / 2
    }
    
    public static func center(_ a: Vector3?, _ b: Vector3?) -> Vector3? {
        guard let a, let b else { return nil }
        return center(a, b)
    }
    
    public static func direction(from: Vector3, to: Vector3) -> Vector3 {
        (to - from).normalized()
    }
    
    public static func direction(from: Vector3?, to: Vector3?) -> Vector3? {
        guard let from, let to else { return nil }
        return direction(from: from, to: to)
    }
    
    public func dotCross(_ other: Vector3) -> DotCross {
        DotCross(dot: dot(other), cross: cross(other))
    }
}
