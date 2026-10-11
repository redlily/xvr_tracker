import Foundation
import di

extension AppContainer {
    public static let appSettings = "AppSettings"
    
    public func registerDataDependencies() {
        registerStorage()
        registerSettingsDataSourceDependencies()
        registerDataSourceDependencies()
        registerController()
        registerRepositoryDependencies()
    }
    
    private func registerController() {
        register { resolver in
            return CameraController(
                displayRotationDataSource: resolver.resolve()
            )
        }.inObjectScope(.weak)
    }
    
    private func registerStorage() {
        register(name: Self.appSettings) { _ in
            UserDefaults.standard
        }.inObjectScope(.container)
    }
    
    private func registerSettingsDataSourceDependencies() {
        register { resolver in
            let repository = NetworkStateRepository(dataSource: resolver.resolve())
            repository.start()
            return repository
        }
        
        register { resolver in
            return VrcConnectionSettingsDataSource(
                userDefaults: resolver.resolve(UserDefaults.self, name: Self.appSettings)!
            )
        }.inObjectScope(.container)
        
        register { resolver in
            return BodyMeasurementsSettingsDataSource(
                userDefaults: resolver.resolve(UserDefaults.self, name: Self.appSettings)!
            )
        }.inObjectScope(.container)
        
        register { resolver in
            return PoseLandmarkerSettingsDataSource(
                userDefaults: resolver.resolve(UserDefaults.self, name: Self.appSettings)!
            )
        }.inObjectScope(.container)
        
        register { resolver in
            return PoseTrackerSettingsDataSource(
                userDefaults: resolver.resolve(UserDefaults.self, name: Self.appSettings)!
            )
        }.inObjectScope(.container)
        
        register { resolver in
            return PoseModificationSettingsDataSource(
                userDefaults: resolver.resolve(UserDefaults.self, name: Self.appSettings)!
            )
        }.inObjectScope(.container)
        
        register { resolver in
            return AnalyzedDataPlotServerSettingsDataSource(
                userDefaults: resolver.resolve(UserDefaults.self, name: Self.appSettings)!
            )
        }.inObjectScope(.container)
        
        register { resolver in
            return CameraSettingsDataSource(
                userDefaults: resolver.resolve(UserDefaults.self, name: Self.appSettings)!
            )
        }.inObjectScope(.container)
        
        register { resolver in
            return OnboardingSettingDataSource(
                userDefaults: resolver.resolve(UserDefaults.self, name: Self.appSettings)!
            )
        }.inObjectScope(.container)
    }
    
    private func registerDataSourceDependencies() {
        register { resolver in
            return DisplayRotationDataSource()
        }.inObjectScope(.container)
        
        register { resolver in
            return AccelerationDataSource()
        }.inObjectScope(.container)
        
        register { resolver in
            return NetworkInterfaceDataSource()
        }.inObjectScope(.container)
        
        register { resolver in
            return PoseLandmarkerDataSource()
        }.inObjectScope(.container)
    }
    
    private func registerRepositoryDependencies() {
        register { resolver in
            return OnboardingSettingsRepository(
                dataSource: resolver.resolve(OnboardingSettingDataSource.self)!
            )
        }.inObjectScope(.container)
        
        register { resolver in
            return VrcConnectionSettingsRepository(
                dataSource: resolver.resolve(VrcConnectionSettingsDataSource.self)!
            )
        }.inObjectScope(.container)
        
        register { resolver in
            return BodyMeasurementsSettingsRepository(
                dataSource: resolver.resolve(BodyMeasurementsSettingsDataSource.self)!
            )
        }.inObjectScope(.container)
        
        register { resolver in
            return PoseLandmarkerSettingsRepository(
                dataSource: resolver.resolve(PoseLandmarkerSettingsDataSource.self)!
            )
        }.inObjectScope(.container)
        
        register { resolver in
            return PoseTrackerSettingsRepository(
                dataSource: resolver.resolve(PoseTrackerSettingsDataSource.self)!
            )
        }.inObjectScope(.container)
        
        register { resolver in
            return PoseModificationSettingsRepository(
                dataSource: resolver.resolve(PoseModificationSettingsDataSource.self)!
            )
        }.inObjectScope(.container)
        
        register { resolver in
            return AnalyzedDataPlotServerSettingsRepository(
                dataSource: resolver.resolve(AnalyzedDataPlotServerSettingsDataSource.self)!
            )
        }.inObjectScope(.container)
        
        register { resolver in
            return CameraSettingsRepository(
                dataSource: resolver.resolve(CameraSettingsDataSource.self)!
            )
        }.inObjectScope(.container)
    }
    
    public func terminate() {
        Task.detached {
            let networkInterfaceRepository: NetworkStateRepository = self.resolve()
            networkInterfaceRepository.stop()
        }
    }
}
