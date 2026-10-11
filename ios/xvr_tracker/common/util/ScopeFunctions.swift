import Foundation

@inlinable
public func update<T>(_ value: T, _ block: (inout T) -> Void) -> T {
    var copy = value
    block(&copy)
    return copy
}
