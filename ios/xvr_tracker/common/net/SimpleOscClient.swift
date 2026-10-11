import Foundation
import Network

public final class SimpleOscClient: @unchecked Sendable {
    public enum State: Sendable {
        case setup
        case teardown
        case open
        case ready
        case waiting(error: NWError?)
        case retry(error: NWError?)
        case close(error: NWError?)
    }
    
    public static let MAX_RETRY_COUNT = 5
    public static let RETRY_INTERVAL_MS = 2_000
    public static let TIMEOUT_MS = 10_000
    
    private var interface: NWInterface?
    private var host: NWEndpoint.Host!
    private var port: NWEndpoint.Port!
    
    private var connectionId: UUID? = nil
    private var connection: NWConnection? = nil
    
    private var latestError: NWError? = nil
    private var retryCount: Int = 0
    
    private var connectionTimeoutTask: Task<Void, Never>? = nil
    private var connectionRetryTask: Task<Void, Never>? = nil
    
    private let mutex = AsyncMutex()
    
    private let stateEmitter = StateEmitter<State>(.teardown)
    public var stateConsumer: StateConsumer<State> { stateEmitter.eraseToStateConsumer() }
    
    public init() {}
    
    public func setup(
        interface: NWInterface?,
        host: NWEndpoint.Host,
        port: NWEndpoint.Port
    ) async {
        debugLog(
            "OSC connection setup: " +
            "\(interface?.debugDescription ?? "nil") " +
            "\(host.debugDescription) " +
            "\(port.debugDescription)"
        )
        try? await mutex.withLock {
            if case .teardown = stateConsumer.value {} else {
                internalTearDown()
            }
            self.interface = interface
            self.host = host
            self.port = port
            stateEmitter.emit(.setup)
            openConnection()
        }
    }
    
    public func teardown() async {
        debugLog("OSC connection teardown")
        try? await mutex.withLock {
            internalTearDown()
            stateEmitter.emit(.teardown)
        }
    }
    
    private func internalTearDown() {
        assert(mutex.isLocked)
        guard let connectionId = self.connectionId else { return }
        closeConnection(connectionId: connectionId)
        latestError = nil
        retryCount = 0
    }
    
    private func openConnection() {
        assert(mutex.isLocked)
        switch stateConsumer.value {
        case .setup, .close:
            break
        default:
            return
        }
        
        debugLog("OSC connection open")
        let endpoint = NWEndpoint.hostPort(host: host, port: port)
        let params: NWParameters = .udp
        params.requiredInterface = interface
        let connectionId = UUID()
        let connection = NWConnection(to: endpoint, using: params)
        connection.stateUpdateHandler = { [weak self, connectionId] state in
            Task {
                do {
                    try await self?.mutex.withLock {
                        self?.handleConnectionState(
                            state: state,
                            connectionId: connectionId
                        )
                    }
                } catch {
                    self?.debugLog("OSC connection observer cancelled")
                    connection.cancel()
                }
            }
        }
        connection.start(queue: .global(qos: .userInitiated))
        self.connectionId = connectionId
        self.connection = connection
        stateEmitter.emit(.open)
    }
    
    private func handleConnectionState(
        state: NWConnection.State,
        connectionId: UUID
    ) {
        if self.connectionId != connectionId { return }
        switch state {
        case .setup:
            print("OSC connection handle state to setup")
            setConnectionTimeout(connectionId: connectionId)
        case .ready:
            print("OSC connection handle state to ready")
            setConnectionReady(connectionId: connectionId)
        case .waiting(let error):
            print("OSC connection handle state to state waiting: \(error)")
            setConnectionTimeout(
                connectionId: connectionId,
                error: error
            )
        case .preparing:
            print("OSC connection handle state preparing")
        case .failed(let error):
            print("OSC connection handle state failed: \(error)")
            setConnectionRetry(
                connectionId: connectionId,
                error: error
            )
        case .cancelled:
            print("OSC connection handle state cancelled")
            closeConnection(connectionId: connectionId)
        default:
            break
        }
    }
    
    private func closeConnection(
        connectionId: UUID,
        error: NWError? = nil,
        isRetry: Bool = false
    ) {
        assert(mutex.isLocked)
        guard let connection = connection,
              self.connectionId == connectionId
        else { return }
        
        debugLog("OSC connection close")
        clearConnectionTimeout(connectionId: connectionId)
        if !isRetry {
            clearConnectionRetry()
        }
        connection.cancel()
        self.connectionId = nil
        self.connection = nil
        if isRetry {
            stateEmitter.emit(.retry(error: error))
        } else {
            stateEmitter.emit(.close(error: error))
        }
    }
    
    private func setConnectionReady(connectionId: UUID) {
        assert(mutex.isLocked)
        clearConnectionTimeout(connectionId: connectionId)
        if self.connectionId != connectionId { return }
        
        debugLog("OSC connection ready")
        latestError = nil
        retryCount = 0
        stateEmitter.emit(.ready)
    }
    
    private func setConnectionTimeout(
        connectionId: UUID,
        error: NWError? = nil
    ) {
        assert(mutex.isLocked)
        if self.connectionId != connectionId { return }
        
        debugLog("OSC connection set timeout \(String(describing: error))")
        latestError = error
        if connectionTimeoutTask == nil {
            connectionTimeoutTask = Task {
                do {
                    try await Task.sleep(nanoseconds: UInt64(1_000_000 * SimpleOscClient.TIMEOUT_MS))
                    try await mutex.withLock {
                        debugLog("OSC connection timeout")
                        clearConnectionTimeout(connectionId: connectionId)
                        setConnectionRetry(
                            connectionId: connectionId,
                            error: latestError
                        )
                    }
                } catch {
                    debugLog("OSC connection timeout cancelled")
                }
            }
        }
        stateEmitter.emit(.waiting(error: error))
    }
    
    private func clearConnectionTimeout(connectionId: UUID) {
        assert(mutex.isLocked)
        guard self.connectionId == connectionId,
              let connectionTimeoutTask = connectionTimeoutTask
        else { return }
        
        debugLog("OSC connection clear timeout")
        connectionTimeoutTask.cancel()
        self.connectionTimeoutTask = nil
    }
    
    private func setConnectionRetry(
        connectionId: UUID,
        error: NWError? = nil
    ) {
        assert(mutex.isLocked)
        if self.connectionId != connectionId || connectionRetryTask != nil { return }
        
        debugLog("OSC connection set retry \(String(describing: error))")
        latestError = error
        let retry = retryCount < SimpleOscClient.MAX_RETRY_COUNT
        closeConnection(
            connectionId: connectionId,
            error: error,
            isRetry: retry
        )
        if retry {
            retryCount += 1
            connectionRetryTask = Task {
                do {
                    try await Task.sleep(nanoseconds: UInt64(1_000_000 * SimpleOscClient.RETRY_INTERVAL_MS))
                    try await mutex.withLock {
                        clearConnectionRetry()
                        debugLog("OSC connection retry")
                        openConnection()
                    }
                } catch {
                    debugLog("OSC connection retry cancelled")
                }
            }
        }
    }
    
    private func clearConnectionRetry() {
        assert(mutex.isLocked)
        guard let connectionRetryTask = connectionRetryTask
        else { return }
        
        debugLog("OSC connection clear retry")
        connectionRetryTask.cancel()
        self.connectionRetryTask = nil
    }
    
    public func send(path: String, params: [Float]) {
        mutex.withTryLock {
            guard let connection = connection,
                  case .ready = stateConsumer.value
            else {
                return
            }
            
            let packetData = createOscPacket(path: path, params: params)
            connection.send(content: packetData, completion: .contentProcessed { error in
                if let error = error {
                    print("OSC send failed: \(error)")
                }
            })
        }
    }
    
    private func oscPadString(_ str: String) -> Data {
        let bytes = str.data(using: .utf8) ?? Data()
        let length = bytes.count + 1
        let padLength = (4 - (length % 4)) % 4
        
        var padded = Data(bytes)
        padded.append(0)
        
        for _ in 0..<padLength {
            padded.append(0)
        }
        
        return padded
    }
    
    private func createOscPacket(path: String, params: [Float]) -> Data {
        let addressBytes = oscPadString(path)
        let typeTag = "," + String(repeating: "f", count: params.count)
        let typeTagBytes = oscPadString(typeTag)
        
        var dataBuffer = Data()
        for param in params {
            var floatValue = param.bitPattern.bigEndian
            withUnsafeBytes(of: &floatValue) {
                dataBuffer.append(contentsOf: $0)
            }
        }
        
        var packet = Data()
        packet.append(addressBytes)
        packet.append(typeTagBytes)
        packet.append(dataBuffer)
        
        return packet
    }

#if DEBUG
    private static let isLoggingEnabled = false
#endif
    
    private func debugLog(_ items: Any...) {
#if DEBUG
        if SimpleOscClient.isLoggingEnabled {
            print(items)
        }
#endif
    }
}
