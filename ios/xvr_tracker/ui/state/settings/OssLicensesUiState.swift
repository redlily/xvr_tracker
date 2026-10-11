import Foundation

struct OssLicense: Identifiable, Hashable {
    let title: String
    let licenseType: String
    let licenseText: String
    
    var id: String { title }
}

struct OssLicensesUiState {
    let licenses: [OssLicense]
    
    init(licenses: [OssLicense] = []) {
        self.licenses = licenses
    }
}
