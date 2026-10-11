import SwiftUI

struct OssLicensesContent: View {
    let uiState: OssLicensesUiState
    let onLicenseClick: (OssLicense) -> Void
    
    var body: some View {
        ScrollView {
            VStack(spacing: 0) {
                ForEach(uiState.licenses) { license in
                    SettingItem(
                        title: license.title,
                        description: license.licenseType.isEmpty ? nil : license.licenseType
                    )
                    .onClick {
                        onLicenseClick(license)
                    }
                }
            }
        }
        .contentMargins(.bottom, 32, for: .scrollContent)
        .navigationTitle(String(localized: "settings_top_oss_info"))
        .navigationBarTitleDisplayMode(.inline)
    }
}


#Preview {
    OssLicensesContent(
        uiState: OssLicensesUiState(
            licenses: [
                OssLicense(
                    title: "Swinject",
                    licenseType: "MIT",
                    licenseText: "The MIT License (MIT)..."
                )
            ]
        ),
        onLicenseClick: { _ in }
    )
}
