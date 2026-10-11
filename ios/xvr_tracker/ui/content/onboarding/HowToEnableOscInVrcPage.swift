import SwiftUI

struct HowToEnableOscInVrcPage: View {
    var body: some View {
        GeometryReader { proxy in
            ScrollView(.vertical) {
                VStack {
                    Spacer()
                    
                    VStack(spacing: 16) {
                        Text(localized: "onboarding_how_to_enable_osc_title")
                            .font(.title)
                            .fontWeight(.bold)
                            .multilineTextAlignment(.center)
                        
                        Text(localized: "onboarding_how_to_enable_osc_subtitle")
                            .font(.subheadline)
                            .multilineTextAlignment(.center)
                    }
                    
                    Spacer()
                        .frame(height: 32)
                    
                    Image("onboarding_osc_enable")
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .frame(maxHeight: 180)
                    
                    Spacer()
                        .frame(height: 32)
                    
                    VStack(alignment: .leading, spacing: 12) {
                        HStack(alignment: .top) {
                            Text("1.")
                                .fontWeight(.bold)
                                .frame(width: 20, alignment: .leading)
                            Text(localized: "onboarding_how_to_enable_osc_body_1")
                                .fixedSize(horizontal: false, vertical: true)
                        }
                        
                        HStack(alignment: .top) {
                            Text("2.")
                                .fontWeight(.bold)
                                .frame(width: 20, alignment: .leading)
                            Text(localized: "onboarding_how_to_enable_osc_body_2")
                                .fixedSize(horizontal: false, vertical: true)
                        }
                        
                        HStack(alignment: .top) {
                            Text("3.")
                                .fontWeight(.bold)
                                .frame(width: 20, alignment: .leading)
                            Text(localized: "onboarding_how_to_enable_osc_body_3")
                                .fixedSize(horizontal: false, vertical: true)
                        }
                        
                        HStack(alignment: .top) {
                            Text("4.")
                                .fontWeight(.bold)
                                .frame(width: 20, alignment: .leading)
                            Text(localized: "onboarding_how_to_enable_osc_body_4")
                                .fixedSize(horizontal: false, vertical: true)
                        }
                        
                        HStack(alignment: .top) {
                            Text("5.")
                                .fontWeight(.bold)
                                .frame(width: 20, alignment: .leading)
                            Text(localized: "onboarding_how_to_enable_osc_body_5")
                                .fixedSize(horizontal: false, vertical: true)
                        }
                        
                        HStack(alignment: .top) {
                            Text("6.")
                                .fontWeight(.bold)
                                .frame(width: 20, alignment: .leading)
                            Text(localized: "onboarding_how_to_enable_osc_body_6")
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
    HowToEnableOscInVrcPage()
}
