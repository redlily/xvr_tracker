import Foundation

public struct UncheckedSendableBox<Value>: @unchecked Sendable {
    public let value: Value
    
    public init(_ value: Value) {
        self.value = value
    }
}

public extension UncheckedSendableBox where Value: Sendable {
    @available(*, unavailable, message: "Value is Sendable. Use Value directly; do not wrap it in UncheckedSendableBox.")
    init(_ value: Value) {
        self.value = value
    }
}
