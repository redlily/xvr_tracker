import Foundation
import SwiftUICore

struct AlertDialogModifier<Title: View, Message: View, Actions: View>: ViewModifier {
    let onDismissRequest: () -> Void
    @ViewBuilder let title: () -> Title
    @ViewBuilder let message: () -> Message
    @ViewBuilder let actions: () -> Actions
    @Binding var isPresented: Bool
    
    func body(content: Content) -> some View {
        ZStack {
            content
            
            if isPresented {
                Color.black.opacity(0.4)
                    .ignoresSafeArea()
                    .onTapGesture { onDismissRequest() }
                    .transition(.opacity)
                
                AlertDialogView(
                    onDismissRequest: onDismissRequest,
                    title: title,
                    message: message,
                    actions: actions,
                    isPresented: $isPresented
                )
                .zIndex(1)
                .transition(
                    .asymmetric(
                        insertion: .scale(scale: 0.8).combined(with: .opacity),
                        removal: .scale(scale: 0.8).combined(with: .opacity)
                    )
                )
            }
        }
        .animation(
            .snappy(duration: 0.3, extraBounce: 0.2),
            value: isPresented
        )
    }
}

extension View {
    func alertDialoga<Title: View, Message: View, Actions: View>(
        onDismissRequest: @escaping () -> Void,
        @ViewBuilder title: @escaping () -> Title,
        @ViewBuilder message: @escaping () -> Message,
        @ViewBuilder actions: @escaping () -> Actions,
        isPresented: Binding<Bool>
    ) -> some View {
        modifier(
            AlertDialogModifier(
                onDismissRequest: onDismissRequest,
                title: title,
                message: message,
                actions: actions,
                isPresented: isPresented
            )
        )
    }
}
