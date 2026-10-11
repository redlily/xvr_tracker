import SwiftUI

struct VrcConnectionSettingsScreen: View {
    @StateObject private var viewModel = VrcConnectionSettingsViewModel()
    
    var body: some View {
        VrcConnectionSettingsContent(
            uiState: viewModel.uiState,
            onSendingIpOrDomainChange: viewModel.onSendingIpOrDomainChange,
            onSendingPortChange: viewModel.onSendingPortChange
        )
    }
}
