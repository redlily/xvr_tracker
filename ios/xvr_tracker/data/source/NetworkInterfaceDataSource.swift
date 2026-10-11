import Foundation
import Network
import os.atomic
import common

public final class NetworkInterfaceDataSource: Sendable {
    private let _currentInterfaces = OSAllocatedUnfairLock<[NWInterface]>(initialState: [])
    public var currentInterfaces: [NWInterface] { _currentInterfaces.withLock { $0 } }
    
    private let _interfacesConsumer = Lazy<AnyConsumer<[NWInterface]>>()
    public var interfacesConsumer: AnyConsumer<[NWInterface]> {
        _interfacesConsumer.get {
            CallbackConsumer<[NWInterface]> { cont in
                let monitor = NWPathMonitor()
                monitor.pathUpdateHandler = { path in
                    self._currentInterfaces.withLock { $0 = path.availableInterfaces }
                    cont.yield(path.availableInterfaces)
                }
                monitor.start(queue: .global(qos: .background))
                cont.onTermination = { _ in
                    monitor.cancel()
                }
            }.eraseToAnyConsumer()
        }
    }
}
