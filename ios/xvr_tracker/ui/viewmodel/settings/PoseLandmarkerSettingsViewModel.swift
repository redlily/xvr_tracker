import Foundation
import Combine
import infra
import common
import data
import di

@MainActor
class PoseLandmarkerSettingsViewModel: ObservableObject {
    @Published private(set) var uiState: PoseLandmarkerSettingsUiState
    
    private let poseLandmarkerSettingsRepository: PoseLandmarkerSettingsRepository
    private let taskBag = TaskBag()
    
    init(
        poseLandmarkerSettingsRepository: PoseLandmarkerSettingsRepository = AppContainer.shared.resolve()
    ) {
        self.poseLandmarkerSettingsRepository = poseLandmarkerSettingsRepository
        self.uiState = PoseLandmarkerSettingsUiState(
            poseLandmarkerSettings: poseLandmarkerSettingsRepository.dataConsumer.value
        )
        
        Task { [weak self] in
            for await settings in poseLandmarkerSettingsRepository.dataConsumer.stream() {
                self?.uiState.poseLandmarkerSettings = settings
            }
        }.store(in: taskBag)
    }
    
    func onModelTypeChange(_ modelType: PoseLandmarkerModelType) {
        poseLandmarkerSettingsRepository.setModelType(modelType)
    }
    
    func onComputeTypeChange(_ computeType: PoseLandmarkerComputeType) {
        poseLandmarkerSettingsRepository.setComputeType(computeType)
    }
    
    func onVisibilityThresholdChange(_ value: Double) {
        poseLandmarkerSettingsRepository.setVisibilityThreshold(value)
    }
    
    func onPresenceThresholdChange(_ value: Double) {
        poseLandmarkerSettingsRepository.setPresenceThreshold(value)
    }
}
