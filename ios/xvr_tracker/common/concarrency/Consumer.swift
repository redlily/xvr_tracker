import Foundation

public protocol Consumer<Element>: Sendable {
    associatedtype Element: Sendable
    func stream(
        bufferingPolicy: AsyncStream<Element>.Continuation.BufferingPolicy
    ) -> AsyncStream<Element>
}

public struct AnyConsumer<Element: Sendable>: Consumer, Sendable {
    private let _stream: @Sendable (
        AsyncStream<Element>.Continuation.BufferingPolicy
    ) -> AsyncStream<Element>
    
    public init<E: Consumer>(_ base: E) where E.Element == Element {
        self._stream = { bufferingPolicy in base.stream(bufferingPolicy: bufferingPolicy) }
    }
    
    public func stream(
        bufferingPolicy: AsyncStream<Element>.Continuation.BufferingPolicy
    ) -> AsyncStream<Element> {
        _stream(bufferingPolicy)
    }
}

public extension Consumer {
    func eraseToAnyConsumer() -> AnyConsumer<Element> { AnyConsumer(self) }
}
