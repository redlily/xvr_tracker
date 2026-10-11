import Foundation

public protocol Emitter<Element>: Sendable {
    associatedtype Element: Sendable
    func emit(_ value: Element)
    func finish()
}

public struct AnyEmitter<Element: Sendable>: Emitter, Sendable {
    private let _emit: @Sendable (Element) -> Void
    private let _finish: @Sendable () -> Void
    
    public init<E: Emitter>(_ base: E) where E.Element == Element {
        self._emit = { value in base.emit(value) }
        self._finish = { base.finish() }
    }
    
    public func emit(_ value: Element) {
        _emit(value)
    }
    
    public func finish() {
        _finish()
    }
}

public extension Emitter {
    func eraseToAnyEmitter() -> AnyEmitter<Element> { AnyEmitter(self) }
}
