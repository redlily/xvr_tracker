import Foundation
import Network
import os.atomic
import common

public final class NetworkStateRepository: Sendable {
    private let dataSource: NetworkInterfaceDataSource
    
    public var currentInterfaces: [NWInterface]? { dataSource.currentInterfaces }
    public var interfacesConsumer: AnyConsumer<[NWInterface]> { dataSource.interfacesConsumer }
    
    private let observer = OSAllocatedUnfairLock<Task<Void, Never>?>(initialState: nil)
    
    init(dataSource: NetworkInterfaceDataSource) {
        self.dataSource = dataSource
    }
    
    func start() {
        let consumer = interfacesConsumer.stream(bufferingPolicy: .bufferingNewest(0))
        observer.withLock {
            $0?.cancel()
            $0 = Task.detached(priority: .utility) {
                for await _ in consumer {}
            }
        }
    }
    
    func stop() {
        observer.withLock {
            $0?.cancel()
            $0 = nil
        }
    }
}
