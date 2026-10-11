import Foundation
import Combine
import common

public final class PoseTrackerSettingsRepository: Sendable {
    private let dataSource: PoseTrackerSettingsDataSource

    private let dataEmitter = StateEmitter(PoseTrackerSettings.default)
    public var dataConsumer: StateConsumer<PoseTrackerSettings> { dataEmitter.eraseToStateConsumer() }
    
    init(dataSource: PoseTrackerSettingsDataSource) {
        self.dataSource = dataSource
    }
    
    public func fetch() async {
        dataEmitter.value = PoseTrackerSettings(
            head: dataSource.head.getValue(),
            chest: dataSource.chest.getValue(),
            waist: dataSource.waist.getValue(),
            elbow: dataSource.elbow.getValue(),
            knee: dataSource.knee.getValue(),
            feet: dataSource.feet.getValue()
        )
    }
    
    @discardableResult
    public func setHead(_ value: PoseTrackerMotionType) -> Bool {
        return dataSource.head.setValue(value).onTrue {
            dataEmitter.update {
                $0.head = value
            }
        }
    }
    
    @discardableResult
    public func setChest(_ value: PoseTrackerMotionType) -> Bool {
        return dataSource.chest.setValue(value).onTrue {
            dataEmitter.update {
                $0.chest = value
            }
        }
    }
    
    @discardableResult
    public func setWaist(_ value: PoseTrackerMotionType) -> Bool {
        return dataSource.waist.setValue(value).onTrue {
            dataEmitter.update {
                $0.waist = value
            }
        }
    }
    
    @discardableResult
    public func setElbow(_ value: PoseTrackerMotionType) -> Bool {
        return dataSource.elbow.setValue(value).onTrue {
            dataEmitter.update {
                $0.elbow = value
            }
        }
    }
    
    @discardableResult
    public func setKnee(_ value: PoseTrackerMotionType) -> Bool {
        return dataSource.knee.setValue(value).onTrue {
            dataEmitter.update {
                $0.knee = value
            }
        }
    }
    
    @discardableResult
    public func setFeet(_ value: PoseTrackerMotionType) -> Bool {
        return dataSource.feet.setValue(value).onTrue {
            dataEmitter.update {
                $0.feet = value
            }
        }
    }
}
