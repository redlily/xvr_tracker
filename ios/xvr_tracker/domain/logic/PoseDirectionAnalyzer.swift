import Foundation
import core_type
import common
import data
import infra

public final actor PoseDirectionAnalyzer {
    private var previousDirections: [PoseDirections] = []
    
    public init() {}
    
    public func reset() {
        previousDirections = []
    }
    
    public func analyze(landmarksData: PoseLandmarksData) -> [PoseDirections] {
        let directions: [PoseDirections] = landmarksData.landmarks.enumerated().map { index, landmarks in
            let previous = index < previousDirections.count ? previousDirections[index] : nil
            return analyzePose(landmarks: landmarks, previousDirections: previous)
        }
        previousDirections = directions
        return directions
    }
    
    private func analyzePose(
        landmarks: [PoseLandmarkType: PoseLandmarkData],
        previousDirections: PoseDirections?
    ) -> PoseDirections {
        // hip
        let leftHip = landmarks[.leftHip]?.worldLandmark
        let rightHip = landmarks[.rightHip]?.worldLandmark
        let hip = Vector3.center(leftHip, rightHip)
        let hipToSide = Vector3.direction(from: leftHip, to: rightHip)
        
        // shoulder
        let leftShoulder = landmarks[.leftShoulder]?.worldLandmark
        let rightShoulder = landmarks[.rightShoulder]?.worldLandmark
        let shoulder = Vector3.center(leftShoulder, rightShoulder)
        let shoulderToSide = Vector3.direction(from: leftShoulder, to: rightShoulder)
        let hipToShoulder = Vector3.direction(from: hip, to: shoulder)
        
        // head
        let nose = landmarks[.nose]?.worldLandmark
        let leftEar = landmarks[.leftEar]?.worldLandmark
        let rightEar = landmarks[.rightEar]?.worldLandmark
        let head = Vector3.center(leftEar, rightEar)
        let headToSide = Vector3.direction(from: leftEar, to: rightEar)
        let headToForward = Vector3.direction(from: head, to: nose)
        let shoulderToHead = Vector3.direction(from: shoulder, to: head)
        
        return PoseDirections(
            hipToSide: hipToSide,
            hipToShoulder: hipToShoulder,
            shoulderToSide: shoulderToSide,
            shoulderToHead: shoulderToHead,
            headToSide: headToSide,
            headToForward: headToForward,
            leftArm: analyzeArmPose(
                landmarks: landmarks,
                shoulderToSide: shoulderToSide,
                hipToShoulder: hipToShoulder,
                previous: previousDirections,
                isLeft: true
            ),
            leftLeg: analyzeLegPoseDirections(
                landmarks: landmarks,
                hipToSide: hipToSide,
                hipToShoulder: hipToShoulder,
                previous: previousDirections,
                isLeft: true
            ),
            rightArm: analyzeArmPose(
                landmarks: landmarks,
                shoulderToSide: shoulderToSide,
                hipToShoulder: hipToShoulder,
                previous: previousDirections,
                isLeft: false
            ),
            rightLeg: analyzeLegPoseDirections(
                landmarks: landmarks,
                hipToSide: hipToSide,
                hipToShoulder: hipToShoulder,
                previous: previousDirections,
                isLeft: false
            )
        )
    }
    
    private func analyzeArmPose(
        landmarks: [PoseLandmarkType: PoseLandmarkData],
        shoulderToSide: Vector3?,
        hipToShoulder: Vector3?,
        previous: PoseDirections?,
        isLeft: Bool
    ) -> PoseDirections.ArmPoseDirections {
        let shoulderKey: PoseLandmarkType = isLeft ? .leftShoulder : .rightShoulder
        let elbowKey: PoseLandmarkType = isLeft ? .leftElbow : .rightElbow
        let wristKey: PoseLandmarkType = isLeft ? .leftWrist : .rightWrist
        let shoulder = landmarks[shoulderKey]?.worldLandmark
        let elbow = landmarks[elbowKey]?.worldLandmark
        let wrist = landmarks[wristKey]?.worldLandmark
        
        let prevShoulderToSide = previous?.shoulderToSide
        let prevHipToShoulder = previous?.hipToShoulder
        var modificationMatrix: Matrix4x4
        if let prevShoulderToSide, let prevHipToShoulder, let shoulderToSide, let hipToShoulder {
            let currentAxis = Axis3.xzy(x: shoulderToSide, ref: hipToShoulder).toMatrix4x4()
            let previousAxis = Axis3.xzy(x: prevShoulderToSide, ref: prevHipToShoulder).toMatrix4x4()
            modificationMatrix = currentAxis * previousAxis.transpose()
        } else {
            modificationMatrix = .identity
        }
        
        let prevArm = isLeft ? previous?.leftArm : previous?.rightArm
        var shoulderToElbow = Vector3.direction(from: shoulder, to: elbow)
        if let current = shoulderToElbow, let prev = prevArm?.shoulderToElbow {
            modificationMatrix = Matrix4x4.rotation(from: prev, to: current) * modificationMatrix
        } else if shoulderToElbow == nil, let prev = prevArm?.shoulderToElbow {
            shoulderToElbow = modificationMatrix * prev
        }
        
        var elbowToWrist = Vector3.direction(from: elbow, to: wrist)
        if elbowToWrist == nil, let prev = prevArm?.elbowToWrist {
            elbowToWrist = modificationMatrix * prev
        }
        
        return PoseDirections.ArmPoseDirections(
            shoulderToElbow: shoulderToElbow,
            elbowToWrist: elbowToWrist
        )
    }
    
    private func analyzeLegPoseDirections(
        landmarks: [PoseLandmarkType: PoseLandmarkData],
        hipToSide: Vector3?,
        hipToShoulder: Vector3?,
        previous: PoseDirections?,
        isLeft: Bool
    ) -> PoseDirections.LegPoseDirections {
        let hipKey: PoseLandmarkType = isLeft ? .leftHip : .rightHip
        let kneeKey: PoseLandmarkType = isLeft ? .leftKnee : .rightKnee
        let ankleKey: PoseLandmarkType = isLeft ? .leftAnkle : .rightAnkle
        let toeKey: PoseLandmarkType = isLeft ? .leftFootIndex : .rightFootIndex
        let hip = landmarks[hipKey]?.worldLandmark
        let knee = landmarks[kneeKey]?.worldLandmark
        let ankle = landmarks[ankleKey]?.worldLandmark
        let toe = landmarks[toeKey]?.worldLandmark
        
        let prevHipToSide = previous?.hipToSide
        let prevHipToShoulder = previous?.hipToShoulder
        var modificationMatrix: Matrix4x4
        if let prevHipToSide, let prevHipToShoulder, let hipToSide, let hipToShoulder {
            let currentAxis = Axis3.xzy(x: hipToSide, ref: hipToShoulder).toMatrix4x4()
            let previousAxis = Axis3.xzy(x: prevHipToSide, ref: prevHipToShoulder).toMatrix4x4()
            modificationMatrix = currentAxis * previousAxis.transpose()
        } else {
            modificationMatrix = .identity
        }
        
        let prevLeg = isLeft ? previous?.leftLeg : previous?.rightLeg
        var hipToKnee = Vector3.direction(from: hip, to: knee)
        if let current = hipToKnee, let prev = prevLeg?.hipToKnee {
            modificationMatrix = Matrix4x4.rotation(from: prev, to: current) * modificationMatrix
        } else if hipToKnee == nil, let prev = prevLeg?.hipToKnee {
            hipToKnee = modificationMatrix * prev
        }
        
        var kneeToAnkle = Vector3.direction(from: knee, to: ankle)
        if let current = kneeToAnkle, let prev = prevLeg?.kneeToAnkle {
            modificationMatrix = Matrix4x4.rotation(from: prev, to: current) * modificationMatrix
        } else if kneeToAnkle == nil, let prev = prevLeg?.kneeToAnkle {
            kneeToAnkle = modificationMatrix * prev
        }
        
        var ankleToToe = Vector3.direction(from: ankle, to: toe)
        if ankleToToe == nil, let prev = prevLeg?.ankleToToe {
            ankleToToe = modificationMatrix * prev
        }
        
        return PoseDirections.LegPoseDirections(
            hipToKnee: hipToKnee,
            kneeToAnkle: kneeToAnkle,
            ankleToToe: ankleToToe
        )
    }
}

