import Foundation
import common

public final class AnalyzedDataPlotServerSettingsDataSource: Sendable {
    public static let prefix = "analyzed_data_plot_server"
    
    public let isEnabled: UserDefaultsBoolProperty
    public let port: UserDefaultsProperty<RangedValue<Int>, Int, Int>
    public let ping: UserDefaultsProperty<RangedValue<Int>, Int, Int>
    public let timeout: UserDefaultsProperty<RangedValue<Int>, Int, Int>
    
    public init(userDefaults: UserDefaults = .standard) {
        self.isEnabled = userDefaults.property(
            prefix: Self.prefix,
            key: "is_enabled",
            defaultValue: AnalyzedDataPlotServerSettings.default.isEnabled
        )
        
        self.port = userDefaults.property(
            prefix: Self.prefix,
            key: "port",
            defaultValue: AnalyzedDataPlotServerSettings.default.port,
            checkValid: { AnalyzedDataPlotServerSettings.default.port.range.contains($0) },
            transformOutput: { RangedValue($0, in: AnalyzedDataPlotServerSettings.default.port.range) }
        )
        
        self.ping = userDefaults.property(
            prefix: Self.prefix,
            key: "ping",
            defaultValue: AnalyzedDataPlotServerSettings.default.ping,
            checkValid: { AnalyzedDataPlotServerSettings.default.ping.range.contains($0) },
            transformOutput: { RangedValue($0, in: AnalyzedDataPlotServerSettings.default.ping.range) }
        )
        
        self.timeout = userDefaults.property(
            prefix: Self.prefix,
            key: "timeout",
            defaultValue: AnalyzedDataPlotServerSettings.default.timeout,
            checkValid: { AnalyzedDataPlotServerSettings.default.timeout.range.contains($0) },
            transformOutput: { RangedValue($0, in: AnalyzedDataPlotServerSettings.default.timeout.range) }
        )
    }
}
