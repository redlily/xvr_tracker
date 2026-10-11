import Foundation
import common

public struct BodyMeasurementsSettings: Sendable, Equatable {
    public var bodyHeight: RangedValue<Double>
    public var upperBodyHeightRatio: RangedValue<Double>
    public var headHeightRatio: RangedValue<Double>
    public var headZOffsetRatio: RangedValue<Double>
    public var neckLengthRatio: RangedValue<Double>
    public var shoulderWidthRatio: RangedValue<Double>
    public var armLengthRatio: RangedValue<Double>
    public var chestPositionRatio: RangedValue<Double>
    public var waistPositionRatio: RangedValue<Double>
    public var hipWidthRatio: RangedValue<Double>
    public var ankleHeightRatio: RangedValue<Double>
    public var footLengthRatio: RangedValue<Double>
    
    public init(
        bodyHeight: RangedValue<Double>,
        upperBodyHeightRatio: RangedValue<Double>,
        headHeightRatio: RangedValue<Double>,
        headZOffsetRatio: RangedValue<Double>,
        neckLengthRatio: RangedValue<Double>,
        shoulderWidthRatio: RangedValue<Double>,
        armLengthRatio: RangedValue<Double>,
        chestPositionRatio: RangedValue<Double>,
        waistPositionRatio: RangedValue<Double>,
        hipWidthRatio: RangedValue<Double>,
        ankleHeightRatio: RangedValue<Double>,
        footLengthRatio: RangedValue<Double>
    ) {
        self.bodyHeight = bodyHeight
        self.upperBodyHeightRatio = upperBodyHeightRatio
        self.headHeightRatio = headHeightRatio
        self.headZOffsetRatio = headZOffsetRatio
        self.neckLengthRatio = neckLengthRatio
        self.shoulderWidthRatio = shoulderWidthRatio
        self.armLengthRatio = armLengthRatio
        self.chestPositionRatio = chestPositionRatio
        self.waistPositionRatio = waistPositionRatio
        self.hipWidthRatio = hipWidthRatio
        self.ankleHeightRatio = ankleHeightRatio
        self.footLengthRatio = footLengthRatio
    }
    
    public static let bodyHeightRange: ClosedRange<Double> = 1.0...2.5
    public static let partsRatioRange: ClosedRange<Double> = 0.5...1.5
    public static let partsOffsetRange: ClosedRange<Double> = -1.0...1.0
    
    public static let `default` = BodyMeasurementsSettings(
        bodyHeight: RangedValue(1.75, in: bodyHeightRange),
        upperBodyHeightRatio: RangedValue(1.0, in: partsRatioRange),
        headHeightRatio: RangedValue(1.0, in: partsRatioRange),
        headZOffsetRatio: RangedValue(0.0, in: partsOffsetRange),
        neckLengthRatio: RangedValue(1.0, in: partsRatioRange),
        shoulderWidthRatio: RangedValue(1.0, in: partsRatioRange),
        armLengthRatio: RangedValue(1.0, in: partsRatioRange),
        chestPositionRatio: RangedValue(1.0, in: partsRatioRange),
        waistPositionRatio: RangedValue(1.0, in: partsRatioRange),
        hipWidthRatio: RangedValue(1.0, in: partsRatioRange),
        ankleHeightRatio: RangedValue(1.0, in: partsRatioRange),
        footLengthRatio: RangedValue(1.0, in: partsRatioRange)
    )
}
