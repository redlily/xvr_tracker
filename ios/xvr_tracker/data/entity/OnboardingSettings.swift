import Foundation

public struct OnboardingSettings: Sendable, Equatable {
    public var done: Bool = false
    
    public static let `default` = OnboardingSettings()
}
