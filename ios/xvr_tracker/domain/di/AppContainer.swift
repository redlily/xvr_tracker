import Foundation
import Swinject
import di
import data
import common

extension AppContainer {
    public func registerDomainDependencies() {
        registerLogic()
        registerService()
        registerWorkFlow()
        registerUseCase()
        registerUtil()
    }
    
    private func registerLogic() {
        register { resolver in
            PoseLandmarkModifier(
                poseLandmarkerSettingsRepository: resolver.resolve(),
                poseModificationSettingsRepository: resolver.resolve(),
                bodyMeasurementsSettingsRepository: resolver.resolve(),
                toneBeep: resolver.resolve()
            )
        }.inObjectScope(.container)
        
        register { _ in
            PoseDirectionAnalyzer()
        }.inObjectScope(.container)
        
        register { resolver in
            PoseJointAnalyzer(
                bodyMeasurementsSettingsRepository: resolver.resolve()
            )
        }.inObjectScope(.container)
        
        register { resolver in
            PoseTrackerCalculator(
                poseDirectionAnalyzer: resolver.resolve(),
                poseJointAnalyzer: resolver.resolve(),
                poseTrackerSettingsRepository: resolver.resolve()
            )
        }.inObjectScope(.container)
    }
    
    private func registerService() {
        register { resolver in
            return SimpleOscClient()
        }.inObjectScope(.container)
        
        register { resolver in
            return VrcClientService(
                vrcConnectionSettingsRepository: resolver.resolve(),
                simpleOscClient: resolver.resolve()
            )
        }
        
        register { resolver in
            return ImageTrackerService(
                poseLandmarkerSettingsRepository: resolver.resolve(),
                accelerationDataSource: resolver.resolve(),
                poseLandmarkerDataSource: resolver.resolve(),
                poseLandmarkModifier: resolver.resolve(),
                poseTrackerCalculator: resolver.resolve(),
                vrcClientService: resolver.resolve()
            )
        }
        
        register { resolver in
            return PlotAnalyzedDataServerService(
                analyzedDataPlotServerSettingsRepository: resolver.resolve()
            )
        }
    }
    
    private func registerWorkFlow() {
        register { register in
            return TrackingDataNetworkHealthCheckWorkflow(
                vrcConnectionSettingsRepository: register.resolve(),
                getPrivateNetworkRoutesUseCase: register.resolve()
            )
        }
    }
    
    private func registerUseCase() {
        register { resolver in
            return GetPrivateNetworkRoutesUseCase(
                networkStateRepository: resolver.resolve()
            )
        }
        
        register { resolver in
            return GetPoseLandmarkerSettingsConsumerUseCase(
                poseLandmarkerSettingsRepository: resolver.resolve()
            )
        }
        
        register { resolver in
            return GetPoseModificationSettingsConsumerUseCase(
                poseModificationSettingsRepository: resolver.resolve()
            )
        }
        
        register { resolver in
            return GetPoseTrackerSettingsConsumerUseCase(
                poseTrackerSettingsRepository: resolver.resolve()
            )
        }
        
        register { resolver in
            return GetVrcConnectionSettingsConsumerUseCase(
                vrcConnectionSettingsRepository: resolver.resolve()
            )
        }
    }
    
    private func registerUtil() {
        register { register in
            return ToneBeep()
        }
    }
}
