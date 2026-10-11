import Foundation
import common

public struct PoseModificationSettings: Sendable, Equatable {
    public var standStabilizingCoefficient: RangedValue<Double>
    public var sitStabilizingCoefficient: RangedValue<Double>
    public var lieDownStabilizingCoefficient: RangedValue<Double>
    public var stabilizingModeChangeThresholdTime: RangedValue<Int>
    public var isStabilizingModeChangeSoundEnabled: Bool
    public var bodyPositiveSlope: RangedValue<Double>
    public var bodyNegativeSlope: RangedValue<Double>
    public var footPositiveSlope: RangedValue<Double>
    public var footNegativeSlope: RangedValue<Double>
    public var kneePositiveSlope: RangedValue<Double>
    public var kneeNegativeSlope: RangedValue<Double>
    
    public static let stabilizingCoefficientRange: ClosedRange<Double> = 0.0...1.0
    public static let stabilizingModeChangeThresholdTimeRange: ClosedRange<Int> = 0...5000
    public static let bodySlopeRange: ClosedRange<Double> = -30.0...30.0
    public static let footSlopeRange: ClosedRange<Double> = -60.0...60.0
    public static let kneeSlopeRange: ClosedRange<Double> = -30.0...30.0
    
    public static let `default` = PoseModificationSettings(
        standStabilizingCoefficient: RangedValue(0.05, in: stabilizingCoefficientRange),
        sitStabilizingCoefficient: RangedValue(0.15, in: stabilizingCoefficientRange),
        lieDownStabilizingCoefficient: RangedValue(0.5, in: stabilizingCoefficientRange),
        stabilizingModeChangeThresholdTime: RangedValue(500, in: stabilizingModeChangeThresholdTimeRange),
        isStabilizingModeChangeSoundEnabled: true,
        bodyPositiveSlope: RangedValue(12.0, in: bodySlopeRange),
        bodyNegativeSlope: RangedValue(0.0, in: bodySlopeRange),
        footPositiveSlope: RangedValue(0.0, in: footSlopeRange),
        footNegativeSlope: RangedValue(30.0, in: footSlopeRange),
        kneePositiveSlope: RangedValue(17.0, in: kneeSlopeRange),
        kneeNegativeSlope: RangedValue(0.0, in: kneeSlopeRange)
    )
}
