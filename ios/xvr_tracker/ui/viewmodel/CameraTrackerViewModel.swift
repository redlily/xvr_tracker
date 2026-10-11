import SwiftUI
import Network
import domain
import Combine
import AVFoundation
import UIKit
import infra
import common
import data
import di

@MainActor
final class CameraTrackerViewModel: ObservableObject {
    @Published private(set) var uiState: CameraTrackerUiState
    @Published private(set) var cameraState: CameraState
    @Published var replacementImage: UIImage? = nil
    
    private let vrcConnectionSettingsRepository: VrcConnectionSettingsRepository
    private let poseLandmarkerSettingsRepository: PoseLandmarkerSettingsRepository
    private let cameraSettingsRepository: CameraSettingsRepository
    private let cameraController: CameraController
    private let imageTrackerService: ImageTrackerService
    private let analyzedDataPlotServerSettingsRepository: AnalyzedDataPlotServerSettingsRepository
    private let plotAnalyzedDataServerService: PlotAnalyzedDataServerService
    private let trackingDataNetworkHealthCheckWorkflow: TrackingDataNetworkHealthCheckWorkflow
    
    private var isActive = false
    
    private var networkHealthCheckingTask: Task<Void, Never>? = nil
    private let taskBag = TaskBag()
    
    private let isReplacementImageEnabled = false
    
    init(
        vrcConnectionSettingsRepository: VrcConnectionSettingsRepository = AppContainer.shared.resolve(),
        poseLandmarkerSettingsRepository: PoseLandmarkerSettingsRepository = AppContainer.shared.resolve(),
        cameraSettingsRepository: CameraSettingsRepository = AppContainer.shared.resolve(),
        cameraController: CameraController = AppContainer.shared.resolve(),
        imageTrackerService: ImageTrackerService = AppContainer.shared.resolve(),
        analyzedDataPlotServerSettingsRepository: AnalyzedDataPlotServerSettingsRepository = AppContainer.shared.resolve(),
        plotAnalyzedDataServerService: PlotAnalyzedDataServerService = AppContainer.shared.resolve(),
        trackingDataNetworkHealthCheckWorkflow: TrackingDataNetworkHealthCheckWorkflow = AppContainer.shared.resolve()
    ) {
        self.vrcConnectionSettingsRepository = vrcConnectionSettingsRepository
        self.cameraSettingsRepository = cameraSettingsRepository
        self.cameraController = cameraController
        self.poseLandmarkerSettingsRepository = poseLandmarkerSettingsRepository
        self.imageTrackerService = imageTrackerService
        self.analyzedDataPlotServerSettingsRepository = analyzedDataPlotServerSettingsRepository
        self.plotAnalyzedDataServerService = plotAnalyzedDataServerService
        self.trackingDataNetworkHealthCheckWorkflow = trackingDataNetworkHealthCheckWorkflow
        
        if isReplacementImageEnabled {
            replacementImage = UIImage(named: "tracking_sample")
        } else {
            replacementImage = nil
        }
        
        uiState = CameraTrackerUiState()
        cameraState = cameraController.stateComitter.value
        
        Task { [weak self] in
            for await settings in cameraSettingsRepository.dataConsumer.stream() {
                guard let self else { return }
                uiState.lensFacingType = settings.lensFacingType
                uiState.zoomRatio = settings.zoomRatio
            }
            print("Unsubscribe cameraSettingsRepository.dataConsumer")
        }.store(in: taskBag)
        
        Task { [weak self] in
            for await state in cameraController.stateComitter.stream(
                bufferingPolicy: .bufferingNewest(1)
            ) {
                self?.cameraState = state
            }
            print("Unsubscribe cameraController.stateComitter")
        }.store(in: taskBag)
        
        if let replacementImage {
            Task.detached(priority: .userInitiated) { [weak self] in
                guard let self else { return }
                while !Task.isCancelled {
                    if let pixelBuffer = replacementImage.toCVImageBuffer() {
                        self.imageTrackerService.updateAnalysisImage(
                            image: pixelBuffer,
                            timestamp: CACurrentMediaTime()
                        )
                    }
                    try? await Task.sleep(nanoseconds: 1_000_000_000)
                }
                print("Unsubscribe cameraController.analysisImageComitter")
            }.store(in: taskBag)
        } else {
            Task.detached(priority: .userInitiated) { [weak self] in
                for await state in cameraController.analysisImageComitter.stream(
                    bufferingPolicy: .bufferingNewest(1)
                ) {
                    self?.imageTrackerService.updateAnalysisImage(
                        image: state.image,
                        timestamp: state.timestamp
                    )
                }
                print("Unsubscribe cameraController.analysisImageComitter")
            }.store(in: taskBag)
        }
        
        Task { [weak self] in
            for await value in imageTrackerService.poseLandmarksDataConsumer.stream(
                bufferingPolicy: .bufferingNewest(1)
            ) {
                guard let self else { return }
                if case .cameraActive(var content) = self.uiState.contentState {
                    content.poseLandmarksData = value
                    self.uiState.contentState = .cameraActive(content: content)
                }
            }
            print("Unsubscribe imageTrackerService.poseLandmarksDataConsumer")
        }.store(in: taskBag)
        
        Task { [weak self] in
            for await value in imageTrackerService.poseTrackersDataConsumer.stream(
                bufferingPolicy: .bufferingNewest(1)
            ) {
                guard let self else { return }
                if case .cameraActive(var content) = self.uiState.contentState {
                    content.poseTrackersData = value
                    self.uiState.contentState = .cameraActive(content: content)
                }
            }
            print("Unsubscribe imageTrackerService.poseTrackersDataConsumer")
        }.store(in: taskBag)
        
        Task.detached(priority: .utility) {
            for await value in imageTrackerService.poseLandmarksDataConsumer.stream(
                bufferingPolicy: .bufferingNewest(1)
            ) {
                await plotAnalyzedDataServerService.updatePoseLandmarksData(value)
            }
            print("Unsubscribe imageTrackerService.poseLandmarksDataConsumer")
        }.store(in: taskBag)
        
        Task.detached(priority: .utility) {
            for await value in imageTrackerService.poseTrackersDataConsumer.stream(
                bufferingPolicy: .bufferingNewest(1)
            ) {
                await plotAnalyzedDataServerService.updatePoseTrackersData(value)
            }
            print("Unsubscribe imageTrackerService.poseLandmarksDataConsumer")
        }.store(in: taskBag)
    }
    
    func onStart() {
        imageTrackerService.resume()
    }
    
    func onStop() {
        isActive = false
        networkHealthCheckingTask?.cancel()
        networkHealthCheckingTask = nil
        cameraController.unbind()
        cameraController.stop()
        imageTrackerService.suspend()
        Task {
            await plotAnalyzedDataServerService.stop()
        }
    }
    
    func onActive() {
        isActive = true
        Task {
            let isRunning = await trackingDataNetworkHealthCheckWorkflow.isRunning
            let isSuspended = await trackingDataNetworkHealthCheckWorkflow.isSuspended
            if (isRunning && isSuspended) {
                await trackingDataNetworkHealthCheckWorkflow.resume(.retry)
            }
        }
    }
    
    func onInactive() {
        isActive = false
        switch uiState.dialogState {
        case .cameraPermissionDenied,
                .privateNetworkNotConnected,
                .destinationAddressResolutionFailed,
                .globalAddressSpecified,
                .localNetworkDenied,
                .unknownNetworkError:
            uiState.dialogState = nil
        default:
            break
        }
    }
    
    func onCameraPermissionGranted() {
        cameraController.start()
        cameraController.bind(lensFacingType: cameraSettingsRepository.dataConsumer.value.lensFacingType)
        uiState.contentState = .cameraActive(content: CameraActiveContent())
        onNetworkHealthCheck()
    }
    
    func onCameraPermissionRejected() {
        uiState.contentState = .cameraPermissionDenied
        uiState.dialogState = .cameraPermissionDenied
    }
    
    private func onNetworkHealthCheck() {
        Task.detached { [weak self] in
            await self?.trackingDataNetworkHealthCheckWorkflow.execute(
                onSuspend: { [weak self] state in
                    switch state {
                    case .privateNetworkNotConnected:
                        Task { await self?.onPrivateNetworkNotConnected() }
                    case .destinationAddressResolveingFailed:
                        Task { await self?.onDestinationAddressResolutionFailed() }
                    case .globalAddressSpecified:
                        Task { await self?.onGlobalAddressSpecified() }
                    case .localNetworkDenied:
                        Task { await self?.onLocalNetworkDenied() }
                    case .unknownNetworkError:
                        Task { await self?.onUnknownNetworkError() }
                    }
                },
                onComplete: { [weak self] in
                    Task { await self?.onNetworkReady() }
                },
                onAbort: { [weak self] in
                    Task { await self?.onNetworkReady() }
                }
            )
        }
    }
    
    private func onNetworkReady() {
        Task {
            imageTrackerService.startNetwork()
            if analyzedDataPlotServerSettingsRepository.dataConsumer.value.isEnabled {
                await plotAnalyzedDataServerService.start()
            }
        }
    }
    
    private func onPrivateNetworkNotConnected() {
        if isActive {
            uiState.dialogState = .privateNetworkNotConnected
        }
    }
    
    private func onDestinationAddressResolutionFailed() {
        if isActive {
            uiState.dialogState = .destinationAddressResolutionFailed
        }
    }
    
    private func onGlobalAddressSpecified() {
        if isActive {
            uiState.dialogState = .globalAddressSpecified
        }
    }
    
    private func onLocalNetworkDenied() {
        if isActive {
            uiState.dialogState = .localNetworkDenied
        }
    }
    
    private func onUnknownNetworkError() {
        if isActive {
            uiState.dialogState = .unknownNetworkError
        }
    }
    
    func onCameraSwitch() {
        let currentLensFacingType = cameraSettingsRepository.dataConsumer.value.lensFacingType
        let newLensFacingType: CameraLensFacingType = currentLensFacingType == .back ? .front : .back
        _ = cameraSettingsRepository.setLensFacingType(newLensFacingType)
    }
    
    func onZoomChange(_ value: Double) {
        cameraController.updateZoomRatio(zoomRatio: value)
    }
    
    func onDialogClose() {
        switch uiState.dialogState {
        case .privateNetworkNotConnected,
                .destinationAddressResolutionFailed,
                .globalAddressSpecified,
                .localNetworkDenied,
                .unknownNetworkError:
            Task { await trackingDataNetworkHealthCheckWorkflow.resume(.confirm) }
        default:
            break
        }
        uiState.dialogState = nil
    }
}
