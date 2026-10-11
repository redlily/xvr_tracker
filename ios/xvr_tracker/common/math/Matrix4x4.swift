import Foundation
import core_type

public struct Matrix4x4: Sendable, Equatable, Encodable {
    public var m00: Double
    public var m01: Double
    public var m02: Double
    public var m03: Double
    public var m10: Double
    public var m11: Double
    public var m12: Double
    public var m13: Double
    public var m20: Double
    public var m21: Double
    public var m22: Double
    public var m23: Double
    public var m30: Double
    public var m31: Double
    public var m32: Double
    public var m33: Double
    
    public init(
        m00: Double, m01: Double, m02: Double, m03: Double,
        m10: Double, m11: Double, m12: Double, m13: Double,
        m20: Double, m21: Double, m22: Double, m23: Double,
        m30: Double, m31: Double, m32: Double, m33: Double
    ) {
        self.m00 = m00
        self.m01 = m01
        self.m02 = m02
        self.m03 = m03
        self.m10 = m10
        self.m11 = m11
        self.m12 = m12
        self.m13 = m13
        self.m20 = m20
        self.m21 = m21
        self.m22 = m22
        self.m23 = m23
        self.m30 = m30
        self.m31 = m31
        self.m32 = m32
        self.m33 = m33
    }
    
    public static let identity = Matrix4x4(
        m00: 1, m01: 0, m02: 0, m03: 0,
        m10: 0, m11: 1, m12: 0, m13: 0,
        m20: 0, m21: 0, m22: 1, m23: 0,
        m30: 0, m31: 0, m32: 0, m33: 1
    )
    
    public static func column(
        x: Vector3,
        y: Vector3,
        z: Vector3,
        t: Vector3
    ) -> Matrix4x4 {
        Matrix4x4(
            m00: x.x, m01: y.x, m02: z.x, m03: t.x,
            m10: x.y, m11: y.y, m12: z.y, m13: t.y,
            m20: x.z, m21: y.z, m22: z.z, m23: t.z,
            m30: 0, m31: 0, m32: 0, m33: 1
        )
    }
    
    public static func column(
        axis: Axis3,
        t: Vector3
    ) -> Matrix4x4 {
        column(x: axis.x, y: axis.y, z: axis.z, t: t)
    }
    
    public static func scale(_ x: Double, _ y: Double, _ z: Double) -> Matrix4x4 {
        Matrix4x4(
            m00: x, m01: 0, m02: 0, m03: 0,
            m10: 0, m11: y, m12: 0, m13: 0,
            m20: 0, m21: 0, m22: z, m23: 0,
            m30: 0, m31: 0, m32: 0, m33: 1
        )
    }
    
    public static func rotation(
        axis: Vector3,
        angle: Double,
        isDegree: Bool = false
    ) -> Matrix4x4 {
        let normalizedAxis = axis.normalized()
        guard !normalizedAxis.isZero else { return .identity }
        let radian = isDegree ? angle.toRadian() : angle
        let cs = cos(radian)
        let sn = sin(radian)
        let omcs = 1 - cs
        let x = normalizedAxis.x
        let y = normalizedAxis.y
        let z = normalizedAxis.z
        return Matrix4x4(
            m00: x * x * omcs + cs, m01: x * y * omcs - z * sn, m02: x * z * omcs + y * sn, m03: 0,
            m10: x * y * omcs + z * sn, m11: y * y * omcs + cs, m12: y * z * omcs - x * sn, m13: 0,
            m20: x * z * omcs - y * sn, m21: y * z * omcs + x * sn, m22: z * z * omcs + cs, m23: 0,
            m30: 0, m31: 0, m32: 0, m33: 1
        )
    }
    
    public static func rotation(
        from: Vector3,
        to: Vector3,
        rate: Double = 1
    ) -> Matrix4x4 {
        let nf = from.normalized()
        let nt = to.normalized()
        let cs = max(-1.0, min(1.0, nf.dot(nt)))
        if cs >= 0.9999 {
            return .identity
        }
        if cs <= -0.9999 {
            let reference = abs(nf.x) < 0.1 ? Vector3(x: 1, y: 0, z: 0) : Vector3(x: 0, y: 1, z: 0)
            let axis = nf.cross(reference).normalized()
            if axis.isZero {
                return .identity
            }
            return rotation(axis: axis, angle: Double.pi * rate)
        }
        let axis = nf.cross(nt).normalized()
        if axis.isZero {
            return .identity
        }
        let angle = acos(cs) * rate
        return rotation(axis: axis, angle: angle)
    }
    
    public static func translation(_ x: Double, _ y: Double, _ z: Double) -> Matrix4x4 {
        return Matrix4x4(
            m00: 1, m01: 0, m02: 0, m03: x,
            m10: 0, m11: 1, m12: 0, m13: y,
            m20: 0, m21: 0, m22: 1, m23: z,
            m30: 0, m31: 0, m32: 0, m33: 1
        )
    }
    
    public static func translation(_ vector: Vector3) -> Matrix4x4 {
        return translation(vector.x, vector.y, vector.z)
    }
    
    public static func * (lhs: Matrix4x4, rhs: Matrix4x4) -> Matrix4x4 {
        return Matrix4x4(
            m00: lhs.m00 * rhs.m00 + lhs.m01 * rhs.m10 + lhs.m02 * rhs.m20 + lhs.m03 * rhs.m30,
            m01: lhs.m00 * rhs.m01 + lhs.m01 * rhs.m11 + lhs.m02 * rhs.m21 + lhs.m03 * rhs.m31,
            m02: lhs.m00 * rhs.m02 + lhs.m01 * rhs.m12 + lhs.m02 * rhs.m22 + lhs.m03 * rhs.m32,
            m03: lhs.m00 * rhs.m03 + lhs.m01 * rhs.m13 + lhs.m02 * rhs.m23 + lhs.m03 * rhs.m33,
            m10: lhs.m10 * rhs.m00 + lhs.m11 * rhs.m10 + lhs.m12 * rhs.m20 + lhs.m13 * rhs.m30,
            m11: lhs.m10 * rhs.m01 + lhs.m11 * rhs.m11 + lhs.m12 * rhs.m21 + lhs.m13 * rhs.m31,
            m12: lhs.m10 * rhs.m02 + lhs.m11 * rhs.m12 + lhs.m12 * rhs.m22 + lhs.m13 * rhs.m32,
            m13: lhs.m10 * rhs.m03 + lhs.m11 * rhs.m13 + lhs.m12 * rhs.m23 + lhs.m13 * rhs.m33,
            m20: lhs.m20 * rhs.m00 + lhs.m21 * rhs.m10 + lhs.m22 * rhs.m20 + lhs.m23 * rhs.m30,
            m21: lhs.m20 * rhs.m01 + lhs.m21 * rhs.m11 + lhs.m22 * rhs.m21 + lhs.m23 * rhs.m31,
            m22: lhs.m20 * rhs.m02 + lhs.m21 * rhs.m12 + lhs.m22 * rhs.m22 + lhs.m23 * rhs.m32,
            m23: lhs.m20 * rhs.m03 + lhs.m21 * rhs.m13 + lhs.m22 * rhs.m23 + lhs.m23 * rhs.m33,
            m30: lhs.m30 * rhs.m00 + lhs.m31 * rhs.m10 + lhs.m32 * rhs.m20 + lhs.m33 * rhs.m30,
            m31: lhs.m30 * rhs.m01 + lhs.m31 * rhs.m11 + lhs.m32 * rhs.m21 + lhs.m33 * rhs.m31,
            m32: lhs.m30 * rhs.m02 + lhs.m31 * rhs.m12 + lhs.m32 * rhs.m22 + lhs.m33 * rhs.m32,
            m33: lhs.m30 * rhs.m03 + lhs.m31 * rhs.m13 + lhs.m32 * rhs.m23 + lhs.m33 * rhs.m33
        )
    }
    
    public static func * (lhs: Matrix4x4, rhs: Vector3) -> Vector3 {
        let w = lhs.m30 * rhs.x + lhs.m31 * rhs.y + lhs.m32 * rhs.z + lhs.m33
        return Vector3(
            x: (lhs.m00 * rhs.x + lhs.m01 * rhs.y + lhs.m02 * rhs.z + lhs.m03) / w,
            y: (lhs.m10 * rhs.x + lhs.m11 * rhs.y + lhs.m12 * rhs.z + lhs.m13) / w,
            z: (lhs.m20 * rhs.x + lhs.m21 * rhs.y + lhs.m22 * rhs.z + lhs.m23) / w
        )
    }
    
    public func columnVector3(_ index: Int) -> Vector3 {
        switch index {
        case 0:
            return Vector3(x: m00, y: m10, z: m20)
        case 1:
            return Vector3(x: m01, y: m11, z: m21)
        case 2:
            return Vector3(x: m02, y: m12, z: m22)
        case 3:
            return Vector3(x: m03, y: m13, z: m23)
        default:
            fatalError("Invalid column index: \(index)")
        }
    }
    
    public func transpose() -> Matrix4x4 {
        return Matrix4x4(
            m00: m00, m01: m10, m02: m20, m03: m30,
            m10: m01, m11: m11, m12: m21, m13: m31,
            m20: m02, m21: m12, m22: m22, m23: m32,
            m30: m03, m31: m13, m32: m23, m33: m33
        )
    }
    
    public func toEulerAngleZXY(isDegree: Bool = false) -> EulerAngle {
        let epsilon: Double = 1e-6
        let beta = atan2(m21, sqrt(m20 * m20 + m22 * m22))
        let alpha: Double
        let gamma: Double
        
        if abs(beta) > epsilon {
            alpha = atan2(-m01, m11)
            gamma = atan2(-m20, m22)
        } else {
            alpha = atan2(m10, m00)
            gamma = 0
        }
        
        if !isDegree {
            return eulerAngleWithRadian(alpha: alpha, beta: beta, gamma: gamma)
        } else {
            return eulerAngleWithDegree(alpha: alpha.toDegree(), beta: beta.toDegree(), gamma: gamma.toDegree())
        }
    }
}
