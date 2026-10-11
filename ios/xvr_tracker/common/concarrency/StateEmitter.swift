import Foundation
import os.lock

public final class StateEmitter<Element: Sendable>: Emitter, Consumer, Sendable {
    private let _value: OSAllocatedUnfairLock<Element>
    public var value: Element {
        get {
            _value.withLock { $0 }
        }
        set {
            _value.withLock {
                $0 = newValue
                sharedEmitter.emit(newValue)
            }
        }
    }
    
    private let sharedEmitter = SharedEmitter<Element>()
    
    public init(_ value: Element) {
        self._value = OSAllocatedUnfairLock(initialState: value)
    }
    
    public func emit(_ value: Element) {
        self.value = value
    }
    
    public func finish() {
        sharedEmitter.finish()
    }
    
    public func stream(
        bufferingPolicy: AsyncStream<Element>.Continuation.BufferingPolicy = .unbounded
    ) -> AsyncStream<Element> {
        sharedEmitter.stream(bufferingPolicy: bufferingPolicy) { cont in
            cont.yield(self.value)
        }
    }
}

public struct StateConsumer<Element: Sendable> {
    private let base: StateEmitter<Element>
    
    public var value: Element { base.value }
    
    init(_ base: StateEmitter<Element>) {
        self.base = base
    }
    
    public func stream(
        bufferingPolicy: AsyncStream<Element>.Continuation.BufferingPolicy = .unbounded
    ) -> AsyncStream<Element> {
        base.stream(bufferingPolicy: bufferingPolicy)
    }
}

extension StateEmitter {
    @discardableResult
    public func update(_ body: @Sendable (inout Element) -> Void) -> Element {
        return _value.withLock {
            body(&$0)
            sharedEmitter.emit($0)
            return $0
        }
    }
    
    @discardableResult
    public func update(_ body: @Sendable (inout Element) -> Void) -> Element where Element: Equatable {
        return _value.withLock {
            let prev = $0
            body(&$0)
            if prev != $0 {
                sharedEmitter.emit($0)
            }
            return $0
        }
    }
    
    public func eraseToStateConsumer() -> StateConsumer<Element> {
        StateConsumer(self)
    }
}
