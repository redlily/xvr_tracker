import Foundation
import os.lock

public final class CallbackConsumer<Element: Sendable>: Consumer, Sendable {
    private struct Status {
        var taskId: UUID? = nil
        var task: Task<Void, Never>?  = nil
        var continuations: [UUID: AsyncStream<Element>.Continuation] = [:]
    }
    
    private let status = OSAllocatedUnfairLock(initialState: Status())
    
    private let priority: TaskPriority?
    private let builder: @Sendable () async -> AsyncStream<Element>
    private let onSubscribe: @Sendable (AsyncStream<Element>.Continuation) -> Void
    
    public init(
        priority: TaskPriority? = nil,
        _ builder: @escaping @Sendable () async -> AsyncStream<Element>,
        onSubscribe: @escaping @Sendable (AsyncStream<Element>.Continuation) -> Void
    ) {
        self.priority = priority
        self.builder = builder
        self.onSubscribe = onSubscribe
    }
    
    public convenience init(
        priority: TaskPriority? = nil,
        _ builder: @escaping @Sendable () async -> AsyncStream<Element>
    ) {
        self.init(
            priority: priority,
            builder,
            onSubscribe: { _ in }
        )
    }
    
    deinit {
        cancel()
    }
    
    public func stream(
        bufferingPolicy: AsyncStream<Element>.Continuation.BufferingPolicy = .unbounded
    ) -> AsyncStream<Element> {
        return AsyncStream<Element>(bufferingPolicy: bufferingPolicy) { cont in
            let id = UUID()
            let shouldStartTask = status.withLock { status in
                status.continuations[id] = cont
                return status.continuations.count == 1 && status.task == nil
            }
            onSubscribe(cont)
            cont.onTermination = { [weak self] _ in
                self?.status.withLock { status -> Task<Void, Never>? in
                    status.continuations.removeValue(forKey: id)
                    if !status.continuations.isEmpty { return nil }
                    let task = status.task
                    status.taskId = nil
                    status.task = nil
                    return task
                }?.cancel()
            }
            if shouldStartTask {
                startTaskIfNeeded()
            }
        }
    }
    
    private func startTaskIfNeeded() {
        status.withLock { status in
            if status.task != nil || status.continuations.isEmpty { return }
            let taskId = UUID()
            status.taskId = taskId
            status.task = Task(priority: priority) { [weak self] in
                
                if let stream = await self?.builder() {
                    for await value in stream {
                        guard let self = self else { break }
                        if !self.emit(value, taskId: taskId) { break }
                    }
                    if !Task.isCancelled {
                        self?.finish(taskId: taskId)
                    }
                }
            }
        }
    }
    
    private func emit(_ value: Element, taskId: UUID) -> Bool {
        let continuations = status.withLock { status -> [AsyncStream<Element>.Continuation]? in
            if status.taskId != taskId {
                return nil
            } else {
                return Array(status.continuations.values)
            }
        }
        continuations?.forEach { continuation in
            continuation.yield(value)
        }
        return continuations != nil
    }
    
    private func finish(taskId: UUID) {
        status.withLock { status -> [AsyncStream<Element>.Continuation]? in
            if status.taskId != taskId { return nil }
            let continuations = Array(status.continuations.values)
            status.taskId = nil
            status.task = nil
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
            status.taskId = nil
            status.task = nil
            status.continuations.removeAll()
            return (task, continuations)
        }
        task?.cancel()
        continuations.forEach { continuation in
            continuation.finish()
        }
    }
}

extension CallbackConsumer {
    public convenience init(
        bufferingPolicy: AsyncStream<Element>.Continuation.BufferingPolicy = .unbounded,
        priority: TaskPriority? = nil,
        _ builder: @escaping @Sendable (AsyncStream<Element>.Continuation) -> Void,
        onSubscribe: @escaping @Sendable (AsyncStream<Element>.Continuation) -> Void
    ) {
        self.init(
            priority: priority,
            {
                AsyncStream<Element>(
                    bufferingPolicy: bufferingPolicy,
                    builder
                )
            },
            onSubscribe: onSubscribe
        )
    }
    
    public convenience init(
        bufferingPolicy: AsyncStream<Element>.Continuation.BufferingPolicy = .unbounded,
        priority: TaskPriority? = nil,
        _ builder: @escaping @Sendable (AsyncStream<Element>.Continuation) -> Void
    ) {
        self.init(
            priority: priority,
            builder,
            onSubscribe: { _ in }
        )
    }
}
