import Foundation
import AVFoundation
import core_type
@_implementationOnly internal import MediaPipeTasksVision

public final class PoseLandmarkerWrapper {
    private class Processor: NSObject, PoseLandmarkerLiveStreamDelegate {
        weak var source: PoseLandmarkerWrapper? = nil
        
        init(_ source: PoseLandmarkerWrapper? = nil) {
            super.init()
            self.source = source
        }
        
        func poseLandmarker(
            _ poseLandmarker: PoseLandmarker,
            didFinishDetection result: PoseLandmarkerResult?,
            timestampInMilliseconds: Int,
            error: (any Error)?
        ) {
            if let source, let onResult = source.onResult {
                if let result {
                    onResult(
                        PoseLandmarksData(
                            imageSize: source.latestImageSize,
                            landmarks: result.toPoseLandmarksDataList(),
                            timestamp: timestampInMilliseconds
                        ),
                        nil
                    )
                } else if let error {
                    onResult(nil, error)
                }
            }
        }
    }
    
    private var poseLandmarker: PoseLandmarker? = nil
    private var processor: Processor? = nil
    
    private var latestImageSize: Size2D = .zero
    
    public var onResult: ((PoseLandmarksData?, (any Error)?) -> Void)? = nil
    
    public init() {}
    
    public func setUp(
        modelAssetPath: String,
        computeType: PoseLandmarkerComputeType,
        numPoses: Int,
        minPoseDetectionConfidence: Float,
        minPosePresenceConfidence: Float,
        minPoseTrackingConfidence: Float,
        shouldOutputSegmentationMasks: Bool
    ) throws {
        let option = PoseLandmarkerOptions()
        option.baseOptions.modelAssetPath = modelAssetPath
        option.baseOptions.delegate = computeType.nativeType
        
        option.runningMode = .liveStream
        option.numPoses = numPoses
        option.minPoseDetectionConfidence = minPoseDetectionConfidence
        option.minPosePresenceConfidence = minPosePresenceConfidence
        option.minTrackingConfidence = minPoseTrackingConfidence
        option.shouldOutputSegmentationMasks = shouldOutputSegmentationMasks
        
        let processor = Processor(self)
        option.poseLandmarkerLiveStreamDelegate = processor
        
        poseLandmarker = try PoseLandmarker(options: option)
        self.processor = processor
    }
    
    public func termDown() {
        poseLandmarker = nil
        processor = nil
    }
    
    public func detectAsync(
        image: PoseLandmarkImage,
        timestampInMilliseconds: Int
    ) throws {
        latestImageSize = image.size
        try poseLandmarker?.detectAsync(
            image: image.image,
            timestampInMilliseconds: timestampInMilliseconds
        )
    }
}
