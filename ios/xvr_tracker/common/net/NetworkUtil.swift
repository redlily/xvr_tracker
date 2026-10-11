import Foundation
import Network

public func isValidIpAddress(_ s: String) -> Bool {
    return IPv6Address(s) != nil || IPv4Address(s) != nil
}

nonisolated(unsafe) private let domainRegex: Regex = #/^[a-zA-Z0-9-]+(\.[a-zA-Z0-9-]+)+$/#

public func isValidDomain(_ s: String) -> Bool {
    s.wholeMatch(of: domainRegex) != nil
}

public func isValidDomainOrIpAddress(_ s: String) -> Bool {
    isValidIpAddress(s) || isValidDomain(s)
}

public let portRange = 0...65535

public func isValidIpPort(_ value: Int) -> Bool {
    return portRange.contains(value)
}

public extension Int {
    func toPort() -> RangedValue<Int> {
        return RangedValue(self, in: portRange)
    }
}

public func isPrivateIpAddress(_ address: IPAddress) -> Bool {
    if let ipv4 = address as? IPv4Address {
        let octets = ipv4.rawValue
        return (octets[0] == 192 && octets[1] == 168) ||
        (octets[0] == 172 && (16...31).contains(Int(octets[1]))) ||
        (octets[0] == 10)
    } else if let ipv6 = address as? IPv6Address {
        let octets = ipv6.rawValue
        return ((octets[0] == 0xfe) && ((octets[1] & 0xc0) == 0x80)) ||
        ((octets[0] & 0xfe) == 0xfc)
    }
    return false
}

public func isSameNetwork(_ a: IPAddress, _ b: IPAddress, prefix: Int) -> Bool {
    let aa = a.rawValue
    let bb = b.rawValue
    if aa.count != bb.count { return false }
    var remain = prefix
    for i in 0..<aa.count {
        if remain >= 8 {
            if aa[i] != bb[i] { return false }
            remain -= 8
        } else if (remain > 0) {
            let mask = UInt8(0xff << (8 - remain))
            if (aa[i] & mask) != (bb[i] & mask) { return false }
            remain = 0
        } else {
            break
        }
    }
    return true
}

public func resolveIpAddress(_ host: String) -> (any IPAddress)? {
    if let direct: IPAddress = IPv4Address(host) ?? IPv6Address(host) {
        return direct
    }
    var hints = addrinfo(
        ai_flags: AI_ADDRCONFIG,
        ai_family: AF_UNSPEC,
        ai_socktype: SOCK_STREAM,
        ai_protocol: IPPROTO_TCP,
        ai_addrlen: 0,
        ai_canonname: nil,
        ai_addr: nil,
        ai_next: nil
    )
    var infoPtr: UnsafeMutablePointer<addrinfo>?
    let result = getaddrinfo(host, nil, &hints, &infoPtr)
    guard result == 0, let info = infoPtr else {
        return nil
    }
    defer { freeaddrinfo(infoPtr) }
    
    let family = info.pointee.ai_addr.pointee.sa_family
    if family == sa_family_t(AF_INET) {
        return info.pointee.ai_addr.withMemoryRebound(to: sockaddr_in.self, capacity: 1) { ptr in
            var addr = ptr.pointee.sin_addr.s_addr
            let data = Data(bytes: &addr, count: MemoryLayout<in_addr_t>.size)
            return IPv4Address(data)
        }
    } else if family == sa_family_t(AF_INET6) {
        return info.pointee.ai_addr.withMemoryRebound(to: sockaddr_in6.self, capacity: 1) { ptr in
            var addr = ptr.pointee.sin6_addr
            let data = Data(bytes: &addr, count: MemoryLayout<in6_addr>.size)
            return IPv6Address(data)
        }
    }
    
    return nil
}

public func makeIpPrefix(address: IPAddress, prefix: Int) -> String {
    return if let v4 = address as? IPv4Address {
        makeIpv4Prefix(address: v4, prefix: prefix)
    } else if let v6 = address as? IPv6Address {
        makeIpv6Prefix(address: v6, prefix: prefix)
    } else {
        ""
    }
}

func makeIpv4Prefix(address: IPv4Address, prefix: Int) -> String {
    if prefix <= 0 || prefix > 32 { return "" }
    let data = address.rawValue
    var result = ""
    for i in 0..<(prefix / 8) {
        result += "\(Int(data[i]) & 0xff)."
    }
    return result
}

func makeIpv6Prefix(address: IPv6Address, prefix: Int) -> String {
    if prefix <= 0 || prefix > 128 { return "" }
    let data = address.rawValue
    var result = ""
    for i in 0..<(prefix / 16) {
        let high = Int(data[i * 2])   // UInt8なので &0xff は不要
        let low  = Int(data[i * 2 + 1])
        let block = (high << 8) | low
        result += String(format: "%x:", block)
    }
    return result
}

public extension NWEndpoint.Host {
    init(ipAddress: IPAddress) {
        if let v4 = ipAddress as? IPv4Address {
            self = .ipv4(v4)
        } else if let v6 = ipAddress as? IPv6Address {
            self = .ipv6(v6)
        } else {
            preconditionFailure("Unsupported IPAddress type: \(type(of: ipAddress))")
        }
    }
}
