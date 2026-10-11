import Foundation
import UIKit
import CoreVideo
import CoreImage

public extension UIImage {
    func toCVImageBuffer() -> CVImageBuffer? {
        guard let ciImage = CIImage(image: self) else { return nil }
        let context = CIContext()
        var pixelBuffer: CVPixelBuffer?
        let attrs = [
            kCVPixelBufferCGImageCompatibilityKey: kCFBooleanTrue!,
            kCVPixelBufferCGBitmapContextCompatibilityKey: kCFBooleanTrue!
        ] as CFDictionary
        let width = Int(size.width)
        let height = Int(size.height)
        let status = CVPixelBufferCreate(
            kCFAllocatorDefault,
            width,
            height,
            kCVPixelFormatType_32BGRA,
            attrs,
            &pixelBuffer
        )
        guard status == kCVReturnSuccess, let buffer = pixelBuffer else { return nil }
        context.render(ciImage, to: buffer)
        return buffer
    }
}

