import Foundation
import common

public final class PoseModificationSettingsDataSource: Sendable {
    public static let prefix = "pose_modification"
    
    public let standStabilizingCoefficient: UserDefaultsProperty<RangedValue<Double>, Double, Double>
    public let sitStabilizingCoefficient: UserDefaultsProperty<RangedValue<Double>, Double, Double>
    public let lieDownStabilizingCoefficient: UserDefaultsProperty<RangedValue<Double>, Double, Double>
    public let stabilizingModeChangeThresholdTime: UserDefaultsProperty<RangedValue<Int>, Int, Int>
    public let isStabilizingModeChangeSoundEnabled: UserDefaultsBoolProperty
    public let bodyPositiveSlope: UserDefaultsProperty<RangedValue<Double>, Double, Double>
    public let bodyNegativeSlope: UserDefaultsProperty<RangedValue<Double>, Double, Double>
    public let footPositiveSlope: UserDefaultsProperty<RangedValue<Double>, Double, Double>
    public let footNegativeSlope: UserDefaultsProperty<RangedValue<Double>, Double, Double>
    public let kneePositiveSlope: UserDefaultsProperty<RangedValue<Double>, Double, Double>
    public let kneeNegativeSlope: UserDefaultsProperty<RangedValue<Double>, Double, Double>
    
    public init(userDefaults: UserDefaults = .standard) {
        self.standStabilizingCoefficient = userDefaults.property(
            prefix: Self.prefix,
            key: "stand_stabilizing_coefficient",
            defaultValue: PoseModificationSettings.default.standStabilizingCoefficient,
            checkValid: { PoseModificationSettings.default.standStabilizingCoefficient.range.contains($0) },
            transformOutput: { RangedValue($0, in: PoseModificationSettings.default.standStabilizingCoefficient.range) }
        )
        
        self.sitStabilizingCoefficient = userDefaults.property(
            prefix: Self.prefix,
            key: "sit_stabilizing_coefficient",
            defaultValue: PoseModificationSettings.default.sitStabilizingCoefficient,
            checkValid: { PoseModificationSettings.default.sitStabilizingCoefficient.range.contains($0) },
            transformOutput: { RangedValue($0, in: PoseModificationSettings.default.sitStabilizingCoefficient.range) }
        )
        
        self.lieDownStabilizingCoefficient = userDefaults.property(
            prefix: Self.prefix,
            key: "lie_down_stabilizing_coefficient",
            defaultValue: PoseModificationSettings.default.lieDownStabilizingCoefficient,
            checkValid: { PoseModificationSettings.default.lieDownStabilizingCoefficient.range.contains($0) },
            transformOutput: { RangedValue($0, in: PoseModificationSettings.default.lieDownStabilizingCoefficient.range) }
        )
        
        self.stabilizingModeChangeThresholdTime = userDefaults.property(
            prefix: Self.prefix,
            key: "stabilizing_mode_change_threshold_time",
            defaultValue: PoseModificationSettings.default.stabilizingModeChangeThresholdTime,
            checkValid: { PoseModificationSettings.default.stabilizingModeChangeThresholdTime.range.contains($0) },
            transformOutput: { RangedValue($0, in: PoseModificationSettings.default.stabilizingModeChangeThresholdTime.range) }
        )
        
        self.isStabilizingModeChangeSoundEnabled = userDefaults.property(
            prefix: Self.prefix,
            key: "is_stabilizing_mode_change_sound_enabled",
            defaultValue: PoseModificationSettings.default.isStabilizingModeChangeSoundEnabled
        )
        
        self.bodyPositiveSlope = userDefaults.property(
            prefix: Self.prefix,
            key: "body_positive_slope",
            defaultValue: PoseModificationSettings.default.bodyPositiveSlope,
            checkValid: { PoseModificationSettings.default.bodyPositiveSlope.range.contains($0) },
            transformOutput: { RangedValue($0, in: PoseModificationSettings.default.bodyPositiveSlope.range) }
        )
        
        self.bodyNegativeSlope = userDefaults.property(
            prefix: Self.prefix,
            key: "body_negative_slope",
            defaultValue: PoseModificationSettings.default.bodyNegativeSlope,
            checkValid: { PoseModificationSettings.default.bodyNegativeSlope.range.contains($0) },
            transformOutput: { RangedValue($0, in: PoseModificationSettings.default.bodyNegativeSlope.range) }
        )

        self.footPositiveSlope = userDefaults.property(
            prefix: Self.prefix,
            key: "foot_positive_slope",
            defaultValue: PoseModificationSettings.default.footPositiveSlope,
            checkValid: { PoseModificationSettings.default.footPositiveSlope.range.contains($0) },
            transformOutput: { RangedValue($0, in: PoseModificationSettings.default.footPositiveSlope.range) }
        )

        self.footNegativeSlope = userDefaults.property(
            prefix: Self.prefix,
            key: "foot_negative_slope",
            defaultValue: PoseModificationSettings.default.footNegativeSlope,
            checkValid: { PoseModificationSettings.default.footNegativeSlope.range.contains($0) },
            transformOutput: { RangedValue($0, in: PoseModificationSettings.default.footNegativeSlope.range) }
        )
        
        self.kneePositiveSlope = userDefaults.property(
            prefix: Self.prefix,
            key: "knee_positive_slope",
            defaultValue: PoseModificationSettings.default.kneePositiveSlope,
            checkValid: { PoseModificationSettings.default.kneePositiveSlope.range.contains($0) },
            transformOutput: { RangedValue($0, in: PoseModificationSettings.default.kneePositiveSlope.range) }
        )
        
        self.kneeNegativeSlope = userDefaults.property(
            prefix: Self.prefix,
            key: "knee_negative_slope",
            defaultValue: PoseModificationSettings.default.kneeNegativeSlope,
            checkValid: { PoseModificationSettings.default.kneeNegativeSlope.range.contains($0) },
            transformOutput: { RangedValue($0, in: PoseModificationSettings.default.kneeNegativeSlope.range) }
        )
    }
}
