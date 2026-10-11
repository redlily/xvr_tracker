import Foundation
import common
import data


public class GetPoseLandmarkerSettingsConsumerUseCase {
    private let poseLandmarkerSettingsRepository: PoseLandmarkerSettingsRepository
    
    public init(poseLandmarkerSettingsRepository: PoseLandmarkerSettingsRepository) {
        self.poseLandmarkerSettingsRepository = poseLandmarkerSettingsRepository
    }
    
    public func callAsFunction() -> StateConsumer<PoseLandmarkerSettings> {
        return poseLandmarkerSettingsRepository.dataConsumer
    }
}
