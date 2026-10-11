import SwiftUI
import Combine
import Foundation

@MainActor
class OssLicensesViewModel: ObservableObject {
    @Published private(set) var uiState: OssLicensesUiState
    
    init() {
        self.uiState = OssLicensesUiState(licenses: Self.loadLicenses())
    }
    
    private static func loadLicenses() -> [OssLicense] {
        guard let settingsBundlePath = Bundle.main.path(forResource: "Settings", ofType: "bundle"),
              let settingsBundle = Bundle(path: settingsBundlePath),
              let licensePlistPath = settingsBundle.path(forResource: "com.mono0926.LicensePlist", ofType: "plist"),
              let licensePlistData = NSDictionary(contentsOfFile: licensePlistPath),
              let preferenceSpecifiers = licensePlistData["PreferenceSpecifiers"] as? [[String: Any]] else {
            return []
        }
        
        var licenses: [OssLicense] = []
        
        for specifier in preferenceSpecifiers {
            guard let type = specifier["Type"] as? String,
                  type == "PSChildPaneSpecifier",
                  let title = specifier["Title"] as? String,
                  let file = specifier["File"] as? String else {
                continue
            }
            
            let licenseType: String
            let licenseText: String
            
            let fileComponents = file.components(separatedBy: "/")
            let fileName = fileComponents.last ?? file
            let subdirectory = fileComponents.count > 1 ? fileComponents[fileComponents.count - 2] : nil
            
            let filePath: String?
            if let subdirectory = subdirectory {
                filePath = settingsBundle.path(forResource: fileName, ofType: "plist", inDirectory: subdirectory)
            } else {
                filePath = settingsBundle.path(forResource: fileName, ofType: "plist")
            }
            
            if let filePath = filePath,
               let fileData = NSDictionary(contentsOfFile: filePath),
               let fileSpecifiers = fileData["PreferenceSpecifiers"] as? [[String: Any]],
               let firstSpecifier = fileSpecifiers.first {
                licenseType = firstSpecifier["License"] as? String ?? ""
                licenseText = firstSpecifier["FooterText"] as? String ?? ""
            } else {
                licenseType = ""
                licenseText = ""
            }
            
            licenses.append(OssLicense(
                title: title,
                licenseType: licenseType,
                licenseText: licenseText
            ))
        }
        
        return licenses
    }
}
