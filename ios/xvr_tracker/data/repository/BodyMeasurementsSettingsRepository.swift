import Foundation
import Combine
import common

public final class BodyMeasurementsSettingsRepository: Sendable {
    private let dataSource: BodyMeasurementsSettingsDataSource

    private let dataEmitter = StateEmitter(BodyMeasurementsSettings.default)
    public var dataConsumer: StateConsumer<BodyMeasurementsSettings> { dataEmitter.eraseToStateConsumer() }
    
    init(dataSource: BodyMeasurementsSettingsDataSource) {
        self.dataSource = dataSource
    }
    
    public func fetch() async {
        dataEmitter.value = BodyMeasurementsSettings(
            bodyHeight: dataSource.bodyHeight.getValue(),
            upperBodyHeightRatio: dataSource.upperBodyHeightRatio.getValue(),
            headHeightRatio: dataSource.headHeightRatio.getValue(),
            headZOffsetRatio: dataSource.headZOffsetRatio.getValue(),
            neckLengthRatio: dataSource.neckLengthRatio.getValue(),
            shoulderWidthRatio: dataSource.shoulderWidthRatio.getValue(),
            armLengthRatio: dataSource.armLengthRatio.getValue(),
            chestPositionRatio: dataSource.chestPositionRatio.getValue(),
            waistPositionRatio: dataSource.waistPositionRatio.getValue(),
            hipWidthRatio: dataSource.hipWidthRatio.getValue(),
            ankleHeightRatio: dataSource.ankleHeightRatio.getValue(),
            footLengthRatio: dataSource.footLengthRatio.getValue()
        )
    }
    
    @discardableResult
    public func setBodyHeight(_ value: Double) -> Bool {
        return dataSource.bodyHeight.setValue(value).onTrue {
            dataEmitter.update {
                $0.bodyHeight = $0.bodyHeight.update(value)
            }
        }
    }
    
    @discardableResult
    public func setUpperBodyHeightRatio(_ value: Double) -> Bool {
        return dataSource.upperBodyHeightRatio.setValue(value).onTrue {
            dataEmitter.update {
                $0.upperBodyHeightRatio = $0.upperBodyHeightRatio.update(value)
            }
        }
    }
    
    @discardableResult
    public func setHeadHeightRatio(_ value: Double) -> Bool {
        return dataSource.headHeightRatio.setValue(value).onTrue {
            dataEmitter.update {
                $0.headHeightRatio = $0.headHeightRatio.update(value)
            }
        }
    }
    
    @discardableResult
    public func setHeadZOffsetRatio(_ value: Double) -> Bool {
        return dataSource.headZOffsetRatio.setValue(value).onTrue {
            dataEmitter.update {
                $0.headZOffsetRatio = $0.headZOffsetRatio.update(value)
            }
        }
    }
    
    @discardableResult
    public func setNeckLengthRatio(_ value: Double) -> Bool {
        return dataSource.neckLengthRatio.setValue(value).onTrue {
            dataEmitter.update {
                $0.neckLengthRatio = $0.neckLengthRatio.update(value)
            }
        }
    }
    
    @discardableResult
    public func setShoulderWidthRatio(_ value: Double) -> Bool {
        return dataSource.shoulderWidthRatio.setValue(value).onTrue {
            dataEmitter.update {
                $0.shoulderWidthRatio = $0.shoulderWidthRatio.update(value)
            }
        }
    }
    
    @discardableResult
    public func setArmLengthRatio(_ value: Double) -> Bool {
        return dataSource.armLengthRatio.setValue(value).onTrue {
            dataEmitter.update {
                $0.armLengthRatio = $0.armLengthRatio.update(value)
            }
        }
    }
    
    @discardableResult
    public func setChestPositionRatio(_ value: Double) -> Bool {
        return dataSource.chestPositionRatio.setValue(value).onTrue {
            dataEmitter.update {
                $0.chestPositionRatio = $0.chestPositionRatio.update(value)
            }
        }
    }
    
    @discardableResult
    public func setWaistPositionRatio(_ value: Double) -> Bool {
        return dataSource.waistPositionRatio.setValue(value).onTrue {
            dataEmitter.update {
                $0.waistPositionRatio = $0.waistPositionRatio.update(value)
            }
        }
    }
    
    @discardableResult
    public func setHipWidthRatio(_ value: Double) -> Bool {
        return dataSource.hipWidthRatio.setValue(value).onTrue {
            dataEmitter.update {
                $0.hipWidthRatio = $0.hipWidthRatio.update(value)
            }
        }
    }
    
    @discardableResult
    public func setAnkleHeightRatio(_ value: Double) -> Bool {
        return dataSource.ankleHeightRatio.setValue(value).onTrue {
            dataEmitter.update {
                $0.ankleHeightRatio = $0.ankleHeightRatio.update(value)
            }
        }
    }
    
    @discardableResult
    public func setFootLengthRatio(_ value: Double) -> Bool {
        return dataSource.footLengthRatio.setValue(value).onTrue {
            dataEmitter.update {
                $0.footLengthRatio = $0.footLengthRatio.update(value)
            }
        }
    }
    
    public func reset() {
        let defaultSettings = BodyMeasurementsSettings.default
        _ = dataSource.upperBodyHeightRatio.setValue(defaultSettings.upperBodyHeightRatio.value)
        _ = dataSource.headHeightRatio.setValue(defaultSettings.headHeightRatio.value)
        _ = dataSource.headZOffsetRatio.setValue(defaultSettings.headZOffsetRatio.value)
        _ = dataSource.neckLengthRatio.setValue(defaultSettings.neckLengthRatio.value)
        _ = dataSource.shoulderWidthRatio.setValue(defaultSettings.shoulderWidthRatio.value)
        _ = dataSource.armLengthRatio.setValue(defaultSettings.armLengthRatio.value)
        _ = dataSource.chestPositionRatio.setValue(defaultSettings.chestPositionRatio.value)
        _ = dataSource.waistPositionRatio.setValue(defaultSettings.waistPositionRatio.value)
        _ = dataSource.hipWidthRatio.setValue(defaultSettings.hipWidthRatio.value)
        _ = dataSource.ankleHeightRatio.setValue(defaultSettings.ankleHeightRatio.value)
        _ = dataSource.footLengthRatio.setValue(defaultSettings.footLengthRatio.value)
        
        dataEmitter.update {
            $0.upperBodyHeightRatio = defaultSettings.upperBodyHeightRatio
            $0.headHeightRatio = defaultSettings.headHeightRatio
            $0.headZOffsetRatio = defaultSettings.headZOffsetRatio
            $0.neckLengthRatio = defaultSettings.neckLengthRatio
            $0.shoulderWidthRatio = defaultSettings.shoulderWidthRatio
            $0.armLengthRatio = defaultSettings.armLengthRatio
            $0.chestPositionRatio = defaultSettings.chestPositionRatio
            $0.waistPositionRatio = defaultSettings.waistPositionRatio
            $0.hipWidthRatio = defaultSettings.hipWidthRatio
            $0.ankleHeightRatio = defaultSettings.ankleHeightRatio
            $0.footLengthRatio = defaultSettings.footLengthRatio
        }
    }
}
