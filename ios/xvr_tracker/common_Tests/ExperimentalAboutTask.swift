import Testing

struct ExperimentalAboutTask {
    
    @Test func taskCancel() async throws {
        let task: Task<String, Error> = Task {
            print("task started")
            do {
                try await Task.sleep(nanoseconds: 1_000_000_000 * 20)
                print("task completed")
                return "hoge"
            } catch {
                print("task cancelled")
                throw error
            }
        }
        _ = await withTaskCancellationHandler(operation: {
            do {
                return try await task.value
            } catch {
                return ""
            }
        }, onCancel: { [task] in
            task.cancel()
            print("be task cancelled outside")
        })
    }
}
