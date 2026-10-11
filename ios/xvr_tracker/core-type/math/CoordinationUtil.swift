import Foundation

public extension Vector3 {
    func mediaPipeToUnity() -> Vector3 {
        var copy = self
        copy.x = -copy.x
        copy.y = -copy.y
        copy.z = -copy.z
        return copy
    }
    
    func unityToScreen() -> Vector3 {
        var copy = self
        copy.x = -copy.x
        copy.y = -copy.y
        return copy
    }
    
    func screenToUnity() -> Vector3 {
        var copy = self
        copy.x = -copy.x
        copy.y = -copy.y
        return copy
    }
    
    func sensorToUnity() -> Vector3 {
        var copy = self
        copy.y = -copy.y
        copy.z = -copy.z
        return copy
    }
}
