import Foundation
import Combine
import common

public class VrcConnectionSettingsRepository: @unchecked Sendable {
    private let dataSource: VrcConnectionSettingsDataSource

    private let dataEmitter = StateEmitter(VrcConnectionSettings.default)
    public var dataConsumer: StateConsumer<VrcConnectionSettings> { dataEmitter.eraseToStateConsumer() }
    
    init(dataSource: VrcConnectionSettingsDataSource) {
        self.dataSource = dataSource
    }
    
    public func fetch() async {
        dataEmitter.value = VrcConnectionSettings(
            sendingIpAddressOrDomain: dataSource.sendingIpAddressOrDomain.getValue(),
            sendingPort: dataSource.sendingPort.getValue()
        )
    }
    
    @discardableResult
    public func setSendingIpAddressOrDomain(_ value: String) -> Bool {
        return dataSource.sendingIpAddressOrDomain.setValue(value).onTrue {
            dataEmitter.update {
                $0.sendingIpAddressOrDomain = value
            }
        }
    }
    
    @discardableResult
    public func setSendingPort(_ value: Int) -> Bool {
        return dataSource.sendingPort.setValue(value).onTrue {
            dataEmitter.update {
                $0.sendingPort = $0.sendingPort.update(value)
            }
        }
    }
}
