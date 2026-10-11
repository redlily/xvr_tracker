import Foundation
import os.lock

public final class TaskBag: Sendable {
    private let tasks = OSAllocatedUnfairLock<
        [UUID: (task: Taskable, monitorTask: Task<Void, Never>)]
    >(initialState: [:])
    
    public init() {}
    
    deinit {
        cancel()
    }
    
    @discardableResult
    public func add(_ task: any Taskable) -> UUID {
        let id = UUID()
        tasks.withLock {
            $0[id] = (
                task: task,
                monitorTask: Task.detached { [weak self] in
                    await task.wait()
                    let snapshot = self?.tasks.withLock {
                        $0.removeValue(forKey: id)?.monitorTask
                    }
                    snapshot?.cancel()
                }
            )
        }
        return id
    }
    
    @discardableResult
    public func remove(_ id: UUID) -> Bool {
        let snapshot = tasks.withLock {
            return $0.removeValue(forKey: id)
        }
        snapshot?.monitorTask.cancel()
        return snapshot != nil
    }
    
    public func cancel() {
        let snapshot = tasks.withLock {
            let tasks = Array($0.values)
            $0.removeAll()
            return tasks
        }
        snapshot.forEach {
            $0.task.cancel()
            $0.monitorTask.cancel()
        }
    }
}

public protocol Taskable: Sendable {
    func wait() async
    func cancel()
}

extension Task: Taskable {
    public func wait() async {
        _ = await result
    }
}

extension Taskable {
    @discardableResult
    public func store(in bag: TaskBag) -> Self {
        _ = bag.add(self)
        return self
    }
}
