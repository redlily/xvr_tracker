import Foundation
import common

public struct VrcConnectionSettings: Sendable, Equatable {
    public var sendingIpAddressOrDomain: String
    public var sendingPort: RangedValue<Int>
    
    public static let `default` = VrcConnectionSettings(
        sendingIpAddressOrDomain: "",
        sendingPort: 9000.toPort()
    )
}
