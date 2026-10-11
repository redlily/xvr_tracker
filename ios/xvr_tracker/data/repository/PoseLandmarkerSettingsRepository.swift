import Foundation
import Combine
import infra
import common

public final class PoseLandmarkerSettingsRepository: Sendable {
    private let dataSource: PoseLandmarkerSettingsDataSource

    private let dataEmitter = StateEmitter(PoseLandmarkerSettings.default)
    public var dataConsumer: StateConsumer<PoseLandmarkerSettings> { dataEmitter.eraseToStateConsumer() }
    
    init(dataSource: PoseLandmarkerSettingsDataSource) {
        self.dataSource = dataSource
    }
    
    public func fetch() async {
        dataEmitter.value = PoseLandmarkerSettings(
            modelType: dataSource.modelType.getValue(),
            computeType: dataSource.computeType.getValue(),
            visibilityThreshold: dataSource.visibilityThreshold.getValue(),
            presenceThreshold: dataSource.presenceThreshold.getValue()
        )
    }
    
    @discardableResult
    public func setModelType(_ value: PoseLandmarkerModelType) -> Bool {
        return dataSource.modelType.setValue(value).onTrue {
            dataEmitter.update {
                $0.modelType = value
            }
        }
    }
    
    @discardableResult
    public func setComputeType(_ value: PoseLandmarkerComputeType) -> Bool {
        return dataSource.computeType.setValue(value).onTrue {
            dataEmitter.update {
                $0.computeType = value
            }
        }
    }
    
    @discardableResult
    public func setVisibilityThreshold(_ value: Double) -> Bool {
        return dataSource.visibilityThreshold.setValue(value).onTrue {
            dataEmitter.update {
                $0.visibilityThreshold = $0.visibilityThreshold.update(value)
            }
        }
    }
    
    @discardableResult
    public func setPresenceThreshold(_ value: Double) -> Bool {
        return dataSource.presenceThreshold.setValue(value).onTrue {
            dataEmitter.update {
                $0.presenceThreshold = $0.presenceThreshold.update(value)
            }
        }
    }
}
