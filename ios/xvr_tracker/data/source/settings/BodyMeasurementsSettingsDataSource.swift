import Foundation
import common

final class BodyMeasurementsSettingsDataSource: Sendable {
    static let prefix = "body_measurements"
    
    let bodyHeight: UserDefaultsProperty<RangedValue<Double>, Double, Double>
    let upperBodyHeightRatio: UserDefaultsProperty<RangedValue<Double>, Double, Double>
    let headHeightRatio: UserDefaultsProperty<RangedValue<Double>, Double, Double>
    let headZOffsetRatio: UserDefaultsProperty<RangedValue<Double>, Double, Double>
    let neckLengthRatio: UserDefaultsProperty<RangedValue<Double>, Double, Double>
    let shoulderWidthRatio: UserDefaultsProperty<RangedValue<Double>, Double, Double>
    let armLengthRatio: UserDefaultsProperty<RangedValue<Double>, Double, Double>
    let chestPositionRatio: UserDefaultsProperty<RangedValue<Double>, Double, Double>
    let waistPositionRatio: UserDefaultsProperty<RangedValue<Double>, Double, Double>
    let hipWidthRatio: UserDefaultsProperty<RangedValue<Double>, Double, Double>
    let ankleHeightRatio: UserDefaultsProperty<RangedValue<Double>, Double, Double>
    let footLengthRatio: UserDefaultsProperty<RangedValue<Double>, Double, Double>
    
    init(userDefaults: UserDefaults) {
        bodyHeight = userDefaults.property(
            prefix: Self.prefix,
            key: "body_height",
            defaultValue: BodyMeasurementsSettings.default.bodyHeight,
            checkValid: { BodyMeasurementsSettings.default.bodyHeight.range.contains($0) },
            transformOutput: { RangedValue($0, in: BodyMeasurementsSettings.default.bodyHeight.range) }
        )
        
        upperBodyHeightRatio = userDefaults.property(
            prefix: Self.prefix,
            key: "upper_body_height_ratio",
            defaultValue: BodyMeasurementsSettings.default.upperBodyHeightRatio,
            checkValid: { BodyMeasurementsSettings.default.upperBodyHeightRatio.range.contains($0) },
            transformOutput: { RangedValue($0, in: BodyMeasurementsSettings.default.upperBodyHeightRatio.range) }
        )
        
        headHeightRatio = userDefaults.property(
            prefix: Self.prefix,
            key: "head_height_ratio",
            defaultValue: BodyMeasurementsSettings.default.headHeightRatio,
            checkValid: { BodyMeasurementsSettings.default.headHeightRatio.range.contains($0) },
            transformOutput: { RangedValue($0, in: BodyMeasurementsSettings.default.headHeightRatio.range) }
        )
        
        headZOffsetRatio = userDefaults.property(
            prefix: Self.prefix,
            key: "head_z_offset_ratio",
            defaultValue: BodyMeasurementsSettings.default.headZOffsetRatio,
            checkValid: { BodyMeasurementsSettings.default.headZOffsetRatio.range.contains($0) },
            transformOutput: { RangedValue($0, in: BodyMeasurementsSettings.default.headZOffsetRatio.range) }
        )
        
        neckLengthRatio = userDefaults.property(
            prefix: Self.prefix,
            key: "neck_length_ratio",
            defaultValue: BodyMeasurementsSettings.default.neckLengthRatio,
            checkValid: { BodyMeasurementsSettings.default.neckLengthRatio.range.contains($0) },
            transformOutput: { RangedValue($0, in: BodyMeasurementsSettings.default.neckLengthRatio.range) }
        )
        
        shoulderWidthRatio = userDefaults.property(
            prefix: Self.prefix,
            key: "shoulder_width_ratio",
            defaultValue: BodyMeasurementsSettings.default.shoulderWidthRatio,
            checkValid: { BodyMeasurementsSettings.default.shoulderWidthRatio.range.contains($0) },
            transformOutput: { RangedValue($0, in: BodyMeasurementsSettings.default.shoulderWidthRatio.range) }
        )
        
        armLengthRatio = userDefaults.property(
            prefix: Self.prefix,
            key: "arm_length_ratio",
            defaultValue: BodyMeasurementsSettings.default.armLengthRatio,
            checkValid: { BodyMeasurementsSettings.default.armLengthRatio.range.contains($0) },
            transformOutput: { RangedValue($0, in: BodyMeasurementsSettings.default.armLengthRatio.range) }
        )
        
        chestPositionRatio = userDefaults.property(
            prefix: Self.prefix,
            key: "chest_position_ratio",
            defaultValue: BodyMeasurementsSettings.default.chestPositionRatio,
            checkValid: { BodyMeasurementsSettings.default.chestPositionRatio.range.contains($0) },
            transformOutput: { RangedValue($0, in: BodyMeasurementsSettings.default.chestPositionRatio.range) }
        )
        
        waistPositionRatio = userDefaults.property(
            prefix: Self.prefix,
            key: "waist_position_ratio",
            defaultValue: BodyMeasurementsSettings.default.waistPositionRatio,
            checkValid: { BodyMeasurementsSettings.default.waistPositionRatio.range.contains($0) },
            transformOutput: { RangedValue($0, in: BodyMeasurementsSettings.default.waistPositionRatio.range) }
        )
        
        hipWidthRatio = userDefaults.property(
            prefix: Self.prefix,
            key: "hip_width_ratio",
            defaultValue: BodyMeasurementsSettings.default.hipWidthRatio,
            checkValid: { BodyMeasurementsSettings.default.hipWidthRatio.range.contains($0) },
            transformOutput: { RangedValue($0, in: BodyMeasurementsSettings.default.hipWidthRatio.range) }
        )
        
        ankleHeightRatio = userDefaults.property(
            prefix: Self.prefix,
            key: "ankle_height_ratio",
            defaultValue: BodyMeasurementsSettings.default.ankleHeightRatio,
            checkValid: { BodyMeasurementsSettings.default.ankleHeightRatio.range.contains($0) },
            transformOutput: { RangedValue($0, in: BodyMeasurementsSettings.default.ankleHeightRatio.range) }
        )
        
        footLengthRatio = userDefaults.property(
            prefix: Self.prefix,
            key: "foot_length_ratio",
            defaultValue: BodyMeasurementsSettings.default.footLengthRatio,
            checkValid: { BodyMeasurementsSettings.default.footLengthRatio.range.contains($0) },
            transformOutput: { RangedValue($0, in: BodyMeasurementsSettings.default.footLengthRatio.range) }
        )
    }
}
