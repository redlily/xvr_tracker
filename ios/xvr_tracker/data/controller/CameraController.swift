import Foundation
import AVFoundation
import common

public class CameraController: NSObject, @unchecked Sendable {
    public struct AnalysisImageState: @unchecked Sendable {
        public var image: CVImageBuffer
        public var timestamp: CFTimeInterval
        public var fps: Float
    }
    
    private let displayRotationDataSource: DisplayRotationDataSource
    
    private var isStart = false
    private var isBindingRequired = false
    private var latestTimestamp: CFTimeInterval = 0
    
    private var device: AVCaptureDevice? = nil
    private var deviceInput: AVCaptureDeviceInput? = nil
    private var videoDataOutput: AVCaptureVideoDataOutput? = nil
    
    private var displayRotationObserverTask: Task<Void, Never>? = nil
    private let taskBag = TaskBag()
    private let lock = AsyncMutex()
    
    private let stateEmitter = StateEmitter(CameraState())
    public var stateComitter: StateConsumer<CameraState> {
        stateEmitter.eraseToStateConsumer()
    }
    
    private let analysisImageEmitter = SharedEmitter<AnalysisImageState>()
    public var analysisImageComitter: SharedConsumer<AnalysisImageState> {
        analysisImageEmitter.eraseToSharedConsumer()
    }
    
    public init(
        displayRotationDataSource: DisplayRotationDataSource
    ) {
        self.displayRotationDataSource = displayRotationDataSource
        super.init()
    }
    
    private func internalBind(lensFacingType: CameraLensFacingType) {
        Task {
            await internalUnbind()
            try await lock.withLock {
                let types: [AVCaptureDevice.DeviceType] = [
                    .builtInTripleCamera,
                    .builtInDualWideCamera,
                    .builtInUltraWideCamera,
                    .builtInWideAngleCamera,
                ]
                let sesstions = AVCaptureDevice.DiscoverySession(
                    deviceTypes: types,
                    mediaType: .video,
                    position: .back
                )
                
                guard let device = sesstions.devices.first else {
                    print("Failed to get camera device")
                    return
                }
                
                let session = AVCaptureSession()
                
//                let devicePosition: AVCaptureDevice.Position = lensFacingType == .back ? .back : .front
//                guard let device = AVCaptureDevice.default(
//                    .continuityCamera,
//                    for: .video,
//                    position: devicePosition)
//                else {
//                    print("Failed to get camera device")
//                    return
//                }
                
                self.device = device
                
                do {
                    let input = try AVCaptureDeviceInput(device: device)
                    self.deviceInput = input
                    
                    if session.canAddInput(input) {
                        session.addInput(input)
                    }
                    
                    let videoOutput = AVCaptureVideoDataOutput()
                    videoOutput.setSampleBufferDelegate(self, queue: DispatchQueue.global(qos: .userInitiated))
                    videoOutput.videoSettings = [
                        kCVPixelBufferPixelFormatTypeKey as String: kCVPixelFormatType_32BGRA
                    ]
                    
                    if session.canAddOutput(videoOutput) {
                        session.addOutput(videoOutput)
                        self.videoDataOutput = videoOutput
                    }
                    session.startRunning()
                    
                    self.device = device
                    self.deviceInput = deviceInput
                    self.videoDataOutput = videoOutput
                    if let connection = videoDataOutput?.connection(with: .video),
                       connection.isVideoRotationAngleSupported(90) {
                        connection.videoRotationAngle = 90
                    }
                    
                    displayRotationObserverTask = Task { [weak self] in
                        guard let stream = self?.displayRotationDataSource
                            .rotationConsumer
                            .stream(bufferingPolicy: .bufferingNewest(1)) else { return }
                        for await value in stream {
                            guard let self else { return }
                            stateEmitter.update {
                                $0.orientation = value
                            }
                        }
                    }
                    
                    let minZoom = Double(device.minAvailableVideoZoomFactor)
                    let maxZoom = Double(device.maxAvailableVideoZoomFactor)
                    let zoom = minZoom

                    try device.withLockForConfiguration { device in
                        let format = device.formats
                            .filter { $0.formatDescription.dimensions.width <= 640 }
                            .max { $0.formatDescription.dimensions.width < $1.formatDescription.dimensions.width } ??
                        device.formats.min { $0.formatDescription.dimensions.width < $1.formatDescription.dimensions.width }
                        device.activeFormat = format!
                        device.videoZoomFactor = CGFloat(zoom)
                    }

                    stateEmitter.update {[
                        device = UncheckedSendableBox(device),
                        session = UncheckedSendableBox(session)
                    ] in
                        $0.zoomRatio = zoom
                        $0.zoomRatioRange = minZoom...maxZoom
                        $0.orientation = displayRotationDataSource.latestValue
                        $0.isBound = true
                        $0.device = device.value
                        $0.session = session.value
                    }
                } catch {
                    print("Failed to bind camera")
                }
            }
        }.store(in: taskBag)
    }
    
    private func internalUnbind() async {
        try? await lock.withLock {
            device = nil
            deviceInput = nil
            videoDataOutput = nil
            displayRotationObserverTask?.cancel()
            displayRotationObserverTask = nil
            stateEmitter.update {
                if let session = $0.session {
                    session.stopRunning()
                    if let deviceInput = deviceInput {
                        session.removeInput(deviceInput)
                    }
                    if let videoDataOutput = videoDataOutput {
                        session.removeOutput(videoDataOutput)
                    }
                    $0.session = nil
                }
                $0.isBound = false
            }
        }
    }
    
    public func start() {
        isStart = true
        if isBindingRequired {
            internalBind(lensFacingType: stateEmitter.value.lensFacingType)
        }
    }
    
    public func stop() {
        isStart = false
        Task {
            await internalUnbind()
        }.store(in: taskBag)
    }
    
    public func bind(lensFacingType: CameraLensFacingType) {
        isBindingRequired = true
        stateEmitter.update {
            $0.lensFacingType = lensFacingType
        }
        if (isStart) {
            internalBind(lensFacingType: lensFacingType)
        }
    }
    
    public func unbind() {
        isBindingRequired = false
        Task {
            await internalUnbind()
        }.store(in: taskBag)
    }
    
    public func updateZoomRatio(zoomRatio: Double) {
        Task {
            try device?.withLockForConfiguration { device in
                let state = stateEmitter.update { state in
                    let clampedZoomRatio = max(
                        state.zoomRatioRange.lowerBound,
                        min(
                            state.zoomRatioRange.upperBound,
                            zoomRatio
                        )
                    )
                    state.zoomRatio = clampedZoomRatio
                }
                device.videoZoomFactor = CGFloat(state.zoomRatio)
            }
        }.store(in: taskBag)
    }
}

extension CameraController: AVCaptureVideoDataOutputSampleBufferDelegate {
    public func captureOutput(
        _ output: AVCaptureOutput,
        didOutput sampleBuffer: CMSampleBuffer,
        from connection: AVCaptureConnection
    ) {
        let currentTimestamp = CACurrentMediaTime()
        if latestTimestamp >= currentTimestamp { return }
        let fps = 1.0 / (currentTimestamp - latestTimestamp)
        latestTimestamp = currentTimestamp
        
        guard let pixelBuffer = CMSampleBufferGetImageBuffer(sampleBuffer) else { return }
        
        analysisImageEmitter.emit(AnalysisImageState(
            image: pixelBuffer,
            timestamp: currentTimestamp,
            fps: Float(fps)
        ))
    }
}
