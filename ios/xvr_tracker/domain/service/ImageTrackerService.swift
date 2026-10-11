import Foundation
import CoreVideo
import infra
import common
import data
import Swinject

public final class ImageTrackerService: @unchecked Sendable {
    private let poseLandmarkerSettingsRepository: PoseLandmarkerSettingsRepository
    private let accelerationDataSource: AccelerationDataSource
    private let poseLandmarkerDataSource: PoseLandmarkerDataSource
    private let poseLandmarkModifier: PoseLandmarkModifier
    private let poseTrackerCalculator: PoseTrackerCalculator
    private let vrcClientService: VrcClientService
    
    private let rawPoseLandmarksDataEmitter = SharedEmitter<PoseLandmarksData>()
    public var rawPoseLandmarksDataConsumer: SharedConsumer<PoseLandmarksData> {
        rawPoseLandmarksDataEmitter.eraseToSharedConsumer()
    }
    
    private let poseLandmarksDataEmitter = SharedEmitter<PoseLandmarksData>()
    public var poseLandmarksDataConsumer: SharedConsumer<PoseLandmarksData> {
        poseLandmarksDataEmitter.eraseToSharedConsumer()
    }
    
    private let poseTrackersDataEmitter = SharedEmitter<PoseTrackersData>()
    public var poseTrackersDataConsumer: SharedConsumer<PoseTrackersData> {
        poseTrackersDataEmitter.eraseToSharedConsumer()
    }
    
    private var accelerationDataTask: Task<Void, Never>? = nil
    private let taskBag = TaskBag()
    private let mutex = AsyncMutex()
    
    public init(
        poseLandmarkerSettingsRepository: PoseLandmarkerSettingsRepository,
        accelerationDataSource: AccelerationDataSource,
        poseLandmarkerDataSource: PoseLandmarkerDataSource,
        poseLandmarkModifier: PoseLandmarkModifier,
        poseTrackerCalculator: PoseTrackerCalculator,
        vrcClientService: VrcClientService
    ) {
        self.poseLandmarkerSettingsRepository = poseLandmarkerSettingsRepository
        self.accelerationDataSource = accelerationDataSource
        self.poseLandmarkerDataSource = poseLandmarkerDataSource
        self.poseLandmarkModifier = poseLandmarkModifier
        self.poseTrackerCalculator = poseTrackerCalculator
        self.vrcClientService = vrcClientService
        
        Task { [weak self] in
            for await data in poseLandmarkerDataSource.dataConsumer.stream(
                bufferingPolicy: .bufferingNewest(1)
            ) {
                guard let self else { return }
                rawPoseLandmarksDataEmitter.emit(data)
                let modifiedData = await poseLandmarkModifier.modify(data)
                poseLandmarksDataEmitter.emit(modifiedData)
                await poseTrackerCalculator.updateLandmarksData(modifiedData)
            }
        }.store(in: taskBag)
        
        Task {
            for await value in poseLandmarkerDataSource.errorConsumer.stream() {
                print("error \(value)")
            }
        }.store(in: taskBag)
        
        Task { [weak self] in
            for await value in await poseTrackerCalculator.poseTrackersDataConsumer.stream(
                bufferingPolicy: .bufferingNewest(1)
            ) {
                guard let self else { return }
                poseTrackersDataEmitter.emit(value)
                await vrcClientService.sendPoseTrackerData(value)
                
            }
        }.store(in: taskBag)
    }
    
    public func dispose() {
        Task.detached {
            try? await self.mutex.withLock {
                await self.poseLandmarkerDataSource.tearDown()
                self.taskBag.cancel()
            }
        }
    }
    
    public func resume() {
        Task {
            try await mutex.withLock {
                await resumePoseLandmarker()
                await poseLandmarkModifier.reset()
                await poseTrackerCalculator.reset()
                accelerationDataTask = Task {
                    for await acceleration in accelerationDataSource.accelerationConsumer.stream(
                        bufferingPolicy: .bufferingNewest(1)
                    ) {
                        let unityAcceleration = acceleration.sensorToUnity()
                        await poseLandmarkModifier.updateUpperDirection(unityAcceleration)
                        await poseTrackerCalculator.updateUpperDirection(unityAcceleration)
                    }
                }
            }
        }.store(in: taskBag)
    }
    
    private func resumePoseLandmarker() async {
        let settings = poseLandmarkerSettingsRepository.dataConsumer.value
        try? await poseLandmarkerDataSource.setUp(
            modelType: settings.modelType,
            computeType: settings.computeType
        )
    }
    
    public func suspend() {
        Task {
            try await mutex.withLock {
                await poseLandmarkerDataSource.tearDown()
                accelerationDataTask?.cancel()
                accelerationDataTask = nil
                await vrcClientService.termDown()
            }
        }.store(in: taskBag)
    }
    
    public func startNetwork() {
        Task {
            try await mutex.withLock {
                await vrcClientService.setUp()
            }
        }.store(in: taskBag)
    }
    
    public func updateAnalysisImage(
        image: CVImageBuffer,
        timestamp: CFTimeInterval
    ) {
        mutex.withTryLock {
            poseLandmarkerDataSource.detect(
                imageBuffer: image,
                timestamp: timestamp
            )
        }
    }
}
