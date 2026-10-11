import Foundation

extension String {
    public func ifEmpty(_ value: String) -> String {
        self.isEmpty ? value : self
    }
}
