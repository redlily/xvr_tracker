import SwiftUI

struct PermissionSetupPage: View {
    var body: some View {
        GeometryReader { proxy in
            ScrollView(.vertical) {
                VStack {
                    Spacer()
                    
                    VStack(spacing: 16) {
                        Text(localized: "onboarding_permission_setup_title")
                            .font(.title)
                            .fontWeight(.bold)
                            .multilineTextAlignment(.center)
                        
                        Text(localized: "onboarding_permission_setup_message_ios")
                            .font(.body)
                            .multilineTextAlignment(.center)
                    }
                    
                    Spacer()
                        .frame(height: 32)
                    
                    Image("onboarding_permission_setup_ios")
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .frame(maxHeight: 180)
                        .padding(.horizontal, 16)
                    
                    Spacer()
                        .frame(height: 32)
                    
                    VStack(alignment: .leading, spacing: 12) {
                        HStack(alignment: .top) {
                            Text("1.")
                                .fontWeight(.bold)
                                .frame(width: 20, alignment: .leading)
                            Text(localized: "onboarding_permission_camera_access")
                                .fixedSize(horizontal: false, vertical: true)
                        }
                        
                        HStack(alignment: .top) {
                            Text("2.")
                                .fontWeight(.bold)
                                .frame(width: 20, alignment: .leading)
                            Text(localized: "onboarding_permission_local_network_access_ios")
                                .fixedSize(horizontal: false, vertical: true)
                        }
                    }
                    .font(.body)
                    .padding(.horizontal, 16)
                    
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
    PermissionSetupPage()
}

