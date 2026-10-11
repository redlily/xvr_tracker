import Foundation
import Combine
import UIKit
import Network
import common
import data
import di
import domain


@MainActor
class DebugSettingsViewModel: ObservableObject {
    @Published private(set) var uiState: DebugSettingsUiState
    @Published var showCopiedAlert = false
    
    private let analyzedDataPlotServerSettingsRepository: AnalyzedDataPlotServerSettingsRepository
    private let getPrivateNetworkRoutesUseCase: GetPrivateNetworkRoutesUseCase
    
    private let taskBag = TaskBag()
    
    init(
        analyzedDataPlotServerSettingsRepository: AnalyzedDataPlotServerSettingsRepository = AppContainer.shared.resolve(),
        getPrivateNetworkRoutesUseCase: GetPrivateNetworkRoutesUseCase = AppContainer.shared.resolve()
    ) {
        self.analyzedDataPlotServerSettingsRepository = analyzedDataPlotServerSettingsRepository
        self.getPrivateNetworkRoutesUseCase = getPrivateNetworkRoutesUseCase
        
        self.uiState = DebugSettingsUiState(
            analyzedDataPlotServerSettings: analyzedDataPlotServerSettingsRepository.dataConsumer.value,
            localIpAddress: nil
        )
        
        Task { [weak self]  in
            guard let info = getPrivateNetworkRoutesUseCase().first(where: { $0.address is IPv4Address }) else { return }
            self?.uiState.localIpAddress = (info.address as? IPv4Address)?.debugDescription
        }.store(in: taskBag)
        
        Task { [weak self] in
            for await settings in analyzedDataPlotServerSettingsRepository.dataConsumer.stream() {
                self?.uiState.analyzedDataPlotServerSettings = settings
            }
        }.store(in: taskBag)
    }
    
    func onAnalyzedDataPlotServerEnabledChange(_ value: Bool) {
        analyzedDataPlotServerSettingsRepository.setIsEnabled(value)
    }
    
    func onAnalyzedDataPlotServerPortChange(_ value: Int) {
        analyzedDataPlotServerSettingsRepository.setPort(value)
    }
    
    func onAnalyzedDataPlotServerPingChange(_ value: Int) {
        analyzedDataPlotServerSettingsRepository.setPing(value)
    }
    
    func onAnalyzedDataPlotServerTimeoutChange(_ value: Int) {
        analyzedDataPlotServerSettingsRepository.setTimeout(value)
    }
    
    func onCopyAnalyzedDataPlotServerUrl(_ url: String) {
        UIPasteboard.general.string = url
        showCopiedAlert = true
    }
}
