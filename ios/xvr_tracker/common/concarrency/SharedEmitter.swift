import Foundation
import os.lock

public final class SharedEmitter<Element: Sendable>: Emitter, Consumer, Sendable {
    private struct State {
        var isFinished = false
        var continuations: [UUID: AsyncStream<Element>.Continuation] = [:]
    }
    
    private let state = OSAllocatedUnfairLock(initialState: State())
    
    public var isFinished: Bool { state.withLock { $0.isFinished } }
    public var subscriberCount: Int { state.withLock { $0.continuations.count } }
    
    public init() {}
    
    deinit {
        finish()
    }
    
    public func stream(
        bufferingPolicy: AsyncStream<Element>.Continuation.BufferingPolicy = .unbounded
    ) -> AsyncStream<Element> {
        return internalStream(
            bufferingPolicy: bufferingPolicy,
            onSubscribe: nil
        )
    }
    
    public func stream(
        bufferingPolicy: AsyncStream<Element>.Continuation.BufferingPolicy = .unbounded,
        onSubscribe: @escaping @Sendable (AsyncStream<Element>.Continuation) -> Void
    ) -> AsyncStream<Element> {
        return internalStream(
            bufferingPolicy: bufferingPolicy,
            onSubscribe: onSubscribe
        )
    }
    
    public func stream(
        bufferingPolicy: AsyncStream<Element>.Continuation.BufferingPolicy = .unbounded,
        buffer: Array<Element>
    ) -> AsyncStream<Element> {
        return internalStream(
            bufferingPolicy: bufferingPolicy,
            onSubscribe: { cont in
                for value in buffer {
                    cont.yield(value)
                }
            }
        )
    }
    
    private func internalStream(
        bufferingPolicy: AsyncStream<Element>.Continuation.BufferingPolicy = .unbounded,
        onSubscribe: (@Sendable (AsyncStream<Element>.Continuation) -> Void)? = nil
    ) -> AsyncStream<Element> {
        return AsyncStream<Element>(bufferingPolicy: bufferingPolicy) { cont in
            let id = UUID()
            let isFinished = state.withLock { state in
                if state.isFinished { return true }
                state.continuations[id] = cont
                if let onSubscribe {
                    onSubscribe(cont)
                }
                return false
            }
            if isFinished {
                cont.finish()
                return
            }
            cont.onTermination = { [weak self] _ in
                _ = self?.state.withLock { state in
                    state.continuations.removeValue(forKey: id)
                }
            }
        }
    }
    
    public func emit(_ value: Element) {
        state.withLock { state -> [AsyncStream<Element>.Continuation]? in
            if state.isFinished {
                return nil
            } else {
                return Array(state.continuations.values)
            }
        }?.forEach { continuation in
            continuation.yield(value)
        }
    }
    
    public func finish() {
        state.withLock { state -> [AsyncStream<Element>.Continuation]? in
            if state.isFinished { return nil }
            let continuations = Array(state.continuations.values)
            state.isFinished = true
            state.continuations.removeAll()
            return continuations
        }?.forEach { continuation in
            continuation.finish()
        }
    }
}

public struct SharedConsumer<Element: Sendable>: Sendable {
    private let base: SharedEmitter<Element>
    
    init(_ base: SharedEmitter<Element>) {
        self.base = base
    }
    
    public func stream(
        bufferingPolicy: AsyncStream<Element>.Continuation.BufferingPolicy = .unbounded
    ) -> AsyncStream<Element> {
        base.stream(bufferingPolicy: bufferingPolicy)
    }
}

extension SharedEmitter {
    public func eraseToSharedConsumer() -> SharedConsumer<Element> {
        SharedConsumer(self)
    }
}
