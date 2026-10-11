import SwiftUI
import data

struct PoseTrackerSettingsContent: View {
    let uiState: PoseTrackerSettingsUiState
    let onHeadTrackerDataTypeChange: (PoseTrackerMotionType) -> Void
    let onChestTrackerDataTypeChange: (PoseTrackerMotionType) -> Void
    let onWaistTrackerDataTypeChange: (PoseTrackerMotionType) -> Void
    let onElbowTrackerDataTypeChange: (PoseTrackerMotionType) -> Void
    let onKneeTrackerDataTypeChange: (PoseTrackerMotionType) -> Void
    let onFeetTrackerDataTypeChange: (PoseTrackerMotionType) -> Void
    let onPresetTrackingSettingsChose: (PresetPoseTrackerSettings?) -> Void
    
    private let selectableTrackerDataTypes = [
        PoseTrackerMotionType.disable,
        PoseTrackerMotionType.position,
        PoseTrackerMotionType.positionAndRotation
    ]
    
    var body: some View {
        ScrollView {
            VStack(spacing: 0) {
                SettingTitle(
                    title: String(localized: "pose_tracker_settings_tracker_data_type"),
                    descriptionText: String(localized: "pose_tracker_settings_tracker_data_type_description")
                )
                
                ChoiceItem(
                    title: String(localized: "pose_tracker_settings_head_tracker"),
                    items: selectableTrackerDataTypes,
                    selectedItem: uiState.poseTrackerSettings.head,
                    itemString: { motionType in
                        switch motionType {
                        case .disable:
                            return String(localized: "pose_tracker_settings_tracker_data_type_disable")
                        case .position:
                            return String(localized: "pose_tracker_settings_tracker_data_type_position")
                        case .rotation:
                            return String(localized: "pose_tracker_settings_tracker_data_type_rotation")
                        case .positionAndRotation:
                            return String(localized: "pose_tracker_settings_tracker_data_type_position_rotation")
                        @unknown default:
                            return "Unknown"
                        }
                    },
                    onValueApplyRequest: onHeadTrackerDataTypeChange
                )
                
                ChoiceItem(
                    title: String(localized: "pose_tracker_settings_chest_tracker"),
                    items: selectableTrackerDataTypes,
                    selectedItem: uiState.poseTrackerSettings.chest,
                    itemString: { motionType in
                        switch motionType {
                        case .disable:
                            return String(localized: "pose_tracker_settings_tracker_data_type_disable")
                        case .position:
                            return String(localized: "pose_tracker_settings_tracker_data_type_position")
                        case .rotation:
                            return String(localized: "pose_tracker_settings_tracker_data_type_rotation")
                        case .positionAndRotation:
                            return String(localized: "pose_tracker_settings_tracker_data_type_position_rotation")
                        @unknown default:
                            return "Unknown"
                        }
                    },
                    onValueApplyRequest: onChestTrackerDataTypeChange
                )
                
                ChoiceItem(
                    title: String(localized: "pose_tracker_settings_hip_tracker"),
                    items: selectableTrackerDataTypes,
                    selectedItem: uiState.poseTrackerSettings.waist,
                    itemString: { motionType in
                        switch motionType {
                        case .disable:
                            return String(localized: "pose_tracker_settings_tracker_data_type_disable")
                        case .position:
                            return String(localized: "pose_tracker_settings_tracker_data_type_position")
                        case .rotation:
                            return String(localized: "pose_tracker_settings_tracker_data_type_rotation")
                        case .positionAndRotation:
                            return String(localized: "pose_tracker_settings_tracker_data_type_position_rotation")
                        @unknown default:
                            return "Unknown"
                        }
                    },
                    onValueApplyRequest: onWaistTrackerDataTypeChange
                )
                
                ChoiceItem(
                    title: String(localized: "pose_tracker_settings_elbow_tracker"),
                    items: selectableTrackerDataTypes,
                    selectedItem: uiState.poseTrackerSettings.elbow,
                    itemString: { motionType in
                        switch motionType {
                        case .disable:
                            return String(localized: "pose_tracker_settings_tracker_data_type_disable")
                        case .position:
                            return String(localized: "pose_tracker_settings_tracker_data_type_position")
                        case .rotation:
                            return String(localized: "pose_tracker_settings_tracker_data_type_rotation")
                        case .positionAndRotation:
                            return String(localized: "pose_tracker_settings_tracker_data_type_position_rotation")
                        @unknown default:
                            return "Unknown"
                        }
                    },
                    onValueApplyRequest: onElbowTrackerDataTypeChange
                )
                
                ChoiceItem(
                    title: String(localized: "pose_tracker_settings_knee_tracker"),
                    items: selectableTrackerDataTypes,
                    selectedItem: uiState.poseTrackerSettings.knee,
                    itemString: { motionType in
                        switch motionType {
                        case .disable:
                            return String(localized: "pose_tracker_settings_tracker_data_type_disable")
                        case .position:
                            return String(localized: "pose_tracker_settings_tracker_data_type_position")
                        case .rotation:
                            return String(localized: "pose_tracker_settings_tracker_data_type_rotation")
                        case .positionAndRotation:
                            return String(localized: "pose_tracker_settings_tracker_data_type_position_rotation")
                        @unknown default:
                            return "Unknown"
                        }
                    },
                    onValueApplyRequest: onKneeTrackerDataTypeChange
                )
                
                ChoiceItem(
                    title: String(localized: "pose_tracker_settings_feet_tracker"),
                    items: selectableTrackerDataTypes,
                    selectedItem: uiState.poseTrackerSettings.feet,
                    itemString: { motionType in
                        switch motionType {
                        case .disable:
                            return String(localized: "pose_tracker_settings_tracker_data_type_disable")
                        case .position:
                            return String(localized: "pose_tracker_settings_tracker_data_type_position")
                        case .rotation:
                            return String(localized: "pose_tracker_settings_tracker_data_type_rotation")
                        case .positionAndRotation:
                            return String(localized: "pose_tracker_settings_tracker_data_type_position_rotation")
                        @unknown default:
                            return "Unknown"
                        }
                    },
                    onValueApplyRequest: onFeetTrackerDataTypeChange
                )
                
                SettingDivider()
                
                SettingTitle(title: String(localized: "pose_tracker_settings_other"))
                
                ChoiceItem(
                    title: String(localized: "pose_tracker_settings_preset"),
                    description: String(localized: "pose_tracker_settings_preset_description"),
                    items: [
                        PresetPoseTrackerSettings.point7,
                        PresetPoseTrackerSettings.point9,
                        PresetPoseTrackerSettings.point10
                    ],
                    selectedItem: PresetPoseTrackerSettings.point7,
                    itemString: { preset in
                        switch preset {
                        case .point7:
                            return String(localized: "pose_tracker_settings_preset_7_point")
                        case .point9:
                            return String(localized: "pose_tracker_settings_preset_9_point")
                        case .point10:
                            return String(localized: "pose_tracker_settings_preset_11_point")
                        default:
                            return ""
                        }
                    },
                    onValueApplyRequest: onPresetTrackingSettingsChose,
                    showValue: false
                )
            }
        }
        .contentMargins(.bottom, 32, for: .scrollContent)
        .navigationTitle(String(localized: "pose_tracker_settings_title"))
        .navigationBarTitleDisplayMode(.inline)
    }
}

#Preview {
    PoseTrackerSettingsContent(
        uiState: PoseTrackerSettingsUiState(
            poseTrackerSettings: PoseTrackerSettings.default
        ),
        onHeadTrackerDataTypeChange: { _ in },
        onChestTrackerDataTypeChange: { _ in },
        onWaistTrackerDataTypeChange: { _ in },
        onElbowTrackerDataTypeChange: { _ in },
        onKneeTrackerDataTypeChange: { _ in },
        onFeetTrackerDataTypeChange: { _ in },
        onPresetTrackingSettingsChose: { _ in }
    )
}
