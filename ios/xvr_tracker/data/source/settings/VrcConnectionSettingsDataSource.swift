import Foundation
import common

class VrcConnectionSettingsDataSource {
    static let prefix = "vrc_connection"
    
    let userDefaults: UserDefaults
    
    let sendingIpAddressOrDomain: UserDefaultsStringProperty
    let sendingPort: UserDefaultsProperty<RangedValue<Int>, Int, Int>
    
    init(userDefaults: UserDefaults) {
        self.userDefaults = userDefaults
        
        sendingIpAddressOrDomain = userDefaults.property(
            prefix: Self.prefix,
            key: "sending_ip_address_or_domain",
            defaultValue: VrcConnectionSettings.default.sendingIpAddressOrDomain,
            checkValid: isValidDomainOrIpAddress
        )
        
        sendingPort = userDefaults.property(
            prefix: Self.prefix,
            key: "sending_port",
            defaultValue: VrcConnectionSettings.default.sendingPort,
            checkValid: { VrcConnectionSettings.default.sendingPort.range.contains($0) },
            transformOutput: { $0.toPort() }
        )
    }
}
