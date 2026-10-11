import Foundation
import common

public class CameraSettingsDataSource {
    public static let prefix = "camera"
    
    public let lensFacingType: UserDefaultsProperty<CameraLensFacingType, CameraLensFacingType, Int>
    public let zoomRatio: UserDefaultsDoubleProperty
    
    public init(userDefaults: UserDefaults = .standard) {
        self.lensFacingType = userDefaults.property(
            prefix: Self.prefix,
            key: "lens_facing_type",
            defaultValue: CameraSettings.default.lensFacingType,
            checkValid: { type in CameraLensFacingType.allCases.contains(type) },
            transformOutput: { CameraLensFacingType.fromId($0) },
            transformInput: { $0.rawValue }
        )
        
        self.zoomRatio = userDefaults.property(
            prefix: Self.prefix,
            key: "zoom_ratio",
            defaultValue: CameraSettings.default.zoomRatio
        )
    }
}
