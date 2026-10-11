import Foundation
import Network
import common
import data

public final class VrcClientService: @unchecked Sendable {
    private static let BASE_PATH = "/tracking/trackers"
    private static let POSITION_PATH = "position"
    private static let ROTATION_PATH = "rotation"
    
    private let vrcConnectionSettingsRepository: VrcConnectionSettingsRepository
    private let simpleOscClient: SimpleOscClient
    
    private var isSetUp: Bool = false
    private let mutex = AsyncMutex()
    
    public init(
        vrcConnectionSettingsRepository: VrcConnectionSettingsRepository,
        simpleOscClient: SimpleOscClient
    ) {
        self.vrcConnectionSettingsRepository = vrcConnectionSettingsRepository
        self.simpleOscClient = simpleOscClient
    }
    
    public func setUp() async {
        try? await mutex.withLock {
            if isSetUp {
                await internalTearDown()
            }
            
            let settings = vrcConnectionSettingsRepository.dataConsumer.value
            
            await simpleOscClient.setup(
                interface: nil,
                host: .name(settings.sendingIpAddressOrDomain, nil),
                port: NWEndpoint.Port(rawValue: UInt16(settings.sendingPort.value))!
            )
            
            isSetUp = true
        }
    }
    
    public func termDown() async {
        try? await mutex.withLock {
            await self.internalTearDown()
            self.isSetUp = false
        }
    }
    
    private func internalTearDown() async {
        await simpleOscClient.teardown()
    }
    
    public func sendPoseTrackerData(_ data: PoseTrackersData) async {
        await mutex.withTryLock {
            if !isSetUp {
                return
            }
            let modificationMatrix = data.modificationMatrix
            for trackers in data.trackers {
                for (type, trackerData) in trackers {
                    let id = switch type {
                    case .head: "head"
                    case .leftFeet: "1"
                    case .rightFeet: "2"
                    case .waist: "3"
                    case .chest: "4"
                    case .leftKnee: "5"
                    case .rightKnee: "6"
                    case .leftElbow: "7"
                    case .rightElbow: "8"
                    }
                    let path = "\(Self.BASE_PATH)/\(id)"
                    let attitude = modificationMatrix * trackerData.attitude
                    
                    if trackerData.dataType.isPositionEnabled {
                        let position = attitude.columnVector3(3)
                        await simpleOscClient.send(
                            path: "\(path)/\(Self.POSITION_PATH)",
                            params: [Float(position.x), Float(position.y), Float(position.z)]
                        )
                    }
                    
                    if trackerData.dataType.isRotationEnabled {
                        let angle = -attitude.transpose().toEulerAngleZXY(isDegree: true)
                        await simpleOscClient.send(
                            path: "\(path)/\(Self.ROTATION_PATH)",
                            params: [Float(angle.beta), Float(angle.gamma), Float(angle.alpha)]
                        )
                    }
                }
            }
        }
    }
}
