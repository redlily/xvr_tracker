import SwiftUI

struct WelcomePage: View {
    var body: some View {
        GeometryReader { proxy in
            ScrollView(.vertical) {
                VStack {
                    Spacer()
                    
                    VStack(spacing: 16) {
                        Text(localized: "onboarding_welcome_title")
                            .font(.title)
                            .fontWeight(.bold)
                            .multilineTextAlignment(.center)
                        
                        Text(localized: "onboarding_welcome_subtitle")
                            .font(.subheadline)
                            .multilineTextAlignment(.center)
                    }
                    
                    Spacer()
                        .frame(height: 64)
                    
                    Image("ic_app_logo")
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .frame(maxHeight: 180)
                    
                    Spacer()
                        .frame(height: 64)
                    
                    Text(localized: "onboarding_welcome_body")
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
    WelcomePage()
}
