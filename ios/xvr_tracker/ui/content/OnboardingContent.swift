import SwiftUI
import common

struct OnboardingContent: View {
    @ObservedObject var uiState: OnboardingUiState
    let onOnboardingExit: () -> Void
    let onBodyHeightChange: (Double) -> Bool
    let onConnectionAddressChange: (String) -> Bool
    let onDialogClose: () -> Void
    
    @State private var currentPageIndex = 0
    @State private var bodyHeight: String = ""
    @State private var connectionAddress: String = ""
    
    @FocusState private var isKeyboardVisible: Bool
    
    private var initializedContent: (bodyHeight: RangedValue<Double>, connectionAddress: String)? {
        if case .initialized(let bodyHeight, let connectionAddress) = uiState.content {
            return (bodyHeight, connectionAddress)
        }
        return nil
    }
    
    private var pages: [OnboardingPageType] {
        OnboardingPageType.allCases
    }
    
    private var currentPageType: OnboardingPageType? {
        pages.indices.contains(currentPageIndex) ? pages[currentPageIndex] : nil
    }
    
    private func nextPage(force: Bool = false) {
        guard !force else {
            currentPageIndex += 1
            return
        }
        
        let canProceed: Bool
        if isKeyboardVisible {
            isKeyboardVisible = false
            canProceed = false
        } else {
            switch currentPageType {
            case .askBodyHeight:
                let heightValue = Double(bodyHeight) ?? 0
                canProceed = onBodyHeightChange(heightValue)
            case .askConnectionAddress:
                canProceed = onConnectionAddressChange(connectionAddress)
            default:
                canProceed = true
            }
        }
        
        if canProceed {
            currentPageIndex += 1
        }
    }
    
    var body: some View {
        ZStack {
            contentView
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .background(Color(.systemBackground))
            
            if case .initializing = uiState.content {
                ProgressView()
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                    .background(Color(.systemBackground))
            }
        }
        .onAppear {
            updateInitialValues()
        }
        .onChange(of: uiState.content) { oldValue, newValue in
            if case .initialized = newValue {
                updateInitialValues()
            }
        }
    }
    
    private func updateInitialValues() {
        if let initialized = initializedContent {
            bodyHeight = String(format: "%.1f", initialized.bodyHeight.value * 100)
            connectionAddress = initialized.connectionAddress
        }
    }
    
    private var contentView: some View {
        VStack(spacing: 0) {
            TabView(selection: $currentPageIndex) {
                ForEach(Array(pages.enumerated()), id: \.offset) { index, pageType in
                    Group {
                        switch pageType {
                        case .welcome:
                            WelcomePage()
                        
                        case .askBodyHeight:
                            AskBodyHeightPage(
                                height: $bodyHeight,
                                isKeyboardVisible: $isKeyboardVisible
                            )
                        case .askConnectionAddress:
                            AskConnectionAddressPage(
                                connectionAddress: $connectionAddress,
                                isKeyboardVisible: $isKeyboardVisible
                            )
                        
                        case .permissionSetup:
                            PermissionSetupPage()
                        
                        case .howToEnableOscInVrc:
                            HowToEnableOscInVrcPage()
                        
                        case .howToTestOscConnectionVrc:
                            HowToTestOscConnectionInVrcPage()
                        
                        case .howToCalibrateInVrc:
                            HowToCalibrateTrackerInVrcPage()
                        
                        case .complete:
                            CompletePage()
                        }
                    }
                    .gesture(DragGesture())
                    .tag(index)
                }
            }
            .tabViewStyle(.page(indexDisplayMode: .never))
            .animation(.easeInOut, value: currentPageIndex)
            .onChange(of: currentPageIndex) { _, _ in
                isKeyboardVisible = false
            }
            
            HStack {
                if currentPageIndex > 0 {
                    Button {
                        if isKeyboardVisible {
                            isKeyboardVisible = false
                        } else {
                            currentPageIndex -= 1
                        }
                    } label: {
                        Text(localized: "back")
                    }
                } else {
                    Button {
                        onOnboardingExit()
                    } label: {
                        Text(localized: "onboarding_skip")
                    }
                }
                
                Spacer()
                
                if currentPageIndex < pages.count - 1 {
                    Button {
                        nextPage()
                    } label: {
                        Text(localized: "next")
                    }
                } else {
                    Button {
                        onOnboardingExit()
                    } label: {
                        Text(localized: "complete")
                    }
                }
            }
            .padding(.horizontal, 32)
            .padding(.top, 16)
            .padding(.bottom, 16)
        }
        .onTapGesture {
            hideKeyboard()
        }
        .alert(
            String(localized: "onboarding_dialog_height_out_of_limits_title"),
            isPresented: Binding(
                get: {
                    if case .bodyHeightOutOfRangeDialogState = uiState.dialogState {
                        return true
                    }
                    return false
                },
                set: { _ in onDialogClose() }
            )
        ) {
            Button(String(localized: "confirm")) {
                onDialogClose()
            }
            Button(String(localized: "skip")) {
                onDialogClose()
                nextPage(force: true)
            }
        } message: {
            if case .bodyHeightOutOfRangeDialogState(let range) = uiState.dialogState {
                Text(String(localized: "onboarding_dialog_height_out_of_limits_description", range.lowerBound, range.upperBound))
            }
        }
        .alert(
            String(localized: "onboarding_dialog_incorrect_ip_or_domain_title"),
            isPresented: Binding(
                get: {
                    if case .incorrectNetworkAddressDialogState = uiState.dialogState {
                        return true
                    }
                    return false
                },
                set: { _ in onDialogClose() }
            )
        ) {
            Button(String(localized: "confirm")) {
                onDialogClose()
            }
            Button(String(localized: "skip")) {
                onDialogClose()
                nextPage(force: true)
            }
        } message: {
            Text(String(localized: "onboarding_dialog_incorrect_ip_or_domain_description"))
        }
    }
}

#Preview {
    OnboardingContent(
        uiState: OnboardingUiState(),
        onOnboardingExit: {},
        onBodyHeightChange: { _ in true },
        onConnectionAddressChange: { _ in true },
        onDialogClose: {}
    )
}
