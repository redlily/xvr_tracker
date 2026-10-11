import SwiftUI

struct AskConnectionAddressPage: View {
    @Binding var connectionAddress: String
    @FocusState.Binding var isKeyboardVisible: Bool
    
    var body: some View {
        GeometryReader { proxy in
            ScrollView(.vertical) {
                VStack {
                    Spacer()
                    
                    VStack(spacing: 16) {
                        Text(localized: "onboarding_ask_machine_ip_address_title")
                            .font(.title)
                            .fontWeight(.bold)
                            .multilineTextAlignment(.center)
                        
                        Text(localized: "onboarding_ask_machine_ip_address_subtitle")
                            .font(.subheadline)
                            .multilineTextAlignment(.center)
                    }
                    
                    Spacer()
                        .frame(height: 32)
                    
                    TextField("IP Address", text: $connectionAddress)
                        .textFieldStyle(.roundedBorder)
                        .keyboardType(.numbersAndPunctuation)
                        .autocapitalization(.none)
                        .textInputAutocapitalization(.never)
                        .autocorrectionDisabled()
                        .frame(maxWidth: 250)
                        .multilineTextAlignment(.center)
                        .focused($isKeyboardVisible)
                    
                    Spacer()
                        .frame(height: 32)
                    
                    Text(localized: "onboarding_ask_machine_ip_address_body")
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
//    AskMachineIpAddressPage(ipAddress: .constant(""), isKeyboardVisible: .constant(false))
}
