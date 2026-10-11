import Foundation

public struct RangedValue<Value: Comparable>: Sendable, Equatable where Value: Sendable {
    public let value: Value
    public let range: ClosedRange<Value>
    
    public init(_ value: Value, in range: ClosedRange<Value>) {
        let clampedValue: Value
        if value < range.lowerBound {
            clampedValue = range.lowerBound
        } else if value > range.upperBound {
            clampedValue = range.upperBound
        } else {
            clampedValue = value
        }
        self.value = clampedValue
        self.range = range
    }
    
    public func contains(_ value: Value) -> Bool {
        return range.contains(value)
    }
    
    public func update(_ value: Value) -> RangedValue<Value> {
        return RangedValue(value, in: range)
    }
}
