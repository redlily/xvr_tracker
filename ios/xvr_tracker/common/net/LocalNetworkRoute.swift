import Foundation
import Network

public struct LocalNetworkRoute: Sendable {
    public let interface: NWInterface
    public let address: any IPAddress
    public let prefix: Int
    
    public init(
        interface: NWInterface,
        address: any IPAddress,
        prefix: Int
    ) {
        self.interface = interface
        self.address = address
        self.prefix = prefix
    }
}
