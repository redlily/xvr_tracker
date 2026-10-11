import Foundation
import Combine
import common
import data
import di

@MainActor
class PoseTrackerAdvancedSettingsViewModel: ObservableObject {
    @Published private(set) var uiState: PoseTrackerAdvancedSettingsUiState
    
    private let poseModificationSettingsRepository: PoseModificationSettingsRepository
    private let taskBag = TaskBag()
    
    init(
        poseModificationSettingsRepository: PoseModificationSettingsRepository = AppContainer.shared.resolve()
    ) {
        self.poseModificationSettingsRepository = poseModificationSettingsRepository
        self.uiState = PoseTrackerAdvancedSettingsUiState(
            poseModificationSettings: poseModificationSettingsRepository.dataConsumer.value
        )
        
        Task { [weak self] in
            for await settings in poseModificationSettingsRepository.dataConsumer.stream() {
                self?.uiState.poseModificationSettings = settings
            }
        }.store(in: taskBag)
    }
    
    func onStandStabilizingCoefficientChange(_ value: Double) {
        poseModificationSettingsRepository.setStandStabilizingCoefficient(value)
    }
    
    func onSitStabilizingCoefficientChange(_ value: Double) {
        poseModificationSettingsRepository.setSitStabilizingCoefficient(value)
    }
    
    func onLieDownStabilizingCoefficientChange(_ value: Double) {
        poseModificationSettingsRepository.setLieDownStabilizingCoefficient(value)
    }
    
    func onStabilizingModeChangeThresholdTimeChange(_ value: Int) {
        poseModificationSettingsRepository.setStabilizingModeChangeThresholdTime(value)
    }
    
    func onStabilizingModeChangeSoundEnabledChange(_ value: Bool) {
        poseModificationSettingsRepository.setIsStabilizingModeChangeSoundEnabled(value)
    }
    
    func onBodyPositiveSlopeChange(_ value: Double) {
        poseModificationSettingsRepository.setBodyPositiveSlope(value)
    }
    
    func onBodyNegativeSlopeChange(_ value: Double) {
        poseModificationSettingsRepository.setBodyNegativeSlope(value)
    }
    
    func onFootPositiveSlopeChange(_ value: Double) {
        poseModificationSettingsRepository.setFootPositiveSlope(value)
    }
    
    func onFootNegativeSlopeChange(_ value: Double) {
        poseModificationSettingsRepository.setFootNegativeSlope(value)
    }
    
    func onKneePositiveSlopeChange(_ value: Double) {
        poseModificationSettingsRepository.setKneePositiveSlope(value)
    }
    
    func onKneeNegativeSlopeChange(_ value: Double) {
        poseModificationSettingsRepository.setKneeNegativeSlope(value)
    }
}
