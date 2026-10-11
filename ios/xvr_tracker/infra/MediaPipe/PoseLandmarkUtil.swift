import Foundation
import core_type
@_implementationOnly internal import MediaPipeTasksVision

extension Landmark {
    var position: Vector3 { Vector3(x: Double(x), y: Double(y), z: Double(z)) }
}

extension NormalizedLandmark {
    var position: Vector3 { Vector3(x: Double(x), y: Double(y), z: Double(z)) }
}

extension PoseLandmarkerResult {
    func toPoseLandmarksDataList() -> [[PoseLandmarkType: PoseLandmarkData]] {
        return zip(landmarks, worldLandmarks).map { (landmark, worldLandmark) in
            zip(landmark, worldLandmark).enumerated().compactMap { (index, value) in
                let (landmark, worldLandmark) = value
                if let type = index.toPoseLandmarkType() {
                    return (type, PoseLandmarkData(
                        normalizedLandmark: landmark.position,
                        worldLandmark: worldLandmark.position.mediaPipeToUnity(),
                        visibility: Double(truncating: landmark.visibility ?? 0),
                        presence: Double(truncating: landmark.presence ?? 0)
                    ))
                } else {
                    return nil
                }
            }.reduce(into: [PoseLandmarkType: PoseLandmarkData]()) { result, value in
                let (type, data) = value
                result[type] = data
            }
        }
    }
}
