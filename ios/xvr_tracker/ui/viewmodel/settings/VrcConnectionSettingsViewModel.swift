import Foundation
import Combine
import common
import di
import data
import domain

@MainActor
class VrcConnectionSettingsViewModel: ObservableObject {
    @Published private(set) var uiState: VrcConnectionSettingsUiState

    private let vrcConnectionSettingsRepository: VrcConnectionSettingsRepository
    private let getPrivateNetworkRoutesUseCase: GetPrivateNetworkRoutesUseCase
    
    private let taskBag = TaskBag()

    init(
        vrcConnectionSettingsRepository: VrcConnectionSettingsRepository = AppContainer.shared.resolve(),
        getPrivateNetworkRoutesUseCase: GetPrivateNetworkRoutesUseCase = AppContainer.shared.resolve()
    ) {
        self.vrcConnectionSettingsRepository = vrcConnectionSettingsRepository
        self.getPrivateNetworkRoutesUseCase = getPrivateNetworkRoutesUseCase
        
        self.uiState = VrcConnectionSettingsUiState(
            vrcConnectionSettings: vrcConnectionSettingsRepository.dataConsumer.value
        )
        
        Task { [weak self] in
            let ipAddresses = getPrivateNetworkRoutesUseCase()
            if let firstInfo = ipAddresses.first {
                self?.uiState.defaultSendingIpPrefix = makeIpPrefix(
                    address: firstInfo.address,
                    prefix: firstInfo.prefix
                )
            }
        }.store(in: taskBag)
        
        Task { [weak self] in
            for await settings in vrcConnectionSettingsRepository.dataConsumer.stream() {
                self?.uiState.vrcConnectionSettings = settings
            }
        }.store(in: taskBag)
    }
    
    func onSendingIpOrDomainChange(_ value: String) -> Bool {
        return vrcConnectionSettingsRepository.setSendingIpAddressOrDomain(value)
    }
    
    func onSendingPortChange(_ value: Int) -> Bool {
        return vrcConnectionSettingsRepository.setSendingPort(value)
    }
}
