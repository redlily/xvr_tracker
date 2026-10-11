import SwiftUI

struct OssLicensesScreen: View {
    @StateObject private var viewModel = OssLicensesViewModel()
    @State private var selectedLicense: OssLicense?
    
    var body: some View {
        OssLicensesContent(
            uiState: viewModel.uiState,
            onLicenseClick: { license in
                selectedLicense = license
            }
        )
        .sheet(item: $selectedLicense) { license in
            LicenseDetailView(license: license)
        }
    }
}

private struct LicenseDetailView: View {
    let license: OssLicense
    @Environment(\.dismiss) private var dismiss
    
    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    if !license.licenseType.isEmpty {
                        Text(license.licenseType)
                            .font(.headline)
                            .padding(.bottom, 8)
                    }
                    
                    Text(license.licenseText)
                        .font(.body)
                        .textSelection(.enabled)
                }
                .padding()
            }
            .navigationTitle(license.title)
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button(String(localized: "cancel")) {
                        dismiss()
                    }
                }
            }
        }
    }
}

#Preview {
    NavigationStack {
        OssLicensesScreen()
    }
}
