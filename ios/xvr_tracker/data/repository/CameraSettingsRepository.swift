import Foundation
import Combine
import common

public class CameraSettingsRepository: @unchecked Sendable {
    private let dataSource: CameraSettingsDataSource

    private let dataEmitter = StateEmitter(CameraSettings.default)
    public var dataConsumer: StateConsumer<CameraSettings> { dataEmitter.eraseToStateConsumer() }
    
    init(dataSource: CameraSettingsDataSource) {
        self.dataSource = dataSource
    }
    
    public func fetch() async {
        dataEmitter.value = CameraSettings(
            lensFacingType: dataSource.lensFacingType.getValue(),
            zoomRatio: dataSource.zoomRatio.getValue()
        )
    }
    
    @discardableResult
    public func setLensFacingType(_ value: CameraLensFacingType) -> Bool {
        return dataSource.lensFacingType.setValue(value).onTrue {
            dataEmitter.update {
                $0.lensFacingType = value
            }
        }
    }
    
    @discardableResult
    public func setZoomRatio(_ value: Double) -> Bool {
        return dataSource.zoomRatio.setValue(value).onTrue {
            dataEmitter.update {
                $0.zoomRatio = value
            }
        }
    }
}
