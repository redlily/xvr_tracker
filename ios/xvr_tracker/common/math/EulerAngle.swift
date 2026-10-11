import Foundation

public struct EulerAngle: Sendable {
    public var alpha: Double
    public var beta: Double
    public var gamma: Double
    public let isDegree: Bool
    
    public init(
        alpha: Double,
        beta: Double,
        gamma: Double,
        isDegree: Bool = false
    ) {
        self.alpha = alpha
        self.beta = beta
        self.gamma = gamma
        self.isDegree = isDegree
    }
    
    public func toRadian() -> EulerAngle {
        if isDegree {
            return EulerAngle(
                alpha: alpha.toRadian(),
                beta: beta.toRadian(),
                gamma: gamma.toRadian(),
                isDegree: false
            )
        } else {
            return self
        }
    }
    
    public func toDegree() -> EulerAngle {
        if isDegree {
            return self
        } else {
            return EulerAngle(
                alpha: alpha.toDegree(),
                beta: beta.toDegree(),
                gamma: gamma.toDegree(),
                isDegree: true
            )
        }
    }
    
    public func toArray() -> [Double] {
        return [alpha, beta, gamma]
    }
    
    public static prefix func - (angle: EulerAngle) -> EulerAngle {
        return EulerAngle(
            alpha: -angle.alpha,
            beta: -angle.beta,
            gamma: -angle.gamma,
            isDegree: angle.isDegree
        )
    }
}

public func eulerAngleWithRadian(alpha: Double, beta: Double, gamma: Double) -> EulerAngle {
    return EulerAngle(alpha: alpha, beta: beta, gamma: gamma, isDegree: false)
}

public func eulerAngleWithDegree(alpha: Double, beta: Double, gamma: Double) -> EulerAngle {
    return EulerAngle(alpha: alpha, beta: beta, gamma: gamma, isDegree: true)
}

