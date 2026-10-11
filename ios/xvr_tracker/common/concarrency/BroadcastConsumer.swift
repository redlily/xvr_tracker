import Foundation
import os.lock

public final class BroadcastConsumer<Element: Sendable>: Consumer, Sendable {
    private struct Status {
        var stream: AsyncStream<Element>?
        var task: Task<Void, Never>? = nil
        var isFinished = false
        var continuations: [UUID: AsyncStream<Element>.Continuation] = [:]
        
    }
    
    private let status: OSAllocatedUnfairLock<Status>
    private let priority: TaskPriority?
    
    public var isFinished: Bool { status.withLock { $0.isFinished } }
    public var subscriberCount: Int { status.withLock { $0.continuations.count } }
    
    public init(
        stream: AsyncStream<Element>,
        priority: TaskPriority? = nil
    ) {
        status = OSAllocatedUnfairLock(initialState: Status(stream: stream))
        self.priority = priority
    }
    
    deinit {
        cancel()
    }
    
    public func stream(
        bufferingPolicy: AsyncStream<Element>.Continuation.BufferingPolicy = .unbounded
    ) -> AsyncStream<Element> {
        return stream(
            bufferingPolicy: bufferingPolicy,
            onSubscribe: nil
        )
    }
    
    public func stream(
        bufferingPolicy: AsyncStream<Element>.Continuation.BufferingPolicy = .unbounded,
        onSubscribe: (@Sendable (AsyncStream<Element>.Continuation) -> Void)? = nil
    ) -> AsyncStream<Element> {
        return AsyncStream<Element>(bufferingPolicy: bufferingPolicy) { cont in
            let id = UUID()
            let isShutdown = status.withLock { status in
                if status.isFinished { return true }
                status.continuations[id] = cont
                return false
            }
            if isShutdown {
                cont.finish()
                return
            }
            cont.onTermination = { [weak self] _ in
                _ = self?.status.withLock { status in
                    status.continuations.removeValue(forKey: id)
                }
            }
            startTaskIfNeeded()
        }
    }
    
    private func startTaskIfNeeded() {
        status.withLock { status in
            if status.task != nil || status.isFinished || status.continuations.isEmpty { return }
            guard let stream = status.stream else { return }
            status.stream = nil
            status.task = Task(priority: priority) { [weak self] in
                await withTaskCancellationHandler {
                    for await value in stream {
                        guard let self = self else { break }
                        self.emit(value)
                    }
                    self?.finish()
                } onCancel: { [weak self] in
                    self?.finish()
                }
            }
        }
    }
    
    private func emit(_ value: Element) {
        status.withLock { status -> [AsyncStream<Element>.Continuation]? in
            if status.isFinished {
                return nil
            } else {
                return Array(status.continuations.values)
            }
        }?.forEach { continuation in
            continuation.yield(value)
        }
    }
    
    private func finish() {
        status.withLock { status -> [AsyncStream<Element>.Continuation]? in
            if status.isFinished { return nil }
            let continuations = Array(status.continuations.values)
            status.stream = nil
            status.task = nil
            status.isFinished = true
            status.continuations.removeAll()
            return continuations
        }?.forEach { continuation in
            continuation.finish()
        }
    }
    
    public func cancel() {
        let (task, continuations) = status.withLock { status in
            let task = status.task
            let continuations = Array(status.continuations.values)
            status.stream = nil
            status.task = nil
            status.isFinished = true
            status.continuations.removeAll()
            return (task, continuations)
        }
        task?.cancel()
        continuations.forEach { continuation in
            continuation.finish()
        }
    }
}

extension BroadcastConsumer {
    public convenience init(
        bufferingPolicy: AsyncStream<Element>.Continuation.BufferingPolicy = .unbounded,
        priority: TaskPriority? = nil,
        _ build: (AsyncStream<Element>.Continuation) -> Void
    ) {
        self.init(
            stream: AsyncStream(bufferingPolicy: bufferingPolicy, build),
            priority: priority
        )
    }
}

extension AsyncStream where Element: Sendable {
    public func broadcast(priority: TaskPriority? = nil) -> BroadcastConsumer<Element> {
        return BroadcastConsumer(stream: self, priority: priority)
    }
}
