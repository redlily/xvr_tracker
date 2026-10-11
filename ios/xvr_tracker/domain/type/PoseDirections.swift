import Foundation
import core_type
import common

public struct PoseDirections: Sendable, Equatable {
    public struct ArmPoseDirections: Sendable, Equatable {
        public var shoulderToElbow: Vector3?
        public var elbowToWrist: Vector3?
        
        public init(
            shoulderToElbow: Vector3?,
            elbowToWrist: Vector3?
        ) {
            self.shoulderToElbow = shoulderToElbow
            self.elbowToWrist = elbowToWrist
        }
    }
    
    public struct LegPoseDirections: Sendable, Equatable {
        public var hipToKnee: Vector3?
        public var kneeToAnkle: Vector3?
        public var ankleToToe: Vector3?
        
        public init(
            hipToKnee: Vector3?,
            kneeToAnkle: Vector3?,
            ankleToToe: Vector3?
        ) {
            self.hipToKnee = hipToKnee
            self.kneeToAnkle = kneeToAnkle
            self.ankleToToe = ankleToToe
        }
    }
    
    public var hipToSide: Vector3?
    public var hipToShoulder: Vector3?
    public var shoulderToSide: Vector3?
    public var shoulderToHead: Vector3?
    public var headToSide: Vector3?
    public var headToForward: Vector3?
    public var leftArm: ArmPoseDirections
    public var leftLeg: LegPoseDirections
    public var rightArm: ArmPoseDirections
    public var rightLeg: LegPoseDirections
    
    public init(
        hipToSide: Vector3?,
        hipToShoulder: Vector3?,
        shoulderToSide: Vector3?,
        shoulderToHead: Vector3?,
        headToSide: Vector3?,
        headToForward: Vector3?,
        leftArm: ArmPoseDirections,
        leftLeg: LegPoseDirections,
        rightArm: ArmPoseDirections,
        rightLeg: LegPoseDirections
    ) {
        self.hipToSide = hipToSide
        self.hipToShoulder = hipToShoulder
        self.shoulderToSide = shoulderToSide
        self.shoulderToHead = shoulderToHead
        self.headToSide = headToSide
        self.headToForward = headToForward
        self.leftArm = leftArm
        self.leftLeg = leftLeg
        self.rightArm = rightArm
        self.rightLeg = rightLeg
    }
}

public struct PoseDirectionsData: Sendable, Equatable {
    public var data: [PoseDirections]
    public var timestamp: Int64
    
    public init(data: [PoseDirections], timestamp: Int64) {
        self.data = data
        self.timestamp = timestamp
    }
}

