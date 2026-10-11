import Foundation
import AVFoundation

public struct AnalysisImageState: @unchecked Sendable {
    public var image: CVImageBuffer
    public var fps: Float
}
