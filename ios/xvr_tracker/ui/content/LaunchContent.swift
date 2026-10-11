import SwiftUI

struct LaunchContent: View {
    @State private var catchphrase: String = ""
    
    var body: some View {
        GeometryReader { geometry in
            VStack(spacing: 0) {
                Spacer()
                    .frame(maxWidth: .infinity)
                
                Image("ic_app_logo")
                    .resizable()
                    .aspectRatio(contentMode: .fit)
                    .frame(width: 128, height: 128)
                
                VStack(spacing: 0) {
                    Spacer()
                    
                    VStack(spacing: 16) {
                        Text(localized: "launch_title")
                            .font(.title)
                            .fontWeight(.semibold)
                            .foregroundColor(.primary)
                        
                        Text(catchphrase)
                            .font(.body)
                            .foregroundColor(.secondary)
                    }
                    
                    Spacer()
                        .frame(height: 72)
                }
                .frame(maxWidth: .infinity)
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .background(Color(UIColor.systemBackground))
            .ignoresSafeArea(.all)
            .onAppear {
                selectRandomCatchphrase()
            }
        }
    }
    
    private func selectRandomCatchphrase() {
        let subtitles = [
            String.LocalizationValue("launch_subtitle_1"),
            String.LocalizationValue("launch_subtitle_2")
        ]
        
        let randomIndex = Int.random(in: 0..<subtitles.count)
        catchphrase = String(localized: subtitles[randomIndex])
    }
}

#Preview {
    LaunchContent()
}
