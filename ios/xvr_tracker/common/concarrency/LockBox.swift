import Foundation

public final actor LockBox<Value> {
    private var value: Value

    public init(_ value: Value) {
        self.value = value
    }

    @discardableResult
    public func withLock<R>(
        _ body: (inout Value) throws -> R
    ) rethrows -> R {
        try body(&value)
    }
}
