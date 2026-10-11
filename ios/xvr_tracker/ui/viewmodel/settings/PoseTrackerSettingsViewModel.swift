import Foundation
import Combine
import common
import data
import di

@MainActor
class PoseTrackerSettingsViewModel: ObservableObject {
    @Published private(set) var uiState: PoseTrackerSettingsUiState
    
    private let poseTrackerSettingsRepository: PoseTrackerSettingsRepository
    private let taskBag = TaskBag()
    
    init(
        poseTrackerSettingsRepository: PoseTrackerSettingsRepository = AppContainer.shared.resolve()
    ) {
        self.poseTrackerSettingsRepository = poseTrackerSettingsRepository
        self.uiState = PoseTrackerSettingsUiState(
            poseTrackerSettings: poseTrackerSettingsRepository.dataConsumer.value
        )
        
        Task { [weak self] in
            for await settings in poseTrackerSettingsRepository.dataConsumer.stream() {
                self?.uiState.poseTrackerSettings = settings
            }
        }.store(in: taskBag)
    }
    
    func onHeadTrackerDataTypeChange(_ motionType: PoseTrackerMotionType) {
        poseTrackerSettingsRepository.setHead(motionType)
    }
    
    func onChestTrackerDataTypeChange(_ motionType: PoseTrackerMotionType) {
        poseTrackerSettingsRepository.setChest(motionType)
    }
    
    func onWaistTrackerDataTypeChange(_ motionType: PoseTrackerMotionType) {
        poseTrackerSettingsRepository.setWaist(motionType)
    }
    
    func onElbowTrackerDataTypeChange(_ motionType: PoseTrackerMotionType) {
        poseTrackerSettingsRepository.setElbow(motionType)
    }
    
    func onKneeTrackerDataTypeChange(_ motionType: PoseTrackerMotionType) {
        poseTrackerSettingsRepository.setKnee(motionType)
    }
    
    func onFeetTrackerDataTypeChange(_ motionType: PoseTrackerMotionType) {
        poseTrackerSettingsRepository.setFeet(motionType)
    }
    
    func onPresetTrackingSettingsChose(_ preset: PresetPoseTrackerSettings?) {
        guard let preset = preset else { return }
        
        // TODO: Implement logic to apply preset settings
        print("Preset Tracking Settings Chose: \(preset)")
        
        switch preset {
        case .point7:
            onHeadTrackerDataTypeChange(.positionAndRotation)
            onChestTrackerDataTypeChange(.positionAndRotation)
            onWaistTrackerDataTypeChange(.positionAndRotation)
            onElbowTrackerDataTypeChange(.disable)
            onKneeTrackerDataTypeChange(.disable)
            onFeetTrackerDataTypeChange(.positionAndRotation)
            
        case .point9:
            onHeadTrackerDataTypeChange(.positionAndRotation)
            onChestTrackerDataTypeChange(.positionAndRotation)
            onWaistTrackerDataTypeChange(.positionAndRotation)
            onElbowTrackerDataTypeChange(.positionAndRotation)
            onKneeTrackerDataTypeChange(.disable)
            onFeetTrackerDataTypeChange(.positionAndRotation)
            
        case .point10:
            onHeadTrackerDataTypeChange(.positionAndRotation)
            onChestTrackerDataTypeChange(.positionAndRotation)
            onWaistTrackerDataTypeChange(.positionAndRotation)
            onElbowTrackerDataTypeChange(.positionAndRotation)
            onKneeTrackerDataTypeChange(.positionAndRotation)
            onFeetTrackerDataTypeChange(.positionAndRotation)
        @unknown default: break
        }
    }
}
