import Foundation
import infra
import common

final class PoseLandmarkerSettingsDataSource: Sendable {
    static let prefix = "pose_landmarker"
    
    let modelType: UserDefaultsProperty<PoseLandmarkerModelType, PoseLandmarkerModelType, Int>
    let computeType: UserDefaultsProperty<PoseLandmarkerComputeType, PoseLandmarkerComputeType, Int>
    let visibilityThreshold: UserDefaultsProperty<RangedValue<Double>, Double, Double>
    let presenceThreshold: UserDefaultsProperty<RangedValue<Double>, Double, Double>
    
    init(userDefaults: UserDefaults) {
        modelType = userDefaults.property(
            prefix: Self.prefix,
            key: "model_type",
            defaultValue: PoseLandmarkerSettings.default.modelType,
            checkValid: { type in PoseLandmarkerModelType.allCases.contains(type) },
            transformOutput: { PoseLandmarkerModelType.fromId($0) },
            transformInput: { $0.rawValue }
        )
        
        computeType = userDefaults.property(
            prefix: Self.prefix,
            key: "compute_type",
            defaultValue: PoseLandmarkerSettings.default.computeType,
            checkValid: { type in PoseLandmarkerComputeType.allCases.contains(type) },
            transformOutput: { PoseLandmarkerComputeType.fromId($0) },
            transformInput: { $0.rawValue }
        )
        
        visibilityThreshold = userDefaults.property(
            prefix: Self.prefix,
            key: "visibility_threshold",
            defaultValue: PoseLandmarkerSettings.default.visibilityThreshold,
            checkValid: { PoseLandmarkerSettings.default.visibilityThreshold.range.contains($0) },
            transformOutput: { RangedValue($0, in: PoseLandmarkerSettings.default.visibilityThreshold.range) }
        )
        
        presenceThreshold = userDefaults.property(
            prefix: Self.prefix,
            key: "presence_threshold",
            defaultValue: PoseLandmarkerSettings.default.presenceThreshold,
            checkValid: { PoseLandmarkerSettings.default.presenceThreshold.range.contains($0) },
            transformOutput: { RangedValue($0, in: PoseLandmarkerSettings.default.presenceThreshold.range) }
        )
    }
}
