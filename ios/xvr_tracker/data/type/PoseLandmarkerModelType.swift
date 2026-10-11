import Foundation

public enum PoseLandmarkerModelType: Int, CaseIterable, Sendable {
    case lite = 0
    case standard = 1
    
    static func fromId(_ id: Int) -> PoseLandmarkerModelType {
        return PoseLandmarkerModelType(rawValue: id) ?? .standard
    }
}

extension PoseLandmarkerModelType {
    var modelFilePath: String {
        switch self {
        case .lite:
            return Bundle.main.path(
                forResource: "pose_landmarker_lite",
                ofType: "task",
                inDirectory: "pose_landmarker"
            )!
        case .standard:
            return Bundle.main.path(
                forResource: "pose_landmarker_full",
                ofType: "task",
                inDirectory: "pose_landmarker"
            )!
        }
    }
}

extension Int {
    func toPoseLandmarkerModelType() -> PoseLandmarkerModelType {
        return PoseLandmarkerModelType.fromId(self)
    }
}
