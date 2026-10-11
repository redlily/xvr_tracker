import SwiftUI
import Combine
import di
import data
import common
import domain

@MainActor
class OnboardingViewModel: ObservableObject {
    @Published var uiState = OnboardingUiState()
    
    private let onboardingSettingsRepository: OnboardingSettingsRepository
    private let bodyMeasurementsSettingsRepository: BodyMeasurementsSettingsRepository
    private let vrcConnectionSettingsRepository: VrcConnectionSettingsRepository
    private let getPrivateNetworkRoutesUseCase: GetPrivateNetworkRoutesUseCase
    
    private let _onboardingExitPublisher = PassthroughSubject<Void, Never>()
    var onboardingExitPublisher: AnyPublisher<Void, Never> { _onboardingExitPublisher.eraseToAnyPublisher() }
    
    init(
        onboardingSettingsRepository: OnboardingSettingsRepository = AppContainer.shared.resolve(),
        bodyMeasurementsSettingsRepository: BodyMeasurementsSettingsRepository = AppContainer.shared.resolve(),
        vrcConnectionSettingsRepository: VrcConnectionSettingsRepository = AppContainer.shared.resolve(),
        getPrivateNetworkRoutesUseCase: GetPrivateNetworkRoutesUseCase = AppContainer.shared.resolve()
    ) {
        self.onboardingSettingsRepository = onboardingSettingsRepository
        self.bodyMeasurementsSettingsRepository = bodyMeasurementsSettingsRepository
        self.vrcConnectionSettingsRepository = vrcConnectionSettingsRepository
        self.getPrivateNetworkRoutesUseCase = getPrivateNetworkRoutesUseCase
        
        Task {
            await bodyMeasurementsSettingsRepository.fetch()
            await vrcConnectionSettingsRepository.fetch()
            
            let bodyHeight = bodyMeasurementsSettingsRepository.dataConsumer.value.bodyHeight
            let existingAddress = vrcConnectionSettingsRepository.dataConsumer.value.sendingIpAddressOrDomain
            let connectionAddress: String
            if existingAddress.isEmpty {
                if let info = getPrivateNetworkRoutesUseCase().first {
                    connectionAddress = makeIpPrefix(address: info.address, prefix: info.prefix)
                } else {
                    connectionAddress = ""
                }
            } else {
                connectionAddress = existingAddress
            }
            
            uiState.content = .initialized(
                bodyHeight: bodyHeight,
                connectionAddress: connectionAddress
            )
        }
    }
    
    func onOnboardingExit() {
        onboardingSettingsRepository.setDone(true)
        _onboardingExitPublisher.send(())
    }
    
    func onBodyHeightChange(_ value: Double) -> Bool {
        let heightInMeters = value / 100.0
        let range = BodyMeasurementsSettings.bodyHeightRange
        let rangeInCm = (range.lowerBound * 100)...(range.upperBound * 100)
        return bodyMeasurementsSettingsRepository.setBodyHeight(heightInMeters).onFalse {
            uiState.dialogState = .bodyHeightOutOfRangeDialogState(range: rangeInCm)
        }
    }
    
    func onIpAddressOrDomain(_ value: String) -> Bool {
        return vrcConnectionSettingsRepository.setSendingIpAddressOrDomain(value).onFalse {
            uiState.dialogState = .incorrectNetworkAddressDialogState
        }
    }
    
    func onErrorDissmiss() {
        uiState.dialogState = nil
    }
}
