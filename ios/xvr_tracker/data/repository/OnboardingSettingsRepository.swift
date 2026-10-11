import Foundation
import Combine
import common

public class OnboardingSettingsRepository: @unchecked Sendable {
    private let dataSource: OnboardingSettingDataSource

    private let dataEmitter = StateEmitter(OnboardingSettings.default)
    public var dataConsumer: StateConsumer<OnboardingSettings> { dataEmitter.eraseToStateConsumer() }
    
    init(dataSource: OnboardingSettingDataSource) {
        self.dataSource = dataSource
    }
    
    public func fetch() async {
        dataEmitter.value = OnboardingSettings(
            done: dataSource.done.getValue()
        )
    }
    
    @discardableResult
    public func setDone(_ value: Bool) -> Bool {
        return dataSource.done.setValue(value).onTrue {
            dataEmitter.update {
                $0.done = value
            }
        }
    }
}
