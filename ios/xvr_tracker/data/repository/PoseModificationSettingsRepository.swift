import Foundation
import Combine
import common

public final class PoseModificationSettingsRepository: Sendable {
    private let dataSource: PoseModificationSettingsDataSource

    private let dataEmitter = StateEmitter(PoseModificationSettings.default)
    public var dataConsumer: StateConsumer<PoseModificationSettings> { dataEmitter.eraseToStateConsumer() }
    
    init(dataSource: PoseModificationSettingsDataSource) {
        self.dataSource = dataSource
    }
    
    public func fetch() async {
        dataEmitter.value = PoseModificationSettings(
            standStabilizingCoefficient: dataSource.standStabilizingCoefficient.getValue(),
            sitStabilizingCoefficient: dataSource.sitStabilizingCoefficient.getValue(),
            lieDownStabilizingCoefficient: dataSource.lieDownStabilizingCoefficient.getValue(),
            stabilizingModeChangeThresholdTime: dataSource.stabilizingModeChangeThresholdTime.getValue(),
            isStabilizingModeChangeSoundEnabled: dataSource.isStabilizingModeChangeSoundEnabled.getValue(),
            bodyPositiveSlope: dataSource.bodyPositiveSlope.getValue(),
            bodyNegativeSlope: dataSource.bodyNegativeSlope.getValue(),
            footPositiveSlope: dataSource.footPositiveSlope.getValue(),
            footNegativeSlope: dataSource.footNegativeSlope.getValue(),
            kneePositiveSlope: dataSource.kneePositiveSlope.getValue(),
            kneeNegativeSlope: dataSource.kneeNegativeSlope.getValue()
        )
    }
    
    @discardableResult
    public func setStandStabilizingCoefficient(_ value: Double) -> Bool {
        return dataSource.standStabilizingCoefficient.setValue(value).onTrue {
            dataEmitter.update {
                $0.standStabilizingCoefficient = $0.standStabilizingCoefficient.update(value)
            }
        }
    }
    
    @discardableResult
    public func setSitStabilizingCoefficient(_ value: Double) -> Bool {
        return dataSource.sitStabilizingCoefficient.setValue(value).onTrue {
            dataEmitter.update {
                $0.sitStabilizingCoefficient = $0.sitStabilizingCoefficient.update(value)
            }
        }
    }
    
    @discardableResult
    public func setLieDownStabilizingCoefficient(_ value: Double) -> Bool {
        return dataSource.lieDownStabilizingCoefficient.setValue(value).onTrue {
            dataEmitter.update {
                $0.lieDownStabilizingCoefficient = $0.lieDownStabilizingCoefficient.update(value)
            }
        }
    }
    
    @discardableResult
    public func setStabilizingModeChangeThresholdTime(_ value: Int) -> Bool {
        return dataSource.stabilizingModeChangeThresholdTime.setValue(value).onTrue {
            dataEmitter.update {
                $0.stabilizingModeChangeThresholdTime = $0.stabilizingModeChangeThresholdTime.update(value)
            }
        }
    }
    
    @discardableResult
    public func setIsStabilizingModeChangeSoundEnabled(_ value: Bool) -> Bool {
        return dataSource.isStabilizingModeChangeSoundEnabled.setValue(value).onTrue {
            dataEmitter.update {
                $0.isStabilizingModeChangeSoundEnabled = value
            }
        }
    }
    
    @discardableResult
    public func setBodyPositiveSlope(_ value: Double) -> Bool {
        return dataSource.bodyPositiveSlope.setValue(value).onTrue {
            dataEmitter.update {
                $0.bodyPositiveSlope = $0.bodyPositiveSlope.update(value)
            }
        }
    }
    
    @discardableResult
    public func setBodyNegativeSlope(_ value: Double) -> Bool {
        return dataSource.bodyNegativeSlope.setValue(value).onTrue {
            dataEmitter.update {
                $0.bodyNegativeSlope = $0.bodyNegativeSlope.update(value)
            }
        }
    }
    
    @discardableResult
    public func setFootPositiveSlope(_ value: Double) -> Bool {
        return dataSource.footPositiveSlope.setValue(value).onTrue {
            dataEmitter.update {
                $0.footPositiveSlope = $0.footPositiveSlope.update(value)
            }
        }
    }

    @discardableResult
    public func setFootNegativeSlope(_ value: Double) -> Bool {
        return dataSource.footNegativeSlope.setValue(value).onTrue {
            dataEmitter.update {
                $0.footNegativeSlope = $0.footNegativeSlope.update(value)
            }
        }
    }
    
    @discardableResult
    public func setKneePositiveSlope(_ value: Double) -> Bool {
        return dataSource.kneePositiveSlope.setValue(value).onTrue {
            dataEmitter.update {
                $0.kneePositiveSlope = $0.kneePositiveSlope.update(value)
            }
        }
    }
    
    @discardableResult
    public func setKneeNegativeSlope(_ value: Double) -> Bool {
        return dataSource.kneeNegativeSlope.setValue(value).onTrue {
            dataEmitter.update {
                $0.kneeNegativeSlope = $0.kneeNegativeSlope.update(value)
            }
        }
    }
}
