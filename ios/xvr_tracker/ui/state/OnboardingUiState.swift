import Foundation
import SwiftUI
import common

class OnboardingUiState: ObservableObject {
    @Published var content: Content = .initializing
    @Published var dialogState: DialogState? = nil
    
    enum Content: Equatable {
        case initializing
        case initialized(
            bodyHeight: RangedValue<Double>,
            connectionAddress: String
        )
    }
    
    enum DialogState {
        case bodyHeightOutOfRangeDialogState(
            range: ClosedRange<Double>
        )
        case incorrectNetworkAddressDialogState
    }
}
