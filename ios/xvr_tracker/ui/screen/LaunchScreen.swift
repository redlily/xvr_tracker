import SwiftUI

struct LaunchScreen: View {
    @StateObject var viewModel = LaunchViewModel()
    
    let onOnboardingNavigate: () -> Void
    let onTopNavigate: () -> Void
    
    var body: some View {
        LaunchContent()
            .onAppear {
                viewModel.onReady()
            }
            .onReceive(viewModel.onboardingNavigatePublisher, perform: onOnboardingNavigate)
            .onReceive(viewModel.topNavigatePublisher, perform: onTopNavigate)
    }
}
