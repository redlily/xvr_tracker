import Foundation

extension Float {
    public func toRadian() -> Float {
        return self * .pi / 180.0
    }
    
    public func toDegree() -> Float {
        return self * 180.0 / .pi
    }
}

extension Double {
    public func toRadian() -> Double {
        return self * .pi / 180.0
    }
    
    public func toDegree() -> Double {
        return self * 180.0 / .pi
    }
}

