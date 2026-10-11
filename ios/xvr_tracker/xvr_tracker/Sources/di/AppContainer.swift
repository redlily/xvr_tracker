import Foundation
import di
import data
import domain
import ui

extension AppContainer {
    func registerDependencies() {
        registerDataDependencies()
        registerDomainDependencies()
        registerUiDependencies()
    }
    
    func terminal() {
        
    }
}
