import Foundation
import core_type

public extension Matrix4x4 {
    func mediaPipeToUnity() -> Matrix4x4 {
        return Matrix4x4.scale(-1, -1, -1) * self
    }
    
    func unityToScreen() -> Matrix4x4 {
        return Matrix4x4.scale(-1, -1, 1) * self
    }
    
    func screenToUnity() -> Matrix4x4 {
        return Matrix4x4.scale(-1, -1, 1) * self
    }
    
    func sensorToUnity() -> Matrix4x4 {
        return Matrix4x4.scale(1, -1, -1) * self
    }
}
