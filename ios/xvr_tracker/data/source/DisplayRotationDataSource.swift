import Foundation
import UIKit
import common
import os.lock

public final class DisplayRotationDataSource: Sendable {
    private let _latestValue = OSAllocatedUnfairLock<UIDeviceOrientation?>(initialState: nil)
    public var latestValue: UIDeviceOrientation { _latestValue.withLock { current in current ?? .unknown } }
    
    private let _rotationConsumer = Lazy<CallbackConsumer<UIDeviceOrientation>>()
    public var rotationConsumer: AnyConsumer<UIDeviceOrientation> {
        _rotationConsumer.get {
            CallbackConsumer<UIDeviceOrientation> { [weak self] cont in
                Task.detached { @MainActor in
                    let app = UIApplication.shared
                    let keyWindow = app.connectedScenes
                        .compactMap { $0 as? UIWindowScene }
                        .flatMap { $0.windows }
                        .first { $0.isKeyWindow }
                    let supportedOrientations = app.supportedInterfaceOrientations(for: keyWindow)
                    
                    UIDevice.current.beginGeneratingDeviceOrientationNotifications()
                    
                    let orientation = UIDevice.current.orientation
                    if orientation.isValidInterfaceOrientation {
                        cont.yield(orientation)
                    }
                    
                    let observer = NotificationCenter.default.addObserver(
                        forName: UIDevice.orientationDidChangeNotification,
                        object: nil,
                        queue: .main
                    ) { [cont = UncheckedSendableBox(cont)] _ in
                        Task.detached { @MainActor () -> Void in
                            guard let self else { return }
                            let orientation = UIDevice.current.orientation
                            if orientation.isValidInterfaceOrientation,
                               let mask = orientation.interfaceMask,
                               supportedOrientations.contains(mask)
                            {
                                self._latestValue.withLock { current in current = orientation }
                                cont.value.yield(UIDevice.current.orientation)
                            }
                        }
                    }
                    
                    cont.onTermination = { [observer = UncheckedSendableBox(observer)] _ in
                        Task.detached { @MainActor in
                            UIDevice.current.endGeneratingDeviceOrientationNotifications()
                            NotificationCenter.default.removeObserver(observer.value)
                        }
                    }
                }
            } onSubscribe: { [weak self] cont in
                if let latestValue = self?._latestValue.withLock({ current in current }) {
                    cont.yield(latestValue)
                }
            }
        }.eraseToAnyConsumer()
    }
}
