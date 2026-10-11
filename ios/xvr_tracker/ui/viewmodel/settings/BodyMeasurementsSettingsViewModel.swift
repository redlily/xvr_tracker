import Foundation
import Combine
import common
import data
import di

@MainActor
class BodyMeasurementsSettingsViewModel: ObservableObject {
    @Published private(set) var uiState: BodyMeasurementsSettingsUiState
    
    private let bodyMeasurementsSettingsRepository: BodyMeasurementsSettingsRepository
    private let taskBag = TaskBag()
    
    init(
        bodyMeasurementsSettingsRepository: BodyMeasurementsSettingsRepository = AppContainer.shared.resolve()
    ) {
        self.bodyMeasurementsSettingsRepository = bodyMeasurementsSettingsRepository
        uiState = BodyMeasurementsSettingsUiState(
            bodyMeasurementsSettings: bodyMeasurementsSettingsRepository.dataConsumer.value
        )
        
        Task { [weak self] in
            for await settings in bodyMeasurementsSettingsRepository.dataConsumer.stream() {
                self?.uiState.bodyMeasurementsSettings = settings
            }
        }.store(in: taskBag)
    }
    
    func onBodyHeightChange(_ value: Double) {
        bodyMeasurementsSettingsRepository.setBodyHeight(value)
    }
    
    func onUpperBodyHeightRatioChange(_ value: Double) {
        bodyMeasurementsSettingsRepository.setUpperBodyHeightRatio(value)
    }
    
    func onHeadHeightRatioChange(_ value: Double) {
        bodyMeasurementsSettingsRepository.setHeadHeightRatio(value)
    }
    
    func onHeadZOffsetRatioChange(_ value: Double) {
        bodyMeasurementsSettingsRepository.setHeadZOffsetRatio(value)
    }
    
    func onNeckLengthRatioChange(_ value: Double) {
        bodyMeasurementsSettingsRepository.setNeckLengthRatio(value)
    }
    
    func onShoulderWidthRatioChange(_ value: Double) {
        bodyMeasurementsSettingsRepository.setShoulderWidthRatio(value)
    }
    
    func onArmLengthRatioChange(_ value: Double) {
        bodyMeasurementsSettingsRepository.setArmLengthRatio(value)
    }
    
    func onChestPositionRatioChange(_ value: Double) {
        bodyMeasurementsSettingsRepository.setChestPositionRatio(value)
    }
    
    func onWaistPositionRatioChange(_ value: Double) {
        bodyMeasurementsSettingsRepository.setWaistPositionRatio(value)
    }
    
    func onHipWidthRatioChange(_ value: Double) {
        bodyMeasurementsSettingsRepository.setHipWidthRatio(value)
    }
    
    func onAnkleHeightRatioChange(_ value: Double) {
        bodyMeasurementsSettingsRepository.setAnkleHeightRatio(value)
    }
    
    func onFootLengthRatioChange(_ value: Double) {
        bodyMeasurementsSettingsRepository.setFootLengthRatio(value)
    }
    
    func onRatioReset() {
        bodyMeasurementsSettingsRepository.reset()
    }
}
