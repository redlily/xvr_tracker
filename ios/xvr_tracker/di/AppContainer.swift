import Foundation
import Swinject

public class AppContainer: @unchecked Sendable {
    public static let shared = AppContainer()
    
    public let container = Container()
    
    private init() {}
}

extension AppContainer {
    @discardableResult
    public func register<Service>(
        name: String? = nil,
        factory: @escaping (Resolver) -> Service
    ) -> ServiceEntry<Service> {
        return container.register(Service.self, name: name, factory: factory)
    }
    
    public func resolve<Service>(name: String? = nil) -> Service {
        return container.resolve(Service.self, name: name)!
    }
}

extension Resolver {
    public func resolve<Service>() -> Service {
        return resolve(Service.self)!
    }
    
    public func resolve<Service>(name: String) -> Service {
        return resolve(Service.self, name: name)!
    }
}
