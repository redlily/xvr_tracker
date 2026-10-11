import Foundation
import Network
import common
import data

public actor TrackingDataNetworkHealthCheckWorkflow {
    public enum SuspendState: Sendable {
        case privateNetworkNotConnected
        case destinationAddressResolveingFailed
        case globalAddressSpecified
        case localNetworkDenied
        case unknownNetworkError
    }
    
    public enum ResumeState: Sendable {
        case confirm
        case abort
        case retry
    }
    
    private enum NetworkErrorState: Sendable {
        case localNetworkDenied
        case unknownNetworkError
    }
    
    private let connectionTestTimeoutMs = 10_000
    
    private let vrcConnectionSettingsRepository: VrcConnectionSettingsRepository
    private let getPrivateNetworkRoutesUseCase: GetPrivateNetworkRoutesUseCase
    
    public private(set) var isRunning: Bool = false
    public private(set) var isSuspended: Bool = false
    private var continuation: CheckedContinuation<ResumeState, Never>? = nil
    
    public init(
        vrcConnectionSettingsRepository: VrcConnectionSettingsRepository,
        getPrivateNetworkRoutesUseCase: GetPrivateNetworkRoutesUseCase
    ) {
        self.vrcConnectionSettingsRepository = vrcConnectionSettingsRepository
        self.getPrivateNetworkRoutesUseCase = getPrivateNetworkRoutesUseCase
    }
    
    public func execute(
        onSuspend: @escaping @Sendable (SuspendState) -> Void,
        onComplete: (@Sendable () -> Void)? = nil,
        onAbort: (@Sendable () -> Void)? = nil
    ) async {
        guard !isRunning else {
            assertionFailure("execute() called while already running")
            return
        }
        isRunning = true
        defer { isRunning = false }
        
        // check NIC
        
        let networkRoutes: [LocalNetworkRoute]
        while true {
            let routes = getPrivateNetworkRoutesUseCase()
            if routes.isEmpty {
                switch await yield(.privateNetworkNotConnected, onSuspend: onSuspend) {
                case .confirm:
                    break
                    
                case .abort:
                    onAbort?()
                    return
                    
                case .retry:
                    continue
                }
            }
            networkRoutes = routes
            break
        }
        
        // resolve destination address
        
        await vrcConnectionSettingsRepository.fetch()
        let vrcConnectionSettings = vrcConnectionSettingsRepository.dataConsumer.value
        let destinationAddress: IPAddress
        while true {
            guard let ipAddress = resolveIpAddress(vrcConnectionSettings.sendingIpAddressOrDomain) else {
                switch await yield(.destinationAddressResolveingFailed, onSuspend: onSuspend) {
                case .confirm:
                    onComplete?()
                    return
                    
                case .abort:
                    onAbort?()
                    return
                    
                case .retry:
                    continue
                }
            }
            destinationAddress = ipAddress
            break
        }
        
        // check destination routing
        
        while true {
            let destinationRoute = networkRoutes.first(where: {
                isSameNetwork(destinationAddress, $0.address, prefix: $0.prefix)
            })
            if destinationRoute == nil {
                switch await yield(.globalAddressSpecified, onSuspend: onSuspend) {
                case .confirm:
                    break
                    
                case .abort:
                    onAbort?()
                    return
                    
                case .retry:
                    continue
                }
            }
            break
        }
        
        // test UDP connection
        
        while true {
            let endpoint = NWEndpoint.hostPort(
                host: NWEndpoint.Host(ipAddress: destinationAddress),
                port: NWEndpoint.Port(rawValue: UInt16(vrcConnectionSettings.sendingPort.value))!
            )
            let params = NWParameters.udp
            params.includePeerToPeer = true
            params.allowLocalEndpointReuse = true
            let connection = NWConnection(to: endpoint, using: params)
            
            let networkErrorState: NetworkErrorState? = await withTaskCancellationHandler {
                await withTaskGroup(of: NetworkErrorState?.self) { group in
                    group.addTask {
                        let stream = AsyncStream<NetworkErrorState?> { continuation in
                            connection.stateUpdateHandler = { state in
                                switch state {
                                case .ready, .cancelled:
                                    debugPrint("")
                                    continuation.yield(nil)
                                    connection.cancel()
                                    continuation.finish()
                                    
                                case .waiting(_), .failed(_):
                                    if case .localNetworkDenied = connection.currentPath?.unsatisfiedReason {
                                        continuation.yield(.localNetworkDenied)
                                    } else {
                                        continuation.yield(.unknownNetworkError)
                                    }
                                    connection.cancel()
                                    continuation.finish()
                                    
                                default:
                                    break
                                }
                            }
                            let queue = DispatchQueue(label: "network.check")
                            connection.start(queue: queue)
                            continuation.onTermination = { _ in
                                connection.cancel()
                            }
                        }
                        return await stream.first(where: { _ in true}) ?? nil
                    }
                    
                    group.addTask {
                        do {
                            try await Task.sleep(nanoseconds: 10 * 1_000_000_000)
                            print("checking network connection is timeout")
                        } catch {
                        }
                        return nil
                    }
                    
                    defer { group.cancelAll() }
                    
                    return await group.next() ?? nil
                }
            } onCancel: {
                connection.cancel()
            }
            
            switch networkErrorState {
            case .localNetworkDenied:
                switch await yield(.localNetworkDenied, onSuspend: onSuspend) {
                case .confirm:
                    break
                    
                case .abort:
                    onAbort?()
                    return
                    
                case .retry:
                    continue
                }
                
            case .unknownNetworkError:
                switch await yield(.unknownNetworkError, onSuspend: onSuspend) {
                case .confirm:
                    break
                    
                case .abort:
                    onAbort?()
                    return
                    
                case .retry:
                    continue
                }
            default:
                break
            }
            break
        }
        
        // network health checking is completed
        
        onComplete?()
    }
    
    private func yield(_ suspendState: SuspendState, onSuspend: ((SuspendState) -> Void)?) async -> ResumeState {
        return await withTaskCancellationHandler {
            return await withCheckedContinuation { continuation in
                isSuspended = true
                self.continuation = continuation
                onSuspend?(suspendState)
            }
        } onCancel: {
            Task { [weak self] in
                await self?.resume(.abort)
            }
        }
    }
    
    public func resume(_ resumeState: ResumeState) {
        if !isSuspended { return }
        isSuspended = false
        continuation?.resume(returning: resumeState)
        continuation = nil
    }
}
