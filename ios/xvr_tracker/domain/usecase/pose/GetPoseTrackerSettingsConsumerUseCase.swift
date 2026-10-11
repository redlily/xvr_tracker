import Foundation
import common
import data

public class GetPoseTrackerSettingsConsumerUseCase {
    private let poseTrackerSettingsRepository: PoseTrackerSettingsRepository
    
    public init(poseTrackerSettingsRepository: PoseTrackerSettingsRepository) {
        self.poseTrackerSettingsRepository = poseTrackerSettingsRepository
    }
    
    public func callAsFunction() -> StateConsumer<PoseTrackerSettings> {
        return poseTrackerSettingsRepository.dataConsumer
    }
}
