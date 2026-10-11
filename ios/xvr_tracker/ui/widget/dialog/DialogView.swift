import Foundation
import SwiftUICore

struct DialogView<Content: View>: View {
    let onDissmissRequest: () -> Void
    @ViewBuilder let content: () -> Content
    @Binding var isPresented: Bool
    
    var body: some View {
        VStack {
            content()
        }
        .background(Color(.systemBackground))
        .cornerRadius(8)
        .padding(32)
        .onTapGesture {}
    }
}
