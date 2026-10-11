import Foundation
import SwiftUICore

struct AlertDialogView<Title: View, Message: View, Actions: View>: View {
    let onDismissRequest: () -> Void
    @ViewBuilder let title: () -> Title
    @ViewBuilder let message: () -> Message
    @ViewBuilder let actions: () -> Actions
    @Binding var isPresented: Bool
    
    var body: some View {
        DialogView(
            onDissmissRequest: onDismissRequest,
            content: {
                VStack(spacing: 32) {
                    title()
                        .font(.headline)
                    
                    message()
                        .font(.subheadline)
                    
                    actions()
                }
                .padding(.vertical, 24)
                .padding(.horizontal, 16)
                .frame(maxWidth: 480)
                .buttonStyle(.borderless)
            },
            isPresented: $isPresented
        )
    }
}
