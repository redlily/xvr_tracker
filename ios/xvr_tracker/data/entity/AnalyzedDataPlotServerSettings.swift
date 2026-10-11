import Foundation
import common

public struct AnalyzedDataPlotServerSettings: Sendable, Equatable {
    public var isEnabled: Bool
    public var port: RangedValue<Int>
    public var ping: RangedValue<Int>
    public var timeout: RangedValue<Int>
    
    public static let pingRange: ClosedRange<Int> = 0...1000
    public static let timeoutRange: ClosedRange<Int> = 0...10000
    
    public static let `default` = AnalyzedDataPlotServerSettings(
        isEnabled: false,
        port: 8000.toPort(),
        ping: RangedValue(100, in: pingRange),
        timeout: RangedValue(1000, in: timeoutRange)
    )
}
