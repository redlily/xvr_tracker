import Foundation
import Combine
import common

public final class AnalyzedDataPlotServerSettingsRepository: Sendable {
    private let dataSource: AnalyzedDataPlotServerSettingsDataSource

    private let dataEmitter = StateEmitter(AnalyzedDataPlotServerSettings.default)
    public var dataConsumer: StateConsumer<AnalyzedDataPlotServerSettings> { dataEmitter.eraseToStateConsumer() }
    
    init(dataSource: AnalyzedDataPlotServerSettingsDataSource) {
        self.dataSource = dataSource
    }
    
    public func fetch() async {
        dataEmitter.value = AnalyzedDataPlotServerSettings(
            isEnabled: dataSource.isEnabled.getValue(),
            port: dataSource.port.getValue(),
            ping: dataSource.ping.getValue(),
            timeout: dataSource.timeout.getValue()
        )
    }
    
    @discardableResult
    public func setIsEnabled(_ value: Bool) -> Bool {
        return dataSource.isEnabled.setValue(value).onTrue {
            dataEmitter.update {
                $0.isEnabled = value
            }
        }
    }
    
    @discardableResult
    public func setPort(_ value: Int) -> Bool {
        return dataSource.port.setValue(value).onTrue {
            dataEmitter.update {
                $0.port = $0.port.update(value)
            }
        }
    }
    
    @discardableResult
    public func setPing(_ value: Int) -> Bool {
        return dataSource.ping.setValue(value).onTrue {
            dataEmitter.update {
                $0.ping = $0.ping.update(value)
            }
        }
    }
    
    @discardableResult
    public func setTimeout(_ value: Int) -> Bool {
        return dataSource.timeout.setValue(value).onTrue {
            dataEmitter.update {
                $0.timeout = $0.timeout.update(value)
            }
        }
    }
}
