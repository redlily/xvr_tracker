import Foundation
import CoreVideo
import Combine
import os.lock
import infra
import common

public class PoseLandmarkerDataSource: @unchecked Sendable {
    public static let defaultNumPoses = 1
    public static let defaultPoseDetectionConfidence: Float = 0.5
    public static let defaultPosePresenceConfidence: Float = 0.5
    public static let defaultPoseTrackingConfidence: Float = 0.5
    public static let defaultShouldOutputSegmentationMasks = false
    
    public private(set) var isSetUp = false
    
    private let dataEmitter = SharedEmitter<PoseLandmarksData>()
    public var dataConsumer: SharedConsumer<PoseLandmarksData> { dataEmitter.eraseToSharedConsumer() }
    
    private let errorEmitter = SharedEmitter<Error>()
    public var errorConsumer: SharedConsumer<Error> { errorEmitter.eraseToSharedConsumer()}
    
    private var poseLandmarker: PoseLandmarkerWrapper? = nil
    private var latestTimestamp: CFTimeInterval = 0
    
    private let mutex = AsyncMutex()
    
    public init() {}
    
    public func setUp(
        modelType: PoseLandmarkerModelType,
        computeType: PoseLandmarkerComputeType,
        numPoses: Int = PoseLandmarkerDataSource.defaultNumPoses,
        minPoseDetectionConfidence: Float = PoseLandmarkerDataSource.defaultPoseTrackingConfidence,
        minPosePresenceConfidence: Float = PoseLandmarkerDataSource.defaultPoseDetectionConfidence,
        minPoseTrackingConfidence: Float = PoseLandmarkerDataSource.defaultPosePresenceConfidence,
        shouldOutputSegmentationMasks: Bool = PoseLandmarkerDataSource.defaultShouldOutputSegmentationMasks
    ) async throws {
        try await mutex.withLock {
            internalTearDown()
            
            let poseLandmarker = PoseLandmarkerWrapper()
            poseLandmarker.onResult = { [weak self] data, error in
                if let data {
                    self?.dataEmitter.emit(data)
                } else if let error {
                    self?.errorEmitter.emit(error)
                }
            }
            try poseLandmarker.setUp(
                modelAssetPath: modelType.modelFilePath,
                computeType: computeType,
                numPoses: numPoses,
                minPoseDetectionConfidence: minPoseDetectionConfidence,
                minPosePresenceConfidence: minPosePresenceConfidence,
                minPoseTrackingConfidence: minPoseDetectionConfidence,
                shouldOutputSegmentationMasks: shouldOutputSegmentationMasks
            )
            
            self.poseLandmarker = poseLandmarker
            latestTimestamp = 0
            isSetUp = true
        }
    }
    
    public func tearDown() async {
        Task.detached {
            try? await self.mutex.withLock {
                self.internalTearDown()
            }
        }
    }
    
    private func internalTearDown() {
        poseLandmarker?.termDown()
        poseLandmarker = nil
        isSetUp = false
    }
    
    public func detect(
        imageBuffer: CVImageBuffer,
        timestamp: CFTimeInterval
    ) {
        mutex.withTryLock {
            if timestamp > latestTimestamp, let image = try? PoseLandmarkImage(imageBuffer: imageBuffer)
            {
                do {
                    try poseLandmarker?.detectAsync(
                        image: image,
                        timestampInMilliseconds: Int(timestamp * 1000)
                    )
                    latestTimestamp = timestamp
                } catch {
                    errorEmitter.emit(error)
                }
            }
        }
    }
}
