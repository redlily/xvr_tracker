import Foundation
import common

class OnboardingSettingDataSource {
    static let prefix = "onboarding"

    let userDefaults: UserDefaults
    
    let done: UserDefaultsBoolProperty
    
    init(userDefaults: UserDefaults) {
        self.userDefaults = userDefaults
        
        done = userDefaults.property(
            prefix: Self.prefix,
            key: "done",
            defaultValue: OnboardingSettings.default.done
        )
    }
}
