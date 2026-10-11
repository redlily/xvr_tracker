import Foundation

extension Bool {
    @inlinable
    @discardableResult
    public func onTrue(_ block: () throws -> Void) rethrows -> Bool {
        if self {
            try block()
        }
        return self
    }
    
    @inlinable
    @discardableResult
    public func onFalse(_ block: () throws -> Void) rethrows -> Bool {
        if !self {
            try block()
        }
        return self
    }
}
