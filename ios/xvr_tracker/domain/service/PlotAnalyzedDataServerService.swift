import Foundation
import UIKit
import UniformTypeIdentifiers
import Hummingbird
import HummingbirdWebSocket
import ServiceLifecycle
import common
import data
import infra

public actor PlotAnalyzedDataServerService {
    private static let assetPath = "plot_analyzed_data"
    
    private let analyzedDataPlotServerSettingsRepository: AnalyzedDataPlotServerSettingsRepository
    
    private var poseLandmarksDataEmitter = SharedEmitter<PoseLandmarksData>()
    private var poseTrackersDataEmitter = SharedEmitter<PoseTrackersData>()
    
    private(set) var isStarted = false
    
    private var application: Application<Router<BasicWebSocketRequestContext>.Responder>?
    private var serverTask: Task<Void, Error>?
    private let fileIO = FileIO()
    
    public init(
        analyzedDataPlotServerSettingsRepository: AnalyzedDataPlotServerSettingsRepository
    ) {
        self.analyzedDataPlotServerSettingsRepository = analyzedDataPlotServerSettingsRepository
    }
    
    public func start() {
        if isStarted {
            stop()
        }
        
        let router = Router(context: BasicWebSocketRequestContext.self)
        router.get("/:filename") { request, context in
            let filename = try context.parameters.require("filename")
            return try await self.handleStaticFile(filePath: filename, context: context)
        }
        router.get("/") { request, context in
            return try await self.handleStaticFile(filePath: "index.html", context: context)
        }
        
        let wsRouter = Router(context: BasicWebSocketRequestContext.self)
        wsRouter.ws("/data") { request, context  in
            return .upgrade([:])
        } onUpgrade: { inbound, outbound, context in
            let taskBag = TaskBag()
            
            Task { [weak self] in
                guard let self else { return }
                for await value in await poseLandmarksDataEmitter.stream(bufferingPolicy: .bufferingNewest(1)) {
                    let json = ChannelledPoseLandmarksData(data: value)
                    if let text = json.jsonString() {
                        try await outbound.write(.text(text))
                    }
                }
            }.store(in: taskBag)
            
            Task { [weak self] in
                guard let self else { return }
                for await value in await poseTrackersDataEmitter.stream(bufferingPolicy: .bufferingNewest(1)) {
                    let json = ChannelledPoseTrackersData(data: value)
                    if let text = json.jsonString() {
                        try await outbound.write(.text(text))
                    }
                }
            }.store(in: taskBag)
            
            for try await frame in inbound {
                if frame.opcode == .text {
                    let text = String(buffer: frame.data)
                    print("message: \(text)")
                }
            }
            
            taskBag.cancel()
        }
        
        let settings = analyzedDataPlotServerSettingsRepository.dataConsumer.value
        let app = Application(
            router: router,
            server: .http1WebSocketUpgrade(webSocketRouter: wsRouter),
            configuration: .init(address: .hostname("0.0.0.0", port: settings.port.value))
        )
        self.application = app
        
        serverTask = Task {
            do {
                print("Start server")
                try await app.runService()
                print("Stop server")
            } catch {
                print("Failed to start server: \(error)")
            }
        }
        isStarted = true
    }
    
    public func stop() {
        serverTask?.cancel()
        serverTask = nil
        isStarted = false
        application = nil
    }
    
    private func handleStaticFile(filePath: String, context: BasicWebSocketRequestContext) async throws -> Response {
        guard let path = Bundle.main.path(
            forResource: filePath,
            ofType: nil,
            inDirectory: Self.assetPath
        ) else {
            throw HTTPError(.notFound)
        }
        
        var headers = HTTPFields()
        headers[.contentType] = contentType(filePath: filePath)
        
        let body = try await fileIO.loadFile(
            path: path,
            context: context
        )
        
        return Response(
            status: .ok,
            headers: headers,
            body: body
        )
    }
    
    private func contentType(filePath: String) -> String {
        switch URL(fileURLWithPath: filePath).pathExtension {
        case "html":
            return "text/html"
        case "css":
            return "text/css"
        case "js":
            return "text/javascript"
        default:
            return "application/octet-stream"
        }
    }
    
    public func updatePoseLandmarksData(_ data: PoseLandmarksData) {
        if isStarted {
            poseLandmarksDataEmitter.emit(data)
        }
    }
    
    public func updatePoseTrackersData(_ data: PoseTrackersData) {
        if isStarted {
            poseTrackersDataEmitter.emit(data)
        }
    }
}

private enum ChannelProperty: String, Encodable, CodingKey {
    case channel
    case data
}

private enum ChannelName: String, Encodable {
    case poseLandmarksData = "pose_landmarks_data"
    case poseTrackersData = "pose_trackers_data"
}

private struct ChannelledPoseLandmarksData: Encodable {
    let data: PoseLandmarksData
    
    func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: ChannelProperty.self)
        try container.encode(ChannelName.poseLandmarksData, forKey: .channel)
        try container.encode(data, forKey: .data)
    }
}

private struct ChannelledPoseTrackersData: Encodable {
    let data: PoseTrackersData
    
    func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: ChannelProperty.self)
        try container.encode(ChannelName.poseTrackersData, forKey: .channel)
        try container.encode(data, forKey: .data)
    }
}

private extension Encodable {
    func jsonString() -> String? {
        let encoder = JSONEncoder()
        guard let jsonData = try? encoder.encode(self),
              let jsonString = String(data: jsonData, encoding: .utf8) else {
            return nil
        }
        return jsonString
    }
}
