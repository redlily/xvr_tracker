import SwiftUI

struct OnboardingScreen: View {
    @StateObject var viewModel: OnboardingViewModel = OnboardingViewModel()
    
    let onOnboardingExit: () -> Void
    
    var body: some View {
        OnboardingContent(
            uiState: viewModel.uiState,
            onOnboardingExit: viewModel.onOnboardingExit,
            onBodyHeightChange: viewModel.onBodyHeightChange,
            onConnectionAddressChange: viewModel.onIpAddressOrDomain,
            onDialogClose: viewModel.onErrorDissmiss
        )
        .onReceive(viewModel.onboardingExitPublisher, perform: onOnboardingExit)
    }
}
