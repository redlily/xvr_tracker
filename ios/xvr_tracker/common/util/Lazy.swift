import Foundation
import os.atomic

public final class Lazy<Value: Sendable>: Sendable {
    private let _value = OSAllocatedUnfairLock<Value?>(initialState: nil)
    public func get(_ factor: @Sendable () -> Value) -> Value {
        return _value.withLock {
            if let value = $0 { return value }
            let value = factor()
            $0 = value
            return value
        }
    }
    public init() {}
}
