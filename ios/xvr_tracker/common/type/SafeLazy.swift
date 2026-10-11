import Foundation
import os.lock

public class SafeLazy<Element: Sendable>: @unchecked Sendable {
    private let storage = OSAllocatedUnfairLock<Element?>(initialState: nil)
    public var value: Element {
        storage.withLock { current in
            if current == nil {
                current = builder()
            }
            return current!
        }
    }
    
    private let builder: @Sendable () -> Element
    
    public init(_ builder: @escaping @Sendable () -> Element) {
        self.builder = builder
    }
}
