import Foundation

extension AsyncStream.Continuation.BufferingPolicy: @unchecked @retroactive Sendable where Element: Sendable {}
