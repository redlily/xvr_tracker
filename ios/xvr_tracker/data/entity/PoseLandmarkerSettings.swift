import Foundation
import infra
import common

public struct PoseLandmarkerSettings: Sendable, Equatable {
    public var modelType: PoseLandmarkerModelType
    public var computeType: PoseLandmarkerComputeType
    public var visibilityThreshold: RangedValue<Double>
    public var presenceThreshold: RangedValue<Double>
    
    public static let visibilityThresholdRange: ClosedRange<Double> = 0.0...1.0
    public static let presenceThresholdRange: ClosedRange<Double> = 0.0...1.0
    
    public static let `default` = PoseLandmarkerSettings(
        modelType: .standard,
        computeType: .cpu,
        visibilityThreshold: RangedValue(0.5, in: visibilityThresholdRange),
        presenceThreshold: RangedValue(0.5, in: presenceThresholdRange)
    )
}
