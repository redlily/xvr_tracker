import SwiftUI

struct AskBodyHeightPage: View {
    @Binding var height: String
    @FocusState.Binding var isKeyboardVisible: Bool

    var body: some View {
        GeometryReader { proxy in
            ScrollView(.vertical) {
                VStack {
                    Spacer()
                    
                    VStack(spacing: 16) {
                        Text(localized: "onboarding_ask_user_height_title")
                            .font(.title)
                            .fontWeight(.bold)
                            .multilineTextAlignment(.center)
                        
                        Text(localized: "onboarding_ask_user_height_subtitle")
                            .font(.subheadline)
                            .multilineTextAlignment(.center)
                    }
                    
                    Spacer()
                        .frame(height: 32)
                    
                    HStack(spacing: 4) {
                        TextField("Height", text: $height)
                            .textFieldStyle(.roundedBorder)
                            .keyboardType(.numbersAndPunctuation)
                            .multilineTextAlignment(.center)
                            .frame(width: 150)
                            .focused($isKeyboardVisible)
                        Text("cm")
                            .foregroundColor(.secondary)
                    }
                    
                    Spacer()
                        .frame(height: 32)
                    
                    Text(localized: "onboarding_ask_user_height_body")
                        .font(.body)
                    
                    Spacer()
                }
                .padding()
                .frame(maxWidth: .infinity, alignment: .center)
                .frame(minHeight: proxy.size.height, alignment: .leading)
            }
            .scrollBounceBehavior(.basedOnSize)
        }
    }
}

#Preview {
//    AskUserHeightPage(height: .constant(""), isKeyboardVisible: .constant(false))
}
