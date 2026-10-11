import Foundation
import AVFoundation
import core_type
@_implementationOnly internal import MediaPipeTasksVision

public struct PoseLandmarkImage {
    var image: MPImage
    
    public init(imageBuffer: CVImageBuffer) throws {
        image = try MPImage(pixelBuffer: imageBuffer)
    }
    
    public init(sampleBuffer: CMSampleBuffer) throws {
        image = try MPImage(sampleBuffer: sampleBuffer)
    }
}

extension PoseLandmarkImage {
    var size: Size2D {
        Size2D(
            width: Double(image.width),
            height: Double(image.height)
        )
    }
}
