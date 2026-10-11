import Foundation
import os.lock

public final class AsyncMutex: Sendable {
    private struct Entry {
        let id: UUID
        let cont: CheckedContinuation<Void, any Error>
    }
    
    private struct State {
        var isLocked = false
        var waitingTasks: [Entry] = []
    }
    
    private let state = OSAllocatedUnfairLock(initialState: State())
    
    public var isLocked: Bool { state.withLock { state in state.isLocked } }
    
    public init() {}
    
    private func tryLock() -> Bool {
        return state.withLock { state in
            if !Task.isCancelled && !state.isLocked {
                state.isLocked = true
                return true
            }
            return false
        }
    }
    
    private func lock() async throws {
        try checkCancellation()
        if tryLock() { return }
        let id = UUID()
        try await withTaskCancellationHandler {
            try await withCheckedThrowingContinuation { cont in
                let hasLock = state.withLock { state in
                    if !state.isLocked {
                        state.isLocked = true
                        return true
                    } else {
                        state.waitingTasks.append(Entry(id: id, cont: cont))
                        return false
                    }
                }
                if hasLock {
                    cont.resume()
                }
            }
        } onCancel: {
            let entry: Entry? = state.withLock { state in
                if let index = state.waitingTasks.firstIndex(where: { $0.id == id }) {
                    return state.waitingTasks.remove(at: index)
                } else {
                    return nil
                }
            }
            entry?.cont.resume(throwing: CancellationError())
        }
    }
    
    private func unlock() {
        let next: Entry? = state.withLock { state in
            if state.waitingTasks.isEmpty {
                state.isLocked = false
                return nil
            } else {
                return state.waitingTasks.removeFirst()
            }
        }
        next?.cont.resume()
    }
}

extension AsyncMutex {
    @discardableResult
    public func withLock<T>(_ operation: () async throws -> T) async throws -> T {
        try await lock()
        defer { unlock() }
        return try await operation()
    }
    
    public enum TryLockResult<T> {
        case success(result: T)
        case failure
    }
    
    @discardableResult
    public func withTryLock<T>(_ operation: () throws -> T) rethrows -> TryLockResult<T> {
        if tryLock() {
            defer { unlock() }
            return .success(result: try operation())
        } else {
            return .failure
        }
    }
    
    @discardableResult
    public func withTryLock<T>(_ operation: () async throws -> T) async rethrows -> TryLockResult<T> {
        if tryLock() {
            defer { unlock() }
            return .success(result: try await operation())
        } else {
            return .failure
        }
    }
}
