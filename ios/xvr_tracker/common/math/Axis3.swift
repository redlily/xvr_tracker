import Foundation
import core_type

public struct Axis3: Sendable, Equatable, Encodable {
    public var x: Vector3
    public var y: Vector3
    public var z: Vector3
    
    public init(x: Vector3, y: Vector3, z: Vector3) {
        self.x = x
        self.y = y
        self.z = z
    }
    
    public static func xzy(x: Vector3, ref: Vector3) -> Axis3 {
        let nx = x.normalized()
        let nz = (x.cross(ref)).normalized()
        return Axis3(x: nx, y: nz.cross(nx), z: nz)
    }
    
    public static func yzx(y: Vector3, ref: Vector3) -> Axis3 {
        let ny = y.normalized()
        let nz = (y.cross(ref)).normalized()
        return Axis3(x: ny.cross(nz), y: ny, z: nz)
    }
    
    public static func zxy(z: Vector3, ref: Vector3) -> Axis3 {
        let nz = z.normalized()
        let nx = (ref.cross(nz)).normalized()
        return Axis3(x: nx, y: nz.cross(nx), z: nz)
    }
    
    public static func zyx(z: Vector3, ref: Vector3) -> Axis3 {
        let nz = z.normalized()
        let ny = (nz.cross(ref)).normalized()
        return Axis3(x: ny.cross(nz), y: ny, z: nz)
    }
    
    public func xzy(ref: Vector3) -> Axis3 {
        let nx = x.normalized()
        let nz = (x.cross(ref)).normalized()
        return Axis3(x: nx, y: nz.cross(nx), z: nz)
    }
    
    public func zxy(ref: Vector3) -> Axis3 {
        let nz = z.normalized()
        let nx = (ref.cross(nz)).normalized()
        return Axis3(x: nx, y: nz.cross(nx), z: nz)
    }
    
    public func zyx(ref: Vector3) -> Axis3 {
        let nz = z.normalized()
        let ny = (nz.cross(ref)).normalized()
        return Axis3(x: ny.cross(nz), y: ny, z: nz)
    }
    
    public func toMatrix4x4() -> Matrix4x4 {
        return Matrix4x4.column(axis: self, t: .zero)
    }
    
    public func toEulerAngleZXY(degree: Bool = false) -> EulerAngle {
        let xx = x.x
        let xy = x.y
        let yx = y.x
        let yy = y.y
        let zx = z.x
        let zy = z.y
        let zz = z.z
        
        let beta = atan2(zy, sqrt(zx * zx + zz * zz))
        let alpha: Double
        let gamma: Double
        
        if beta != 0.0 {
            alpha = atan2(-xy, yy)
            gamma = atan2(-zx, zz)
        } else {
            alpha = atan2(yx, xx)
            gamma = 0.0
        }
        
        if !degree {
            return eulerAngleWithRadian(
                alpha: alpha,
                beta: beta,
                gamma: gamma
            )
        } else {
            return eulerAngleWithDegree(
                alpha: alpha,
                beta: beta,
                gamma: gamma
            )
        }
    }
    
    public static let identity = Axis3(x: .x, y: .y, z: .z)
}

extension Matrix4x4 {
    public static func * (lhs: Matrix4x4, rhs: Axis3) -> Axis3 {
        return Axis3(
            x: lhs * rhs.x,
            y: lhs * rhs.y,
            z: lhs * rhs.z
        )
    }
}

