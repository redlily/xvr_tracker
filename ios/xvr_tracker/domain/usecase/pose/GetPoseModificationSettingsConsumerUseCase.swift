import Foundation
import common
import data

public class GetPoseModificationSettingsConsumerUseCase {
    private let poseModificationSettingsRepository: PoseModificationSettingsRepository
    
    public init(poseModificationSettingsRepository: PoseModificationSettingsRepository) {
        self.poseModificationSettingsRepository = poseModificationSettingsRepository
    }
    
    public func callAsFunction() -> StateConsumer<PoseModificationSettings> {
        return poseModificationSettingsRepository.dataConsumer
    }
}
