import Foundation

extension String {
    init(localized key: String.LocalizationValue) {
        self.init(String(localized: key, bundle: Bundle.main))
    }
    
    init(localized key: String.LocalizationValue, comment: StaticString) {
        self.init(String(localized: key, bundle: Bundle.main, comment: comment))
    }
    
    init(localized key: String.LocalizationValue, _ arguments: CVarArg...) {
        let format = String(localized: key, bundle: Bundle.main)
        self.init(format: format, arguments: arguments)
    }
}
