import Foundation

@inlinable
public func checkCancellation() throws {
    if Task.isCancelled {
        throw CancellationError()
    }
}

@inlinable
public func nonCancellable<T: Sendable>(
    priority: TaskPriority? = nil,
    _ operation: @escaping @Sendable() async -> T
) async -> T {
    return await Task.detached(priority: priority) {
        return await operation()
    }.value
}

@inlinable
public func nonCancellable<T: Sendable>(
    priority: TaskPriority? = nil,
    _ operation: @escaping @Sendable () async throws -> T
) async throws -> T {
    return try await Task.detached(priority: priority) {
        return try await operation()
    }.value
}
