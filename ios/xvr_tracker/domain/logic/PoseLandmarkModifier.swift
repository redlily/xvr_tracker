import Foundation
import UIKit
import core_type
import infra
import common
import data

public final actor PoseLandmarkModifier {
    public struct LandmarksStateData: Sendable {
        public var landmarks: [PoseLandmarkType: PoseLandmarkData]
        public var motionType: PoseMotionType
        public var standTime: Int
        public var sitTime: Int
        public var lieDownTime: Int
        
        public init(
            landmarks: [PoseLandmarkType: PoseLandmarkData],
            motionType: PoseMotionType,
            standTime: Int,
            sitTime: Int,
            lieDownTime: Int
        ) {
            self.landmarks = landmarks
            self.motionType = motionType
            self.standTime = standTime
            self.sitTime = sitTime
            self.lieDownTime = lieDownTime
        }
    }
    
    public struct LandmarksStatesData: Sendable {
        public var data: [LandmarksStateData]
        public var timestamp: Int
        
        public init(data: [LandmarksStateData], timestamp: Int) {
            self.data = data
            self.timestamp = timestamp
        }
    }
    
    private let poseLandmarkerSettingsRepository: PoseLandmarkerSettingsRepository
    private let poseModificationSettingsRepository: PoseModificationSettingsRepository
    private let bodyMeasurementsSettingsRepository: BodyMeasurementsSettingsRepository
    private let toneBeep: ToneBeep
    
    private var upper: Vector3 = Vector3(x: 0, y: 1, z: 0)
    private var modificationMatrix: Matrix4x4 = .identity
    private var landmarksStateData: LandmarksStatesData = LandmarksStatesData(
        data: [],
        timestamp: 0
    )
    
    public init(
        poseLandmarkerSettingsRepository: PoseLandmarkerSettingsRepository,
        poseModificationSettingsRepository: PoseModificationSettingsRepository,
        bodyMeasurementsSettingsRepository: BodyMeasurementsSettingsRepository,
        toneBeep: ToneBeep
    ) {
        self.poseLandmarkerSettingsRepository = poseLandmarkerSettingsRepository
        self.poseModificationSettingsRepository = poseModificationSettingsRepository
        self.bodyMeasurementsSettingsRepository = bodyMeasurementsSettingsRepository
        self.toneBeep = toneBeep
    }
    
    public func reset() {
        landmarksStateData = LandmarksStatesData(
            data: [],
            timestamp: 0
        )
    }
    
    public func updateUpperDirection(_ direction: Vector3) {
        upper = direction.normalized()
        let cs = upper.dot(Vector3.y)
        if cs > 0.99 {
            modificationMatrix = .identity
        } else if cs < -0.99 {
            modificationMatrix = Matrix4x4(
                m00: -1, m01: 0, m02: 0, m03: 0,
                m10: 0, m11: -1, m12: 0, m13: 0,
                m20: 0, m21: 0, m22: 1, m23: 0,
                m30: 0, m31: 0, m32: 0, m33: 1
            )
        } else {
            let axis = upper.cross(Vector3.y)
            modificationMatrix = Matrix4x4.rotation(axis: axis, angle: acos(cs))
        }
    }
    
    public func modify(_ landmarksData: PoseLandmarksData) async -> PoseLandmarksData {
        let landmarkerSettings = poseLandmarkerSettingsRepository.dataConsumer.value
        let modificationSettings = poseModificationSettingsRepository.dataConsumer.value
        let bodyMeasurements = bodyMeasurementsSettingsRepository.dataConsumer.value.bodyMeasurements()
        
        let deltaTime = landmarksData.timestamp - landmarksStateData.timestamp
        
        let filteredLandmarks = filter(
            landmarks: landmarksData.landmarks,
            landmarkerSettings: landmarkerSettings
        )
        let correctedLandmarks = correct(
            landmarks: filteredLandmarks,
            modificationSettings: modificationSettings,
            bodyMeasurements: bodyMeasurements
        )
        let stabilizedLandmarks = stabilize(
            landmarks: correctedLandmarks,
            landmarkerSettings: landmarkerSettings,
            modificationSettings: modificationSettings,
            deltaTime: deltaTime
        )
        
        updateState(
            landmarksData: landmarksData,
            landmarks: stabilizedLandmarks,
            modificationSettings: modificationSettings,
            deltaTime: deltaTime
        )
        
        return update(landmarksData) {
            $0.landmarks = stabilizedLandmarks
        }
    }
    
    private func filter(
        landmarks: [[PoseLandmarkType: PoseLandmarkData]],
        landmarkerSettings: PoseLandmarkerSettings
    ) -> [[PoseLandmarkType: PoseLandmarkData]] {
        landmarks.map { landmarks in
            landmarks.filter { (_, landmark) in
                landmark.visibility > landmarkerSettings.visibilityThreshold.value &&
                landmark.presence > landmarkerSettings.presenceThreshold.value
            }
        }
    }
    
    private func correct(
        landmarks: [[PoseLandmarkType: PoseLandmarkData]],
        modificationSettings: PoseModificationSettings,
        bodyMeasurements: BodyMeasurements
    ) -> [[PoseLandmarkType: PoseLandmarkData]] {
        landmarks.enumerated().map { index, currentLandmarks in
            let leftShoulder = currentLandmarks[.leftShoulder]
            let rightShoulder = currentLandmarks[.rightShoulder]
            let leftHip = currentLandmarks[.leftHip]
            let rightHip = currentLandmarks[.rightHip]
            
            let hipAxis: Axis3?
            let worldMatrix: Matrix4x4?
            
            if let leftShoulder, let rightShoulder, let leftHip, let rightHip {
                let shoulder = Vector3.center(
                    leftShoulder.worldLandmark,
                    rightShoulder.worldLandmark
                )
                let hip = Vector3.center(
                    leftHip.worldLandmark,
                    rightHip.worldLandmark
                )
                let hipToShoulder = Vector3.direction(
                    from: hip,
                    to: shoulder
                )
                let hipToSide = Vector3.direction(
                    from: leftHip.worldLandmark,
                    to: rightHip.worldLandmark
                )
                hipAxis = Axis3.xzy(
                    x: hipToSide,
                    ref: hipToShoulder
                )
                
                let axis = Vector3.z.cross(hipToShoulder).normalized()
                let effective = hipAxis!.z.dot(Vector3.z)
                worldMatrix = Matrix4x4.rotation(
                    axis: axis,
                    angle: effective > 0 ? modificationSettings.bodyPositiveSlope.value * effective :
                        modificationSettings.bodyNegativeSlope.value * -effective,
                    isDegree: true
                )
            } else {
                hipAxis = nil
                worldMatrix = nil
            }
            
            if let landmarksState = landmarksStateData.data.indices.contains(index) ?
                landmarksStateData.data[index] : nil
            {
                var newLandmarks = currentLandmarks
                
                correctHip(
                    landmarks: &newLandmarks,
                    bodyMeasurements: bodyMeasurements
                )
                
                if let hipAxis {
                    imputeLegs(
                        landmarks: &newLandmarks,
                        hipAxis: hipAxis,
                        bodyMeasurements: bodyMeasurements
                    )
                }
                
                if let hipAxis, landmarksState.motionType == .stand {
                    correctKnees(
                        landmarks: &newLandmarks,
                        hipAxis: hipAxis,
                        modificationSettings: modificationSettings
                    )
                    
                    correctLegs(
                        landmarks: &newLandmarks,
                        hipAxis: hipAxis,
                        modificationSettings: modificationSettings
                    )
                }
                
                if let hipAxis,
                    landmarksState.motionType == .sit ||
                    landmarksState.motionType == .lieDown
                {
                    restrictLegs(
                        landmarks: &newLandmarks,
                        hipAxis: hipAxis,
                        motionType: landmarksState.motionType
                    )
                }
                
                if let worldMatrix {
                    for (key, value) in newLandmarks {
                        newLandmarks[key] = update(value) {
                            $0.worldLandmark = worldMatrix * value.worldLandmark
                        }
                    }
                }
                return newLandmarks
            } else {
                return currentLandmarks
            }
        }
    }
    
    private func correctHip(
        landmarks: inout [PoseLandmarkType: PoseLandmarkData],
        bodyMeasurements: BodyMeasurements
    ) {
        guard let leftShoulder = landmarks[.leftShoulder],
              let rightShoulder = landmarks[.rightShoulder],
              let leftHip = landmarks[.leftHip],
              let rightHip = landmarks[.rightHip]
        else { return }
        
        let widthRatio = bodyMeasurements.hipWidth / bodyMeasurements.shoulderWidth
        let shoulderWidth = (rightShoulder.worldLandmark - leftShoulder.worldLandmark).length
        let hipWidth = shoulderWidth * widthRatio
        
        let shoulderCenter = Vector3.center(leftShoulder.worldLandmark, rightShoulder.worldLandmark)
        let hipCenter = Vector3.center(leftHip.worldLandmark, rightHip.worldLandmark)
        
        let shoulderAxis = Axis3.yzx(
            y: hipCenter - shoulderCenter,
            ref: rightShoulder.worldLandmark - leftShoulder.worldLandmark
        )
        
        let leftToRight = rightHip.worldLandmark - leftHip.worldLandmark
        
        let depth = shoulderAxis.z * leftToRight.dot(shoulderAxis.z)
        let height = shoulderAxis.y * leftToRight.dot(shoulderAxis.y)
        let widthSq = hipWidth * hipWidth - height.dot(height) - depth.dot(depth)
        if widthSq <= 0 { return }
        let width = shoulderAxis.x * sqrt(widthSq)
        
        landmarks[.leftHip] = update(leftHip) {
            $0.worldLandmark = (hipCenter + (width + height + depth) / 2)
        }
        landmarks[.rightHip] = update(rightHip) {
            $0.worldLandmark = (hipCenter - (width + height + depth) / 2)
        }
    }
    
    private func correctKnees(
        landmarks: inout [PoseLandmarkType: PoseLandmarkData],
        hipAxis: Axis3,
        modificationSettings: PoseModificationSettings
    ) {
        correctKnee(
            landmarks: &landmarks,
            hipAxis: hipAxis,
            isLeft: true,
            modificationSettings: modificationSettings
        )
        correctKnee(
            landmarks: &landmarks,
            hipAxis: hipAxis,
            isLeft: false,
            modificationSettings: modificationSettings
        )
    }
    
    private func correctKnee(
        landmarks: inout [PoseLandmarkType: PoseLandmarkData],
        hipAxis: Axis3,
        isLeft: Bool,
        modificationSettings: PoseModificationSettings
    ) {
        guard let hip = landmarks[isLeft ? .leftHip : .rightHip],
              let knee = landmarks[isLeft ? .leftKnee : .rightKnee]
        else { return }
        
        let kneeToHip = hip.worldLandmark - knee.worldLandmark
        let axis = Axis3.yzx(y: kneeToHip, ref: hipAxis.x)
        
        let effective = axis.z.dot(Vector3.z)
        let matrix = (
            Matrix4x4.translation(-hip.worldLandmark) *
            Matrix4x4.rotation(
                axis: axis.x,
                angle: (
                    effective < 0 ?
                    modificationSettings.kneePositiveSlope.value * effective :
                        modificationSettings.kneeNegativeSlope.value * -effective
                ),
                isDegree: true
            ) * Matrix4x4.translation(hip.worldLandmark)
        )
        
        applyMatrix(
            landmarks: &landmarks,
            target: isLeft ? .leftKnee : .rightKnee,
            matrix: matrix
        )
    }
    
    private func imputeLegs(
        landmarks: inout [PoseLandmarkType: PoseLandmarkData],
        hipAxis: Axis3,
        bodyMeasurements: BodyMeasurements
    ) {
        imputeLeg(
            landmarks: &landmarks,
            hipAxis: hipAxis,
            isLeft: true,
            bodyMeasurements: bodyMeasurements
        )
        imputeLeg(
            landmarks: &landmarks,
            hipAxis: hipAxis,
            isLeft: false,
            bodyMeasurements: bodyMeasurements
        )
    }
    
    private func imputeLeg(
        landmarks: inout [PoseLandmarkType: PoseLandmarkData],
        hipAxis: Axis3,
        isLeft: Bool,
        bodyMeasurements: BodyMeasurements
    ) {
        guard let hip = landmarks[isLeft ? .leftHip : .rightHip] else {
            return
        }
        
        let knee = landmarks[isLeft ? .leftKnee : .rightKnee]
        var ankle = landmarks[isLeft ? .leftAnkle : .rightAnkle]
        
        if let heel = landmarks[isLeft ? .leftHeel: .rightHeel], ankle == nil {
            ankle = update(heel) {
                $0.visibility = 0
                $0.presence = 0
            }
            landmarks[isLeft ? .leftAnkle : .rightAnkle] = ankle
        }
        
        if let ankle,
           let oppositeKnee = landmarks[isLeft ? .rightKnee : .leftKnee],
           let oppositeHip = landmarks[isLeft ? .rightHip : .leftHip],
           knee == nil
        {
            let hipToAnkle = ankle.worldLandmark - hip.worldLandmark
            let hypotenuse = (oppositeKnee.worldLandmark - oppositeHip.worldLandmark).length
            let base = hipToAnkle.length / 2
            let heightSq = hypotenuse * hypotenuse - base * base
            let height = if heightSq > 0 { sqrt(hypotenuse * hypotenuse - base * base) } else { 0.0 }
            
            let knee = (
                Matrix4x4.translation(-hip.worldLandmark) *
                Matrix4x4.rotation(from: -hipAxis.y, to: hipToAnkle) *
                Matrix4x4.translation(hip.worldLandmark) *
                (-hipAxis.y * base + hipAxis.z * height)
            )
            
            landmarks[isLeft ? .leftKnee : .rightKnee] = PoseLandmarkData(
                normalizedLandmark: .zero,
                worldLandmark: knee,
                visibility: 0,
                presence: 0
            )
        }
    }
    
    private func correctLegs(
        landmarks: inout [PoseLandmarkType: PoseLandmarkData],
        hipAxis: Axis3,
        modificationSettings: PoseModificationSettings
    ) {
        correntLeg(
            landmarks: &landmarks,
            hipAxis: hipAxis,
            isLeft: true,
            modificationSettings: modificationSettings
        )
        correntLeg(
            landmarks: &landmarks,
            hipAxis: hipAxis,
            isLeft: false,
            modificationSettings: modificationSettings
        )
    }
    
    private func correntLeg(
        landmarks: inout [PoseLandmarkType: PoseLandmarkData],
        hipAxis: Axis3,
        isLeft: Bool,
        modificationSettings: PoseModificationSettings
    ) {
        guard let hip = landmarks[isLeft ? .leftHip : .rightHip],
              let ankle = landmarks[isLeft ? .leftAnkle : .rightAnkle]
        else { return }
        
        let AnkleToHip = hip.worldLandmark - ankle.worldLandmark
        let axis = Axis3.yzx(y: AnkleToHip, ref: hipAxis.x)
        
        let cs = hipAxis.z.dot(.z)
        let sn = sqrt(1.0 - cs * cs)
        let effective = 2.0 * cs * sn
        let matrix = (
            Matrix4x4.translation(-hip.worldLandmark) *
            Matrix4x4.rotation(
                axis: axis.x,
                angle: (
                    cs > 0 ?
                    modificationSettings.footPositiveSlope.value * effective:
                        modificationSettings.footNegativeSlope.value * effective
                ),
                isDegree: true
            ) * Matrix4x4.translation(hip.worldLandmark)
        )
        
        applyMatrix(
            landmarks: &landmarks,
            target: isLeft ? .leftKnee : .rightKnee,
            matrix: matrix
        )
        applyMatrix(
            landmarks: &landmarks,
            target: isLeft ? .leftAnkle : .rightAnkle,
            matrix: matrix
        )
        applyMatrix(
            landmarks: &landmarks,
            target: isLeft ? .leftHeel : .rightHeel,
            matrix: matrix
        )
        applyMatrix(
            landmarks: &landmarks,
            target: isLeft ? .leftFootIndex : .rightFootIndex,
            matrix: matrix
        )
    }
    
    private func restrictLegs(
        landmarks: inout [PoseLandmarkType: PoseLandmarkData],
        hipAxis: Axis3,
        motionType: PoseMotionType
    ) {
        restrictLeg(
            landmarks: &landmarks,
            hipAxis: hipAxis,
            isLeft: true,
            motionType: motionType
        )
        restrictLeg(
            landmarks: &landmarks,
            hipAxis: hipAxis,
            isLeft: false,
            motionType: motionType
        )
    }
    
    private func restrictLeg(
        landmarks: inout [PoseLandmarkType: PoseLandmarkData],
        hipAxis: Axis3,
        isLeft: Bool,
        motionType: PoseMotionType
    ) {
        guard let hip = landmarks[isLeft ? .leftHip : .rightHip] else {
            return
        }
        
        let ankle: PoseLandmarkData
        if let data = landmarks[isLeft ? .leftAnkle : .rightAnkle] {
            ankle = data
        } else if let knee = landmarks[isLeft ? .leftKnee : .rightKnee],
                  landmarks[isLeft ? .leftHeel : .rightHeel] == nil &&
                    landmarks[isLeft ? .leftFootIndex : .rightFootIndex] == nil &&
                    motionType == .sit
        {
            let hipToKnee = hip.worldLandmark - knee.worldLandmark
            let kneeAxis = hipAxis.z.cross(upper).normalized()
            let kneeToAnkle = kneeAxis.cross(upper).normalized() * hipToKnee.length
            
            landmarks[isLeft ? .leftAnkle : .rightAnkle] = PoseLandmarkData(
                normalizedLandmark: Vector3.zero,
                worldLandmark: (knee.worldLandmark + kneeToAnkle),
                visibility: 0,
                presence: 0
            )
            return
        } else {
            return
        }
        
        let hipToAnkle = Vector3.direction(
            from: hip.worldLandmark,
            to: ankle.worldLandmark
        )
        let cs = hipToAnkle.dot(-upper)
        if cs < 0 { return }
        
        let axis = (hipToAnkle.cross(upper)).normalized()
        if hipAxis.x.dot(axis) > 0 { return }
        
        let to = upper.cross(axis).normalized()
        let matrix = (
            Matrix4x4.translation(-hip.worldLandmark) *
            Matrix4x4.rotation(from: hipToAnkle, to: to) *
            Matrix4x4.translation(hip.worldLandmark)
        )
        
        applyMatrix(
            landmarks: &landmarks,
            target: isLeft ? .leftKnee : .rightKnee,
            matrix: matrix
        )
        applyMatrix(
            landmarks: &landmarks,
            target: isLeft ? .leftAnkle : .rightAnkle,
            matrix: matrix
        )
        applyMatrix(
            landmarks: &landmarks,
            target: isLeft ? .leftHeel : .rightHeel,
            matrix: matrix
        )
        applyMatrix(
            landmarks: &landmarks,
            target: isLeft ? .leftFootIndex : .rightFootIndex,
            matrix: matrix
        )
    }
    
    private func applyMatrix(
        landmarks: inout [PoseLandmarkType: PoseLandmarkData],
        target: PoseLandmarkType,
        matrix: Matrix4x4
    ) {
        if let landmark = landmarks[target] {
            landmarks[target] = update(landmark) {
                $0.worldLandmark = (matrix * landmark.worldLandmark)
            }
        }
    }
    
    private func stabilize(
        landmarks: [[PoseLandmarkType: PoseLandmarkData]],
        landmarkerSettings: PoseLandmarkerSettings,
        modificationSettings: PoseModificationSettings,
        deltaTime: Int
    ) -> [[PoseLandmarkType: PoseLandmarkData]] {
        landmarks.enumerated().map { index, currentLandmarks in
            if let landmarksState = landmarksStateData.data.indices.contains(index) ?
                landmarksStateData.data[index] : nil
            {
                return Dictionary(uniqueKeysWithValues: currentLandmarks.map { type, currentLandmark in
                    let coefficient: Double
                    switch landmarksState.motionType {
                    case .stand:
                        coefficient = modificationSettings.standStabilizingCoefficient.value
                    case .sit:
                        coefficient = if type.category == .leg {
                            modificationSettings.sitStabilizingCoefficient.value
                        } else {
                            modificationSettings.standStabilizingCoefficient.value
                        }
                    case .lieDown:
                        coefficient = if type.category == .leg || type.category == .body || type.category == .head {
                            modificationSettings.lieDownStabilizingCoefficient.value
                        } else {
                            modificationSettings.standStabilizingCoefficient.value
                        }
                    }
                    let alpha: Double = pow(coefficient, Double(deltaTime) / 1000.0 * 10.0)
                    
                    if let previousLandmark = landmarksState.landmarks[type] {
                        let normalizedPosition =
                        if previousLandmark.visibility > landmarkerSettings.visibilityThreshold.value &&
                            previousLandmark.presence > landmarkerSettings.presenceThreshold.value {
                            currentLandmark.normalizedLandmark * (1 - alpha) +
                            previousLandmark.normalizedLandmark * alpha
                        } else {
                            currentLandmark.normalizedLandmark
                        }
                        let worldPosition = (
                            currentLandmark.worldLandmark * (1 - alpha) +
                            previousLandmark.worldLandmark * alpha
                        )
                        
                        return (type, update(currentLandmark) {
                            $0.normalizedLandmark = normalizedPosition
                            $0.worldLandmark = worldPosition
                        })
                    } else {
                        return (type, currentLandmark)
                    }
                })
            } else {
                return currentLandmarks
            }
        }
    }
    
    private func updateState(
        landmarksData: PoseLandmarksData,
        landmarks: [[PoseLandmarkType: PoseLandmarkData]],
        modificationSettings: PoseModificationSettings,
        deltaTime: Int
    ) {
        landmarksStateData = LandmarksStatesData(
            data: landmarksData.landmarks.indices.map { index in
                let rawLandmarks = landmarksData.landmarks[index]
                let modifiedLandmarks = landmarks[index]
                if let stateData = landmarksStateData.data.indices.contains(index) ?
                    landmarksStateData.data[index] : nil
                {
                    let leftHip = rawLandmarks[.leftHip]
                    let rightHip = rawLandmarks[.rightHip]
                    if let leftHip, let rightHip {
                        let hip = Vector3.center(
                            leftHip.worldLandmark,
                            rightHip.worldLandmark
                        )
                        
                        let leftShoulder = rawLandmarks[.leftShoulder]
                        let rightShoulder = rawLandmarks[.rightShoulder]
                        
                        let isBodyDown: Bool
                        if let leftShoulder, let rightShoulder {
                            let shoulder = Vector3.center(
                                leftShoulder.worldLandmark,
                                rightShoulder.worldLandmark
                            )
                            isBodyDown = acos(Vector3.direction(from: hip, to: shoulder)?.dot(upper) ?? 0) > 50.0.toRadian()
                        } else {
                            isBodyDown = false
                        }
                        
                        let leftKnee = rawLandmarks[.leftKnee]
                        let rightKnee = rawLandmarks[.rightKnee]
                        let leftAnkle = rawLandmarks[.leftAnkle]
                        let rightAnkle = rawLandmarks[.rightAnkle]
                        
                        let isKneesDown: Bool
                        if let leftKnee, let rightKnee {
                            let leftKneeDot = max(-1.0, min(1.0, Vector3.direction(
                                from: leftKnee.worldLandmark,
                                to: leftHip.worldLandmark
                            )?.dot(upper) ?? 0))
                            let rightKneeDot = max(-1.0, min(1.0, Vector3.direction(
                                from: rightKnee.worldLandmark,
                                to: rightHip.worldLandmark
                            )?.dot(upper) ?? 0))
                            isKneesDown = acos(leftKneeDot) > 60.0.toRadian() && acos(rightKneeDot) > 60.0.toRadian()
                        } else {
                            isKneesDown = false
                        }
                        
                        let isAnklesDown: Bool
                        if let leftAnkle, let rightAnkle, let leftKnee, let rightKnee {
                            let leftAnkleDot = max(-1.0, min(1.0, Vector3.direction(
                                from: leftAnkle.worldLandmark,
                                to: leftKnee.worldLandmark
                            )?.dot(upper) ?? 0))
                            let rightAnkleDot = max(-1.0, min(1.0, Vector3.direction(
                                from: rightAnkle.worldLandmark,
                                to: rightKnee.worldLandmark
                            )?.dot(upper) ?? 0))
                            isAnklesDown = acos(leftAnkleDot) > 60.0.toRadian() && acos(rightAnkleDot) > 60.0.toRadian()
                        } else {
                            isAnklesDown = leftAnkle == nil && rightAnkle == nil
                        }
                        
                        let isLegsDown: Bool
                        if let leftAnkle, let rightAnkle {
                            let leftLegDot = max(-1.0, min(1.0, Vector3.direction(
                                from: leftAnkle.worldLandmark,
                                to: leftHip.worldLandmark
                            )?.dot(upper) ?? 0))
                            let rightLegDot = max(-1.0, min(1.0, Vector3.direction(
                                from: rightAnkle.worldLandmark,
                                to: rightHip.worldLandmark
                            )?.dot(upper) ?? 0))
                            isLegsDown = acos(leftLegDot) > 45.0.toRadian() && acos(rightLegDot) > 45.0.toRadian()
                        } else {
                            isLegsDown = false
                        }
                        
                        let motionType: PoseMotionType = if isBodyDown && (isKneesDown || isAnklesDown || isLegsDown) {
                            .lieDown
                        } else if isKneesDown || isAnklesDown || isLegsDown {
                            .sit
                        } else {
                            .stand
                        }
                        
                        var standTime = stateData.standTime
                        var sitTime = stateData.sitTime
                        var lieDownTime = stateData.lieDownTime
                        var currentMotionType = stateData.motionType
                        let thresholdTime = modificationSettings.stabilizingModeChangeThresholdTime.value
                        
                        switch motionType {
                        case .stand:
                            standTime = min(standTime + deltaTime, thresholdTime)
                            sitTime = max(sitTime - deltaTime, 0)
                            lieDownTime = max(lieDownTime - deltaTime, 0)
                            if currentMotionType != motionType && standTime >= thresholdTime {
                                standTime = 0
                                sitTime = 0
                                lieDownTime = 0
                                currentMotionType = motionType
                                beep(modificationSettings: modificationSettings)
                            }
                        case .sit:
                            standTime = max(standTime - deltaTime, 0)
                            sitTime = min(sitTime + deltaTime, thresholdTime)
                            lieDownTime = max(lieDownTime - deltaTime, 0)
                            if currentMotionType != motionType && sitTime >= thresholdTime {
                                standTime = 0
                                sitTime = 0
                                lieDownTime = 0
                                currentMotionType = motionType
                                beep(modificationSettings: modificationSettings)
                            }
                        case .lieDown:
                            standTime = max(standTime - deltaTime, 0)
                            sitTime = max(sitTime - deltaTime, 0)
                            lieDownTime = min(lieDownTime + deltaTime, thresholdTime)
                            if currentMotionType != motionType && lieDownTime >= thresholdTime {
                                standTime = 0
                                sitTime = 0
                                lieDownTime = 0
                                currentMotionType = motionType
                                beep(modificationSettings: modificationSettings)
                            }
                        }
                        
                        return LandmarksStateData(
                            landmarks: modifiedLandmarks,
                            motionType: currentMotionType,
                            standTime: standTime,
                            sitTime: sitTime,
                            lieDownTime: lieDownTime
                        )
                    } else {
                        return LandmarksStateData(
                            landmarks: modifiedLandmarks,
                            motionType: stateData.motionType,
                            standTime: stateData.standTime,
                            sitTime: stateData.sitTime,
                            lieDownTime: stateData.lieDownTime
                        )
                    }
                } else {
                    return LandmarksStateData(
                        landmarks: modifiedLandmarks,
                        motionType: .stand,
                        standTime: 0,
                        sitTime: 0,
                        lieDownTime: 0
                    )
                }
            },
            timestamp: landmarksData.timestamp
        )
    }
    
    private func beep(modificationSettings: PoseModificationSettings) {
        if modificationSettings.isStabilizingModeChangeSoundEnabled {
            Task.detached { [weak self] in
                try await self?.toneBeep.emergency()
            }
        }
    }
}

