import Foundation
import CoreMotion
import os.lock
import core_type
import common

public final class AccelerationDataSource: Sendable {
    private let _latestValue = OSAllocatedUnfairLock<Vector3>(initialState: .zero)
    public var latestValue: Vector3 { _latestValue.withLock { $0 } }
    
    private let _accelerationConsumer = Lazy<AnyConsumer<Vector3>>()
    public var accelerationConsumer: AnyConsumer<Vector3> {
        _accelerationConsumer.get {
            CallbackConsumer<Vector3> { cont in
                let motionManager = CMMotionManager()
                guard motionManager.isAccelerometerAvailable else {
                    return
                }
                motionManager.accelerometerUpdateInterval = 0.1
                motionManager.startAccelerometerUpdates(to: .main) { data, error in
                    guard let accelerationData = data?.acceleration else {
                        return
                    }
                    let vector = Vector3(
                        x: accelerationData.x,
                        y: accelerationData.y,
                        z: accelerationData.z
                    )
                    self._latestValue.withLock { $0 = vector }
                    cont.yield(vector)
                }
                cont.onTermination = { [manager = UncheckedSendableBox(motionManager)] _ in
                    manager.value.stopAccelerometerUpdates()
                }
            }.eraseToAnyConsumer()
        }
    }
}
