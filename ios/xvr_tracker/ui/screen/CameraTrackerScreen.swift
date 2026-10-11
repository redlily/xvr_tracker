import SwiftUI
import AVFoundation

struct CameraTrackerScreen: View {
    @StateObject var viewModel = CameraTrackerViewModel()
    let isTransiting: Bool
    let onBackNavigate: () -> Void
    
    @Environment(\.scenePhase) private var scenePhase
    
    var body: some View {
        CameraTrackerContent(
            uiState: viewModel.uiState,
            cameraState: viewModel.cameraState,
            isTransiting: isTransiting,
            replacementImage: viewModel.replacementImage,
            onBackNavigate: onBackNavigate,
            onCameraSwitch: viewModel.onCameraSwitch,
            onZoomChange: viewModel.onZoomChange,
            onPrivacyAndSecurityOpen: {
                if let url = URL(string: UIApplication.openSettingsURLString) {
                    if UIApplication.shared.canOpenURL(url) {
                        UIApplication.shared.open(url, options: [:], completionHandler: nil)
                    }
                }
            },
            onDialogClose: viewModel.onDialogClose
        )
        .onAppear {
            UIApplication.shared.isIdleTimerDisabled = true
            onAppear()
        }
        .onDisappear {
            viewModel.onStop()
            UIApplication.shared.isIdleTimerDisabled = false
        }
        .onChange(of: scenePhase) { old, new in
            switch new {
            case .active:
                viewModel.onActive()
            case .inactive:
                viewModel.onInactive()
            default:
                break
            }
        }
    }
    
    private func onAppear() {
        viewModel.onStart()
        
        switch AVCaptureDevice.authorizationStatus(for: .video) {
        case .authorized:
            viewModel.onCameraPermissionGranted()
        case .denied, .restricted:
            viewModel.onCameraPermissionRejected()
        case .notDetermined:
            Task { @MainActor in
                if await AVCaptureDevice.requestAccess(for: .video) {
                    viewModel.onCameraPermissionGranted()
                } else {
                    viewModel.onCameraPermissionRejected()
                }
            }
        @unknown default:
            break
        }
    }
}

#Preview {
    CameraTrackerScreen(
        isTransiting: false,
        onBackNavigate: {}
    )
}
