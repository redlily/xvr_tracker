import Foundation
import Network
import di
import data
import common

public final class GetPrivateNetworkRoutesUseCase: Sendable {
    private let networkStateRepository: NetworkStateRepository
    
    init(
        networkStateRepository: NetworkStateRepository
    ) {
        self.networkStateRepository = networkStateRepository
    }
    
    public func callAsFunction() -> [LocalNetworkRoute] {
        guard let interfaces = (networkStateRepository.currentInterfaces?.filter {
            $0.type == .wifi || $0.type == .wiredEthernet
        }), !interfaces.isEmpty else {
            return []
        }
        
        var ifaddr: UnsafeMutablePointer<ifaddrs>?
        guard getifaddrs(&ifaddr) == 0, let firstAddr = ifaddr else { return [] }
        defer { freeifaddrs(ifaddr) }
        return sequence(first: firstAddr, next: { $0.pointee.ifa_next })
            .compactMap { ifptr -> LocalNetworkRoute? in
                let interface = ifptr.pointee
                
                let if_name = String(cString: interface.ifa_name)
                guard let nw_interface = interfaces.first(where: { $0.name == if_name }) else { return nil }
                
                if interface.ifa_addr.pointee.sa_family == UInt8(AF_INET) {
                    guard let ifa_addr = interface.ifa_addr,
                          let address = IPv4Address(ifa_addr.withMemoryRebound(to: sockaddr_in.self, capacity: 1) { sin in
                              withUnsafePointer(to: sin.pointee.sin_addr) { address in
                                  address.withMemoryRebound(to: UInt8.self, capacity: 4) { bytes in
                                      Data(bytes: bytes, count: 4)
                                  }
                              }
                          }), isPrivateIpAddress(address) else { return nil }
                    let prefix = interface.ifa_netmask.withMemoryRebound(to: sockaddr_in.self, capacity: 1) { sin in
                        withUnsafePointer(to: sin.pointee.sin_addr) { address in
                            address.withMemoryRebound(to: UInt8.self, capacity: 4) { bytes in
                                (0..<4).map {bytes[ $0 ]}.reduce(0) { $0 + $1.nonzeroBitCount }
                            }
                        }
                    }
                    return LocalNetworkRoute(
                        interface: nw_interface,
                        address: address,
                        prefix: prefix
                    )
                } else if interface.ifa_addr.pointee.sa_family == UInt8(AF_INET6) {
                    guard let address = IPv6Address(interface.ifa_addr.withMemoryRebound(to: sockaddr_in6.self, capacity: 1) { sin in
                        withUnsafePointer(to: sin.pointee.sin6_addr) { address in
                            address.withMemoryRebound(to: UInt8.self, capacity: 16) { bytes in
                                Data(bytes: bytes, count: 16)
                            }
                        }
                    }), isPrivateIpAddress(address) else { return nil }
                    let prefix = interface.ifa_netmask.withMemoryRebound(to: sockaddr_in6.self, capacity: 1) { sin in
                        withUnsafePointer(to: sin.pointee.sin6_addr) { address in
                            address.withMemoryRebound(to: UInt8.self, capacity: 16) { bytes in
                                (0..<16).map {bytes[ $0 ]}.reduce(0) { $0 + $1.nonzeroBitCount }
                            }
                        }
                    }
                    return LocalNetworkRoute(
                        interface: nw_interface,
                        address: address,
                        prefix: prefix
                    )
                }
                return nil
            }.sorted(by: {
                if $0.address.rawValue.count != $1.address.rawValue.count {
                    return $0.address.rawValue.count < $1.address.rawValue.count
                } else {
                    return $0.prefix > $1.prefix
                }
            })
    }
}
