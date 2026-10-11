import SwiftUI

extension Image {
    init(_ name: String) {
        self.init(name, bundle: Bundle.main)
    }
    
    init(_ name: String, label: Text) {
        self.init(name, bundle: Bundle.main, label: label)
    }
}
