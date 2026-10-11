import Foundation
import core_type
import infra
import common
import data
import os.lock

public final actor PoseTrackerCalculator {
    private let poseTrackerSettingsRepository: PoseTrackerSettingsRepository
    private let poseDirectionAnalyzer: PoseDirectionAnalyzer
    private let poseJointAnalyzer: PoseJointAnalyzer
    
    private let poseTrackersDataEmitter = SharedEmitter<PoseTrackersData>()
    public var poseTrackersDataConsumer: SharedConsumer<PoseTrackersData> {
        poseTrackersDataEmitter.eraseToSharedConsumer()
    }
    
    private var upper = Vector3(x: 0, y: 1, z: 0)
    private var modificationMatrix = Matrix4x4.identity
    
    public init(
        poseDirectionAnalyzer: PoseDirectionAnalyzer,
        poseJointAnalyzer: PoseJointAnalyzer,
        poseTrackerSettingsRepository: PoseTrackerSettingsRepository
    ) {
        self.poseTrackerSettingsRepository = poseTrackerSettingsRepository
        self.poseDirectionAnalyzer = poseDirectionAnalyzer
        self.poseJointAnalyzer = poseJointAnalyzer
    }
    
    public func reset() async {
        await poseDirectionAnalyzer.reset()
        poseTrackersDataEmitter.emit(.empty)
    }
    
    public func updateUpperDirection(_ direction: Vector3) {
        upper = direction.normalized()
        let cs = max(-1.0, min(1.0, upper.dot(Vector3.y)))
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
    
    public func updateLandmarksData(_ data: PoseLandmarksData) async {
        let poseTrackerSettings = poseTrackerSettingsRepository.dataConsumer.value
        
        let directionsData = await poseDirectionAnalyzer.analyze(landmarksData: data)
        let jointsData = poseJointAnalyzer.analyze(directionsData: directionsData)
        
        let trackers = jointsData.map { joints -> [PoseTrackerType: PoseTrackerData] in
            var dictionary: [PoseTrackerType: PoseTrackerData] = [:]
            if let head = joints.head {
                dictionary[.head] = PoseTrackerData(
                    dataType: poseTrackerSettings.head,
                    attitude: Matrix4x4.column(axis: head.axis, t: head.position)
                )
            }
            if let chest = joints.chest {
                dictionary[.chest] = PoseTrackerData(
                    dataType: poseTrackerSettings.chest,
                    attitude: Matrix4x4.column(axis: chest.axis, t: chest.position)
                )
            }
            if let waist = joints.waist {
                dictionary[.waist] = PoseTrackerData(
                    dataType: poseTrackerSettings.waist,
                    attitude: Matrix4x4.column(axis: waist.axis, t: waist.position)
                )
            }
            if let leftElbow = joints.leftArm.elbow {
                dictionary[.leftElbow] = PoseTrackerData(
                    dataType: poseTrackerSettings.elbow,
                    attitude: Matrix4x4.column(axis: leftElbow.axis, t: leftElbow.position)
                )
            }
            if let rightElbow = joints.rightArm.elbow {
                dictionary[.rightElbow] = PoseTrackerData(
                    dataType: poseTrackerSettings.elbow,
                    attitude: Matrix4x4.column(axis: rightElbow.axis, t: rightElbow.position)
                )
            }
            if let leftKnee = joints.leftLeg.knee {
                dictionary[.leftKnee] = PoseTrackerData(
                    dataType: poseTrackerSettings.knee,
                    attitude: Matrix4x4.column(axis: leftKnee.axis, t: leftKnee.position)
                )
            }
            if let rightKnee = joints.rightLeg.knee {
                dictionary[.rightKnee] = PoseTrackerData(
                    dataType: poseTrackerSettings.knee,
                    attitude: Matrix4x4.column(axis: rightKnee.axis, t: rightKnee.position)
                )
            }
            if let leftFeet = joints.leftLeg.feet {
                dictionary[.leftFeet] = PoseTrackerData(
                    dataType: poseTrackerSettings.feet,
                    attitude: Matrix4x4.column(axis: leftFeet.axis, t: leftFeet.position)
                )
            }
            if let rightFeet = joints.rightLeg.feet {
                dictionary[.rightFeet] = PoseTrackerData(
                    dataType: poseTrackerSettings.feet,
                    attitude: Matrix4x4.column(axis: rightFeet.axis, t: rightFeet.position)
                )
            }
            return dictionary
        }
        let trackersData = PoseTrackersData(
            trackers: trackers,
            modificationMatrix: modificationMatrix,
            timestamp: Int64(data.timestamp)
        )
        poseTrackersDataEmitter.emit(trackersData)
    }
}

