import Foundation
import core_type
import common

public struct PoseJoint: Sendable, Equatable {
    public var position: Vector3
    public var axis: Axis3
    
    public init(position: Vector3, axis: Axis3) {
        self.position = position
        self.axis = axis
    }
}

public struct PoseJoints: Sendable, Equatable {
    public struct ArmPoseJoints: Sendable, Equatable {
        public var elbow: PoseJoint?
        
        public init(elbow: PoseJoint?) {
            self.elbow = elbow
        }
    }
    
    public struct LegPoseJoints: Sendable, Equatable {
        public var knee: PoseJoint?
        public var feet: PoseJoint?
        
        public init(knee: PoseJoint?, feet: PoseJoint?) {
            self.knee = knee
            self.feet = feet
        }
    }
    
    public var waist: PoseJoint?
    public var chest: PoseJoint?
    public var head: PoseJoint?
    public var leftArm: ArmPoseJoints
    public var leftLeg: LegPoseJoints
    public var rightArm: ArmPoseJoints
    public var rightLeg: LegPoseJoints
    
    public init(
        waist: PoseJoint?,
        chest: PoseJoint?,
        head: PoseJoint?,
        leftArm: ArmPoseJoints,
        leftLeg: LegPoseJoints,
        rightArm: ArmPoseJoints,
        rightLeg: LegPoseJoints
    ) {
        self.waist = waist
        self.chest = chest
        self.head = head
        self.leftArm = leftArm
        self.leftLeg = leftLeg
        self.rightArm = rightArm
        self.rightLeg = rightLeg
    }
}

public struct PoseJointsData: Sendable, Equatable {
    public var data: [PoseJoints]
    public var timestamp: Int64
    
    public init(data: [PoseJoints], timestamp: Int64) {
        self.data = data
        self.timestamp = timestamp
    }
}

