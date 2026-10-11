import Foundation
import common
import data

public class GetVrcConnectionSettingsConsumerUseCase {
    private let vrcConnectionSettingsRepository: VrcConnectionSettingsRepository
    
    public init(vrcConnectionSettingsRepository: VrcConnectionSettingsRepository) {
        self.vrcConnectionSettingsRepository = vrcConnectionSettingsRepository
    }
    
    public func callAsFunction() -> StateConsumer<VrcConnectionSettings> {
        return vrcConnectionSettingsRepository.dataConsumer
    }
}
