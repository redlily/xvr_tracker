import SwiftUI

extension Text {
    init(localized key: String.LocalizationValue) {
        self.init(String(localized: key, bundle: Bundle.main))
    }
    
    init(localized key: String.LocalizationValue, comment: StaticString) {
        self.init(String(localized: key, bundle: Bundle.main, comment: comment))
    }
}
