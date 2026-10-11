import SwiftUI
import data

struct CameraTrackerContent: View {
    let uiState: CameraTrackerUiState
    let cameraState: CameraState?
    let isTransiting: Bool
    let replacementImage: UIImage?
    let onBackNavigate: () -> Void
    let onCameraSwitch: () -> Void
    let onZoomChange: (Double) -> Void
    let onPrivacyAndSecurityOpen: () -> Void
    let onDialogClose: () -> Void
    
    var body: some View {
        ZStack {
            Color.black
                .ignoresSafeArea(.all)
            
            if !isTransiting {
                if let replacementImage = replacementImage {
                    GeometryReader { geometry in
                        Image(uiImage: replacementImage)
                            .resizable()
                            .aspectRatio(contentMode: .fill)
                            .frame(width: geometry.size.width, height: geometry.size.height)
                            .clipped()
                    }
                } else if let cameraState,
                          let device = cameraState.device,
                          let session = cameraState.session {
                    CameraPreview(
                        device: device,
                        session: session,
                        orientation: cameraState.orientation
                    )
                }
            }
            
            if case .cameraActive(let content) = uiState.contentState, let cameraState {
                PlotPoseDataView(
                    orientation: cameraState.orientation != .unknown ? cameraState.orientation : .portrait,
                    flip: uiState.lensFacingType == .front,
                    poseLandmarksData: content.poseLandmarksData,
                    poseTrackersData: content.poseTrackersData,
                    isDrawLandmarksDataEnabled: true,
                    isDrawTrackersDataEnabled: true,
                    visibilityThreshold: content.visibilityThreshold,
                    presenceThreshold: content.presenceThreshold
                )
            }
            
            VStack {
                HStack {
                    Button(action: onBackNavigate) {
                        VStack(spacing: 4) {
                            Image(systemName: "xmark")
                                .font(.title2)
                                .foregroundColor(.white)
                            Text(String(localized: "back"))
                                .font(.caption)
                                .foregroundColor(.white)
                        }
                        .frame(width: 60, height: 60)
                        .background(Color.black.opacity(0.5))
                        .clipShape(Circle())
                    }
                    .padding(.leading, 16)
                    .padding(.top, 16)
                    
                    Spacer()
                    
                    /*
                     if case .cameraActive = uiState.contentState {
                     Button(action: onCameraSwitch) {
                     VStack(spacing: 4) {
                     Image(systemName: "camera.rotate")
                     .font(.title2)
                     .foregroundColor(.white)
                     Text(String(localized: "camera_tracker_switch"))
                     .font(.caption)
                     .foregroundColor(.white)
                     }
                     .frame(width: 60, height: 60)
                     .background(Color.black.opacity(0.5))
                     .clipShape(Circle())
                     }
                     .padding(.trailing, 16)
                     .padding(.top, 16)
                     }
                     */
                }
                
                Spacer()
                
                if case .cameraActive = uiState.contentState,
                   let cameraState,
                   cameraState.zoomRatioRange.lowerBound < cameraState.zoomRatioRange.upperBound
                {
                    ZoomController(
                        cameraState: cameraState,
                        maxZoomRatio: uiState.maxZoomRatio,
                        onZoomChange: onZoomChange
                    )
                }
            }
        }
        .alertDialoga(
            onDismissRequest: {},
            title: {
                dialogTitle
            },
            message: {
                dialogMessage
            },
            actions: {
                dialogActions
            },
            isPresented: Binding(
                get: { uiState.dialogState != nil },
                set: { _ in }
            )
        )
    }
    
    @ViewBuilder
    private var dialogTitle: some View {
        switch uiState.dialogState {
        case .cameraPermissionDenied:
            VStack(spacing: 16) {
                Image(systemName: "video.slash")
                
                Text(localized: "camera_tracker_no_camera_permission_dialog_title")
            }
        case .privateNetworkNotConnected:
            VStack(spacing: 16) {
                Image(systemName: "house.slash")
                
                Text(localized: "camera_tracker_private_network_not_connected_dialog_title")
            }
        case .destinationAddressResolutionFailed:
            VStack(spacing: 16) {
                Image(systemName: "icloud.slash")
                
                Text(localized: "camera_tracker_destination_address_resolution_failed_dialog_title")
            }
        case .globalAddressSpecified:
            VStack(spacing: 16) {
                Image(systemName: "exclamationmark.triangle")
                
                Text(localized: "camera_tracker_external_host_specified_dialog_title")
            }
        case .localNetworkDenied:
            VStack(spacing: 16) {
                Image(systemName: "network.slash")
                
                Text(localized: "camera_tracker_local_network_access_not_permitted_dialog_title_ios")
            }
        case .unknownNetworkError:
            VStack(spacing: 16) {
                Image(systemName: "network.slash")
                
                Text(localized: "camera_tracker_unknown_network_error_dialog_title")
            }
        default:
            EmptyView()
        }
    }
    
    @ViewBuilder
    private var dialogMessage: some View {
        switch uiState.dialogState {
        case .cameraPermissionDenied:
            VStack(spacing: 32) {
                Text(localized: "camera_tracker_no_camera_permission_dialog_description_ios")
                
                Button(action: {
                    onPrivacyAndSecurityOpen()
                }, label: {
                    Text(localized: "open_privacy_and_security_ios")
                        .frame(maxWidth: .infinity)
                })
            }
        case .privateNetworkNotConnected:
            Text(localized: "camera_tracker_private_network_not_connected_dialog_description")
        case .destinationAddressResolutionFailed:
            Text(localized: "camera_tracker_destination_address_resolution_failed_dialog_description")
        case .globalAddressSpecified:
            Text(localized: "camera_tracker_external_host_specified_dialog_description")
        case .localNetworkDenied:
            VStack(spacing: 32) {
                Text(localized: "camera_tracker_local_network_access_not_permitted_dialog_description_ios")
                
                Button(action: {
                    onPrivacyAndSecurityOpen()
                }, label: {
                    Text(localized: "open_privacy_and_security_ios")
                        .frame(maxWidth: .infinity)
                })
            }
        case .unknownNetworkError:
            VStack(spacing: 32) {
                Text(localized: "camera_tracker_unknown_network_error_dialog_description_ios")
                
                Button(action: {
                    onPrivacyAndSecurityOpen()
                }, label: {
                    Text(localized: "open_privacy_and_security_ios")
                        .frame(maxWidth: .infinity)
                })
            }
        default:
            EmptyView()
        }
    }
    
    @ViewBuilder
    private var dialogActions: some View {
        switch uiState.dialogState {
        default:
            HStack {
                Button(action: {
                    onDialogClose()
                }, label: {
                    Text(localized: "confirm")
                        .frame(maxWidth: .infinity)
                })
            }
        }
    }
}

private struct ZoomController: View {
    let cameraState: CameraState
    let maxZoomRatio: Double
    let onZoomChange: (Double) -> Void
    
    @State private var modifiedZoomRatio: Double
    
    init(cameraState: CameraState, maxZoomRatio: Double, onZoomChange: @escaping (Double) -> Void) {
        self.cameraState = cameraState
        self.maxZoomRatio = maxZoomRatio
        self.onZoomChange = onZoomChange
        let cappedZoom = min(cameraState.zoomRatio, maxZoomRatio)
        self._modifiedZoomRatio = State(initialValue: log10(cappedZoom))
    }
    
    private var modifiedZoomRatioRange: ClosedRange<Double> {
        let lower = cameraState.zoomRatioRange.lowerBound
        let upper = min(cameraState.zoomRatioRange.upperBound, maxZoomRatio)
        return log10(lower)...log10(upper)
    }
    
    var body: some View {
        VStack(spacing: 8) {
            Text(String(format: "x %.1f", pow(10, modifiedZoomRatio)))
                .font(.body)
                .foregroundColor(.white)
                .padding(.horizontal, 16)
                .padding(.vertical, 4)
                .background(Color.black.opacity(0.6))
                .clipShape(RoundedRectangle(cornerRadius: 8))
            
            Slider(
                value: $modifiedZoomRatio,
                in: modifiedZoomRatioRange
            ) {
                Text("")
            } minimumValueLabel: {
                Text("")
            } maximumValueLabel: {
                Text("")
            }
            .onChange(of: modifiedZoomRatio) { oldValue, newValue in
                let zoom = min(pow(10, newValue), maxZoomRatio)
                onZoomChange(zoom)
            }
            .onChange(of: cameraState.zoomRatio) { oldValue, newValue in
                let cappedZoom = min(newValue, maxZoomRatio)
                modifiedZoomRatio = log10(cappedZoom)
            }
        }
        .padding(.horizontal, 32)
        .padding(.top, 16)
        .padding(.bottom, 32)
    }
}

#Preview {
    CameraTrackerContent(
        uiState: CameraTrackerUiState(),
        cameraState: CameraState(),
        isTransiting: false,
        replacementImage: nil,
        onBackNavigate: {},
        onCameraSwitch: {},
        onZoomChange: { _ in },
        onPrivacyAndSecurityOpen: {},
        onDialogClose: {}
    )
}
