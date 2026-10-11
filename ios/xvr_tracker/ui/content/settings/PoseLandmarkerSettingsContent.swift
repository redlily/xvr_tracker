import SwiftUI
import infra
import common
import data

struct PoseLandmarkerSettingsContent: View {
    let uiState: PoseLandmarkerSettingsUiState
    let onModelTypeChange: (PoseLandmarkerModelType) -> Void
    let onComputeTypeChange: (PoseLandmarkerComputeType) -> Void
    let onVisibilityThresholdChange: (Double) -> Void
    let onPresenceThresholdChange: (Double) -> Void
    
    var body: some View {
        ScrollView {
            VStack(spacing: 0) {
                SettingTitle(title: String(localized: "pose_landmarker_settings_image_analyzer"))
                
                ChoiceItem(
                    title: String(localized: "pose_landmarker_settings_analyzer_model"),
                    description: String(localized: "pose_landmarker_settings_analyzer_model_description"),
                    items: PoseLandmarkerModelType.allCases,
                    selectedItem: uiState.poseLandmarkerSettings.modelType,
                    itemString: { modelType in
                        switch modelType {
                        case .lite:
                            return String(localized: "pose_landmarker_settings_analyzer_model_lite")
                        case .standard:
                            return String(localized: "pose_landmarker_settings_analyzer_model_standard")
                        @unknown default:
                            return "Unknown"
                        }
                    },
                    onValueApplyRequest: onModelTypeChange
                )
                
                ChoiceItem(
                    title: String(localized: "pose_landmarker_settings_compute_type"),
                    description: String(localized: "pose_landmarker_settings_compute_type_description"),
                    items: PoseLandmarkerComputeType.allCases,
                    selectedItem: uiState.poseLandmarkerSettings.computeType,
                    itemString: { computeType in
                        switch computeType {
                        case .cpu:
                            return String(localized: "pose_landmarker_settings_compute_type_cpu")
                        case .gpu:
                            return String(localized: "pose_landmarker_settings_compute_type_gpu")
                        @unknown default:
                            return "Unknown"
                        }
                    },
                    onValueApplyRequest: onComputeTypeChange
                )
                
                SliderItem(
                    title: String(localized: "pose_landmarker_settings_visibility_threshold"),
                    description: String(localized: "pose_landmarker_settings_visibility_threshold_description"),
                    value: uiState.poseLandmarkerSettings.visibilityThreshold.value,
                    range: PoseLandmarkerSettings.visibilityThresholdRange,
                    valueString: { String(format: "%.2f", $0) },
                    onValueApplyRequest: onVisibilityThresholdChange
                )
                
                SliderItem(
                    title: String(localized: "pose_landmarker_settings_presence_threshold"),
                    description: String(localized: "pose_landmarker_settings_presence_threshold_description"),
                    value: uiState.poseLandmarkerSettings.presenceThreshold.value,
                    range: PoseLandmarkerSettings.presenceThresholdRange,
                    valueString: { String(format: "%.2f", $0) },
                    onValueApplyRequest: onPresenceThresholdChange
                )
            }
        }
        .contentMargins(.bottom, 32, for: .scrollContent)
        .navigationTitle(String(localized: "settings_top_pose_analyzer"))
        .navigationBarTitleDisplayMode(.inline)
    }
}

#Preview {
    PoseLandmarkerSettingsContent(
        uiState: PoseLandmarkerSettingsUiState(
            poseLandmarkerSettings: PoseLandmarkerSettings.default
        ),
        onModelTypeChange: { _ in },
        onComputeTypeChange: { _ in },
        onVisibilityThresholdChange: { _ in },
        onPresenceThresholdChange: { _ in }
    )
}
