import Foundation
import data

public struct BodyMeasurements: Sendable, Codable {
    public var bodyHeight: Double
    public var upperBodyHeight: Double
    public var headHeight: Double
    public var headZOffset: Double
    public var neckLength: Double
    public var headToShoulderLength: Double
    public var shoulderWidth: Double
    public var armLength: Double
    public var shoulderToChestLength: Double
    public var chestToWaistLength: Double
    public var waistToHipLength: Double
    public var hipWidth: Double
    public var legLength: Double
    public var ankleToHeelLength: Double
    public var footLength: Double
    
    public var hipToShoulderLength: Double {
        return waistToHipLength + chestToWaistLength + shoulderToChestLength
    }
    
    public init(
        bodyHeight: Double,
        upperBodyHeight: Double,
        headHeight: Double,
        headZOffset: Double,
        neckLength: Double,
        headToShoulderLength: Double,
        shoulderWidth: Double,
        armLength: Double,
        shoulderToChestLength: Double,
        chestToWaistLength: Double,
        waistToHipLength: Double,
        hipWidth: Double,
        legLength: Double,
        ankleToHeelLength: Double,
        footLength: Double
    ) {
        self.bodyHeight = bodyHeight
        self.upperBodyHeight = upperBodyHeight
        self.headHeight = headHeight
        self.headZOffset = headZOffset
        self.neckLength = neckLength
        self.headToShoulderLength = headToShoulderLength
        self.shoulderWidth = shoulderWidth
        self.armLength = armLength
        self.shoulderToChestLength = shoulderToChestLength
        self.chestToWaistLength = chestToWaistLength
        self.waistToHipLength = waistToHipLength
        self.hipWidth = hipWidth
        self.legLength = legLength
        self.ankleToHeelLength = ankleToHeelLength
        self.footLength = footLength
    }
    
    public static let `default` = make(bodyHeight: 1.75)
    
    public static func make(
        bodyHeight: Double,
        upperBodyHeightRatio: Double = 1.0,
        headHeightRatio: Double = 1.0,
        headZOffsetRatio: Double = 1.0,
        neckLengthRatio: Double = 1.0,
        shoulderWidthRatio: Double = 1.0,
        armLengthRatio: Double = 1.0,
        chestPositionRatio: Double = 1.0,
        waistPositionRatio: Double = 1.0,
        hipWidthRatio: Double = 1.0,
        anklePositionRatio: Double = 1.0,
        footLengthRatio: Double = 1.0
    ) -> BodyMeasurements {
        let upperBodyHeight = bodyHeight * 0.5 * upperBodyHeightRatio
        
        let headHeight = upperBodyHeight * (4.0 / 14.0) * headHeightRatio * headHeightRatio
        let headZOffset = headHeight * headZOffsetRatio
        let neckLength = upperBodyHeight * (1.0 / 14.0) * headHeightRatio * neckLengthRatio
        let headToShoulderLength = headHeight / 2.0 + neckLength
        
        let shoulderWidth = upperBodyHeight * 0.40 * shoulderWidthRatio
        let shoulderToHipLength = upperBodyHeight - headHeight - neckLength
        let armLength = shoulderToHipLength * (7.0 / 8.0) * armLengthRatio
        let shoulderToChestLength = shoulderToHipLength * (1.0 / 8.0) * chestPositionRatio
        let waistToHipLength = shoulderToHipLength * (1.0 / 8.0) * waistPositionRatio
        let chestToWaistLength = shoulderToHipLength - shoulderToChestLength - waistToHipLength
        
        let lowerBodyHeight = bodyHeight - upperBodyHeight
        let hipWidth = upperBodyHeight * 0.30 * hipWidthRatio
        let ankleToHeelLength = lowerBodyHeight * (1.0 / 8.0) * anklePositionRatio
        let legLength = lowerBodyHeight - ankleToHeelLength
        let footLength = ankleToHeelLength * 2.4 * footLengthRatio
        
        return BodyMeasurements(
            bodyHeight: bodyHeight,
            upperBodyHeight: upperBodyHeight,
            headHeight: headHeight,
            headZOffset: headZOffset,
            neckLength: neckLength,
            headToShoulderLength: headToShoulderLength,
            shoulderWidth: shoulderWidth,
            armLength: armLength,
            shoulderToChestLength: shoulderToChestLength,
            chestToWaistLength: chestToWaistLength,
            waistToHipLength: waistToHipLength,
            hipWidth: hipWidth,
            legLength: legLength,
            ankleToHeelLength: ankleToHeelLength,
            footLength: footLength
        )
    }
}

extension BodyMeasurementsSettings {
    public func bodyMeasurements(
        bodyHeight: Double? = nil,
        upperBodyHeightRatio: Double? = nil,
        headHeightRatio: Double? = nil,
        headZOffsetRatio: Double? = nil,
        neckLengthRatio: Double? = nil,
        shoulderWidthRatio: Double? = nil,
        armLengthRatio: Double? = nil,
        chestPositionRatio: Double? = nil,
        waistPositionRatio: Double? = nil,
        hipWidthRatio: Double? = nil,
        ankleHeightRatio: Double? = nil,
        footLengthRatio: Double? = nil
    ) -> BodyMeasurements {
        return BodyMeasurements.make(
            bodyHeight: bodyHeight ?? self.bodyHeight.value,
            upperBodyHeightRatio: upperBodyHeightRatio ?? self.upperBodyHeightRatio.value,
            headHeightRatio: headHeightRatio ?? self.headHeightRatio.value,
            headZOffsetRatio: headZOffsetRatio ?? self.headZOffsetRatio.value,
            neckLengthRatio: neckLengthRatio ?? self.neckLengthRatio.value,
            shoulderWidthRatio: shoulderWidthRatio ?? self.shoulderWidthRatio.value,
            armLengthRatio: armLengthRatio ?? self.armLengthRatio.value,
            chestPositionRatio: chestPositionRatio ?? self.chestPositionRatio.value,
            waistPositionRatio: waistPositionRatio ?? self.waistPositionRatio.value,
            hipWidthRatio: hipWidthRatio ?? self.hipWidthRatio.value,
            anklePositionRatio: ankleHeightRatio ?? self.ankleHeightRatio.value,
            footLengthRatio: footLengthRatio ?? self.footLengthRatio.value
        )
    }
}

