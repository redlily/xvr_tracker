import SwiftUI

struct CompletePage: View {
    var body: some View {
        GeometryReader { proxy in
            ScrollView(.vertical) {
                VStack {
                    Spacer()
                    
                    VStack(spacing: 16) {
                        Text(localized: "onboarding_complete_title")
                            .font(.title)
                            .fontWeight(.bold)
                            .multilineTextAlignment(.center)
                        
                        Text(localized: "onboarding_complete_subtitle")
                            .font(.subheadline)
                            .multilineTextAlignment(.center)
                    }
                    
                    Spacer()
                        .frame(height: 32)
                    
                    Image("cracker")
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .frame(width: 256, height: 256)
                    
                    Spacer()
                        .frame(height: 32)
                    
                    Text(localized: "onboarding_complete_body")
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
    CompletePage()
}
