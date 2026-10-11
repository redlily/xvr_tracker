import Foundation
import core_type
import common
import data

public final class PoseJointAnalyzer: Sendable {
    private static let hipAngleThreshold = cos(60.0 * Double.pi / 180.0)
    private static let elbowAngleThreshold = cos(20.0 * Double.pi / 180.0)
    private static let kneeAngleThreshold = cos(20.0 * Double.pi / 180.0)
    private static let footAngleThreshold = cos(90.0 * Double.pi / 180.0)
    private static let elbowAngleMaximum = acos(elbowAngleThreshold)
    private static let kneeAngleMaximum = acos(kneeAngleThreshold)
    
    private let bodyMeasurementsSettingsRepository: BodyMeasurementsSettingsRepository
    
    public init(bodyMeasurementsSettingsRepository: BodyMeasurementsSettingsRepository) {
        self.bodyMeasurementsSettingsRepository = bodyMeasurementsSettingsRepository
    }
    
    public func analyze(directionsData: [PoseDirections]) -> [PoseJoints] {
        let settings = bodyMeasurementsSettingsRepository.dataConsumer.value
        let bodyMeasurements = settings.bodyMeasurements()
        return directionsData.map { directions in
            analyzePoseJoints(poseDirections: directions, bodyMeasurements: bodyMeasurements)
        }
    }
    
    private func analyzePoseJoints(
        poseDirections: PoseDirections,
        bodyMeasurements: BodyMeasurements
    ) -> PoseJoints {
        let hipToSide = poseDirections.hipToSide
        let hipToShoulder = poseDirections.hipToShoulder
        let shoulderToSide = poseDirections.shoulderToSide
        let shoulderToHead = poseDirections.shoulderToHead
        let headToSide = poseDirections.headToSide
        let headToForward = poseDirections.headToForward
        
        let hip: PoseJoint?
        if let hipToSide, let hipToShoulder {
            hip = PoseJoint(
                position: .zero,
                axis: Axis3.xzy(x: hipToSide, ref: hipToShoulder)
            )
        } else {
            hip = nil
        }
        
        let waist: PoseJoint?
        if let hip, let hipToShoulder {
            waist = PoseJoint(
                position: hip.position + hipToShoulder * bodyMeasurements.waistToHipLength,
                axis: hip.axis
            )
        } else {
            waist = nil
        }
        
        let shoulder: PoseJoint?
        if let shoulderToSide, let hipToShoulder {
            shoulder = PoseJoint(
                position: hipToShoulder * bodyMeasurements.hipToShoulderLength,
                axis: Axis3.xzy(x: shoulderToSide, ref: hipToShoulder)
            )
        } else {
            shoulder = nil
        }
        
        let chest: PoseJoint?
        if let shoulder, let hipToShoulder {
            chest = PoseJoint(
                position: shoulder.position - hipToShoulder * bodyMeasurements.shoulderToChestLength,
                axis: shoulder.axis
            )
        } else {
            chest = nil
        }
        
        let head: PoseJoint?
        if let shoulder, let shoulderToHead, let headToSide, let headToForward {
            let position = shoulder.position
                + shoulderToHead * bodyMeasurements.headToShoulderLength
                + headToForward * (-bodyMeasurements.headHeight * 0.25)
                + headToForward * bodyMeasurements.headZOffset
            head = PoseJoint(
                position: position,
                axis: Axis3.zyx(z: headToForward, ref: headToSide)
            )
        } else {
            head = nil
        }
        
        return PoseJoints(
            waist: waist,
            chest: chest,
            head: head,
            leftArm: analyzeArmPoseJoints(
                shoulder: shoulder,
                armPoseDirections: poseDirections.leftArm,
                bodyMeasurements: bodyMeasurements,
                isLeft: true
            ),
            leftLeg: analyzeLegPoseJoints(
                hip: hip,
                legPoseDirections: poseDirections.leftLeg,
                bodyMeasurements: bodyMeasurements,
                isLeft: true
            ),
            rightArm: analyzeArmPoseJoints(
                shoulder: shoulder,
                armPoseDirections: poseDirections.rightArm,
                bodyMeasurements: bodyMeasurements,
                isLeft: false
            ),
            rightLeg: analyzeLegPoseJoints(
                hip: hip,
                legPoseDirections: poseDirections.rightLeg,
                bodyMeasurements: bodyMeasurements,
                isLeft: false
            )
        )
    }
    
    private func analyzeArmPoseJoints(
        shoulder: PoseJoint?,
        armPoseDirections: PoseDirections.ArmPoseDirections,
        bodyMeasurements: BodyMeasurements,
        isLeft: Bool
    ) -> PoseJoints.ArmPoseJoints {
        guard let shoulder, let shoulderToElbow = armPoseDirections.shoulderToElbow else {
            return PoseJoints.ArmPoseJoints(elbow: nil)
        }
        let elbowToWrist = armPoseDirections.elbowToWrist
        var axis = Matrix4x4.rotation(
            from: shoulder.axis.x,
            to: isLeft ? -shoulderToElbow : shoulderToElbow
        ) * shoulder.axis
        if let elbowToWrist, shoulder.axis.z.dot(Vector3.z) > Self.hipAngleThreshold {
            let axisRotation: Vector3.DotCross = isLeft ? shoulderToElbow.dotCross(elbowToWrist) : elbowToWrist.dotCross(shoulderToElbow)
            let rate = PoseJointAnalyzer.rotationRate(dot: axisRotation.dot, maximum: Self.elbowAngleMaximum)
            axis = Matrix4x4.rotation(
                from: axis.y,
                to: axisRotation.cross,
                rate: rate
            ) * axis
        }
        let lateral = (isLeft ? -shoulder.axis.x : shoulder.axis.x) * (bodyMeasurements.shoulderWidth / 2)
        let forward = shoulderToElbow * (bodyMeasurements.armLength / 2)
        let position = shoulder.position + lateral + forward
        let elbow = PoseJoint(position: position, axis: axis)
        return PoseJoints.ArmPoseJoints(elbow: elbow)
    }
    
    private func analyzeLegPoseJoints(
        hip: PoseJoint?,
        legPoseDirections: PoseDirections.LegPoseDirections,
        bodyMeasurements: BodyMeasurements,
        isLeft: Bool
    ) -> PoseJoints.LegPoseJoints {
        let hipToKnee = legPoseDirections.hipToKnee
        let kneeToAnkle = legPoseDirections.kneeToAnkle
        let ankleToToe = legPoseDirections.ankleToToe
        
        var kneeJoint: PoseJoint?
        if let hip, let hipToKnee {
            var axis = Matrix4x4.rotation(from: hip.axis.y, to: -hipToKnee) * hip.axis
            if let kneeToAnkle, hip.axis.z.dot(Vector3.z) > Self.hipAngleThreshold {
                let rotationAxis = hipToKnee.dotCross(kneeToAnkle)
                if hip.axis.x.dot(rotationAxis.cross) > 0 {
                    let rate = Self.rotationRate(dot: rotationAxis.dot, maximum: Self.kneeAngleMaximum)
                    axis = Matrix4x4.rotation(from: axis.x, to: rotationAxis.cross, rate: rate) * axis
                }
            }
            let lateral = (isLeft ? -hip.axis.x : hip.axis.x) * (bodyMeasurements.hipWidth / 2)
            let forward = hipToKnee * (bodyMeasurements.legLength / 2)
            let position = hip.position + lateral + forward
            kneeJoint = PoseJoint(position: position, axis: axis)
        }
        
        let feetJoint: PoseJoint?
        if let knee = kneeJoint, let kneeToAnkle {
            let axis = Matrix4x4.rotation(from: knee.axis.y, to: -kneeToAnkle) * knee.axis
            if let ankleToToe, let hip, hip.axis.z.dot(Vector3.z) > Self.hipAngleThreshold {
                let rotationAxis = ankleToToe.dotCross(kneeToAnkle)
                let rate = Self.rotationRate(dot: rotationAxis.dot, maximum: Self.kneeAngleMaximum)
                let modifiedAxis = Matrix4x4.rotation(from: axis.x, to: rotationAxis.cross, rate: rate) * axis
                let position = knee.position
                    + kneeToAnkle * (bodyMeasurements.legLength / 2)
                    + ankleToToe * (bodyMeasurements.footLength / 2)
                feetJoint = PoseJoint(position: position, axis: modifiedAxis)
            } else {
                let position = knee.position
                    + kneeToAnkle * (bodyMeasurements.legLength / 2)
                    + ((-axis.y) * bodyMeasurements.ankleToHeelLength + axis.z * bodyMeasurements.footLength) / 2
                feetJoint = PoseJoint(position: position, axis: axis)
            }
        } else {
            feetJoint = nil
        }
        
        return PoseJoints.LegPoseJoints(knee: kneeJoint, feet: feetJoint)
    }
    
    private static func rotationRate(dot: Double, maximum: Double) -> Double {
        let clampedDot = max(-1.0, min(1.0, dot))
        let numerator = acos(clampedDot)
        guard maximum > 0 else { return 1 }
        return min(numerator / maximum, 1)
    }
}

