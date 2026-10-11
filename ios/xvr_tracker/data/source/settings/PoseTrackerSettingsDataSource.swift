import Foundation
import common

final class PoseTrackerSettingsDataSource: Sendable {
    static let prefix = "pose_tracker"
    
    let head: UserDefaultsProperty<PoseTrackerMotionType, PoseTrackerMotionType, Int>
    let chest: UserDefaultsProperty<PoseTrackerMotionType, PoseTrackerMotionType, Int>
    let waist: UserDefaultsProperty<PoseTrackerMotionType, PoseTrackerMotionType, Int>
    let elbow: UserDefaultsProperty<PoseTrackerMotionType, PoseTrackerMotionType, Int>
    let knee: UserDefaultsProperty<PoseTrackerMotionType, PoseTrackerMotionType, Int>
    let feet: UserDefaultsProperty<PoseTrackerMotionType, PoseTrackerMotionType, Int>
    
    init(userDefaults: UserDefaults) {
        head = userDefaults.property(
            prefix: Self.prefix,
            key: "head",
            defaultValue: PoseTrackerSettings.default.head,
            checkValid: { type in PoseTrackerMotionType.allCases.contains(type) },
            transformOutput: { PoseTrackerMotionType.fromId($0) },
            transformInput: { $0.id }
        )
        
        chest = userDefaults.property(
            prefix: Self.prefix,
            key: "chest",
            defaultValue: PoseTrackerSettings.default.chest,
            checkValid: { type in PoseTrackerMotionType.allCases.contains(type) },
            transformOutput: { PoseTrackerMotionType.fromId($0) },
            transformInput: { $0.id }
        )
        
        waist = userDefaults.property(
            prefix: Self.prefix,
            key: "waist",
            defaultValue: PoseTrackerSettings.default.waist,
            checkValid: { type in PoseTrackerMotionType.allCases.contains(type) },
            transformOutput: { PoseTrackerMotionType.fromId($0) },
            transformInput: { $0.id }
        )
        
        elbow = userDefaults.property(
            prefix: Self.prefix,
            key: "elbow",
            defaultValue: PoseTrackerSettings.default.elbow,
            checkValid: { type in PoseTrackerMotionType.allCases.contains(type) },
            transformOutput: { PoseTrackerMotionType.fromId($0) },
            transformInput: { $0.id }
        )
        
        knee = userDefaults.property(
            prefix: Self.prefix,
            key: "knee",
            defaultValue: PoseTrackerSettings.default.knee,
            checkValid: { type in PoseTrackerMotionType.allCases.contains(type) },
            transformOutput: { PoseTrackerMotionType.fromId($0) },
            transformInput: { $0.id }
        )
        
        feet = userDefaults.property(
            prefix: Self.prefix,
            key: "feet",
            defaultValue: PoseTrackerSettings.default.feet,
            checkValid: { type in PoseTrackerMotionType.allCases.contains(type) },
            transformOutput: { PoseTrackerMotionType.fromId($0) },
            transformInput: { $0.id }
        )
    }
}
