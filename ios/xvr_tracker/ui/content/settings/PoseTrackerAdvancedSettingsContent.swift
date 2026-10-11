import SwiftUI
import common
import data

struct PoseTrackerAdvancedSettingsContent: View {
    let uiState: PoseTrackerAdvancedSettingsUiState
    let onStandStabilizingCoefficientChange: (Double) -> Void
    let onSitStabilizingCoefficientChange: (Double) -> Void
    let onLieDownStabilizingCoefficientChange: (Double) -> Void
    let onStabilizingModeChangeThresholdTimeChange: (Int) -> Void
    let onStabilizingModeChangeSoundEnabledChange: (Bool) -> Void
    let onBodyPositiveSlopeChange: (Double) -> Void
    let onBodyNegativeSlopeChange: (Double) -> Void
    let onFootPositiveSlopeChange: (Double) -> Void
    let onFootNegativeSlopeChange: (Double) -> Void
    let onKneePositiveSlopeChange: (Double) -> Void
    let onKneeNegativeSlopeChange: (Double) -> Void
    
    var body: some View {
        ScrollView {
            VStack(spacing: 0) {
                SettingTitle(
                    title: String(localized: "pose_tracker_advanced_settings_stabilizing_coefficients"),
                    descriptionText: String(localized: "pose_tracker_advanced_settings_stabilizing_coefficients_description")
                )
                
                SliderItem(
                    title: String(localized: "pose_tracker_advanced_settings_stand_stabilizing_coefficient"),
                    value: uiState.poseModificationSettings.standStabilizingCoefficient.value,
                    range: PoseModificationSettings.stabilizingCoefficientRange,
                    valueString: { String(format: "%.2f", $0) },
                    onValueApplyRequest: onStandStabilizingCoefficientChange
                )
                
                SliderItem(
                    title: String(localized: "pose_tracker_advanced_settings_sit_stabilizing_coefficient"),
                    value: uiState.poseModificationSettings.sitStabilizingCoefficient.value,
                    range: PoseModificationSettings.stabilizingCoefficientRange,
                    valueString: { String(format: "%.2f", $0) },
                    onValueApplyRequest: onSitStabilizingCoefficientChange
                )
                
                SliderItem(
                    title: String(localized: "pose_tracker_advanced_settings_lie_down_stabilizing_coefficient"),
                    value: uiState.poseModificationSettings.lieDownStabilizingCoefficient.value,
                    range: PoseModificationSettings.stabilizingCoefficientRange,
                    valueString: { String(format: "%.2f", $0) },
                    onValueApplyRequest: onLieDownStabilizingCoefficientChange
                )
                
                SettingDivider()
                
                SettingTitle(
                    title: String(localized: "pose_tracker_advanced_settings_mode_change_settings"),
                    descriptionText: String(localized: "pose_tracker_advanced_settings_mode_change_settings_description")
                )
                
                SliderItem(
                    title: String(localized: "pose_tracker_advanced_settings_stabilizing_mode_change_threshold_time"),
                    value: Double(uiState.poseModificationSettings.stabilizingModeChangeThresholdTime.value),
                    range: Double(PoseModificationSettings.stabilizingModeChangeThresholdTimeRange.lowerBound)...Double(PoseModificationSettings.stabilizingModeChangeThresholdTimeRange.upperBound),
                    valueString: { "\(Int($0))ms" },
                    onValueApplyRequest: { value in
                        onStabilizingModeChangeThresholdTimeChange(Int(value))
                    }
                )
                
                SwitchItem(
                    title: String(localized: "pose_tracker_advanced_settings_stabilizing_mode_change_sound"),
                    description: String(localized: "pose_tracker_advanced_settings_stabilizing_mode_change_sound_description"),
                    value: uiState.poseModificationSettings.isStabilizingModeChangeSoundEnabled,
                    onChange: onStabilizingModeChangeSoundEnabledChange
                )
                
                SettingDivider()
                
                SettingTitle(
                    title: String(localized: "pose_tracker_advanced_settings_slop_settings"),
                    descriptionText: String(localized: "pose_tracker_advanced_settings_slop_settings_description")
                )
                
                SliderItem(
                    title: String(localized: "pose_tracker_advanced_settings_body_positive_slop"),
                    description: String(localized: "pose_tracker_advanced_settings_body_positive_slop_description"),
                    value: uiState.poseModificationSettings.bodyPositiveSlope.value,
                    range: PoseModificationSettings.bodySlopeRange,
                    valueString: { String(format: "%.1f°", $0) },
                    onValueApplyRequest: onBodyPositiveSlopeChange
                )
                
                SliderItem(
                    title: String(localized: "pose_tracker_advanced_settings_body_negative_slop"),
                    description: String(localized: "pose_tracker_advanced_settings_body_negative_slop_description"),
                    value: uiState.poseModificationSettings.bodyNegativeSlope.value,
                    range: PoseModificationSettings.bodySlopeRange,
                    valueString: { String(format: "%.1f°", $0) },
                    onValueApplyRequest: onBodyNegativeSlopeChange
                )
                
                SliderItem(
                    title: String(localized: "pose_tracker_advanced_settings_foot_positive_slop"),
                    description: String(localized: "pose_tracker_advanced_settings_foot_positive_slop_description"),
                    value: uiState.poseModificationSettings.footPositiveSlope.value,
                    range: PoseModificationSettings.footSlopeRange,
                    valueString: { String(format: "%.1f°", $0) },
                    onValueApplyRequest: onFootPositiveSlopeChange
                )
                
                SliderItem(
                    title: String(localized: "pose_tracker_advanced_settings_foot_negative_slop"),
                    description: String(localized: "pose_tracker_advanced_settings_foot_negative_slop_description"),
                    value: uiState.poseModificationSettings.footNegativeSlope.value,
                    range: PoseModificationSettings.footSlopeRange,
                    valueString: { String(format: "%.1f°", $0) },
                    onValueApplyRequest: onFootNegativeSlopeChange
                )
                
                SliderItem(
                    title: String(localized: "pose_tracker_advanced_settings_knee_positive_slop"),
                    description: String(localized: "pose_tracker_advanced_settings_knee_positive_slop_description"),
                    value: uiState.poseModificationSettings.kneePositiveSlope.value,
                    range: PoseModificationSettings.kneeSlopeRange,
                    valueString: { String(format: "%.1f°", $0) },
                    onValueApplyRequest: onKneePositiveSlopeChange
                )
                
                SliderItem(
                    title: String(localized: "pose_tracker_advanced_settings_knee_negative_slop"),
                    description: String(localized: "pose_tracker_advanced_settings_knee_negative_slop_description"),
                    value: uiState.poseModificationSettings.kneeNegativeSlope.value,
                    range: PoseModificationSettings.kneeSlopeRange,
                    valueString: { String(format: "%.1f°", $0) },
                    onValueApplyRequest: onKneeNegativeSlopeChange
                )
            }
        }
        .contentMargins(.bottom, 32, for: .scrollContent)
        .navigationTitle(String(localized: "settings_top_pose_tracker_advanced_settings"))
        .navigationBarTitleDisplayMode(.inline)
    }
}

#Preview {
    PoseTrackerAdvancedSettingsContent(
        uiState: PoseTrackerAdvancedSettingsUiState(
            poseModificationSettings: PoseModificationSettings.default
        ),
        onStandStabilizingCoefficientChange: { _ in },
        onSitStabilizingCoefficientChange: { _ in },
        onLieDownStabilizingCoefficientChange: { _ in },
        onStabilizingModeChangeThresholdTimeChange: { _ in },
        onStabilizingModeChangeSoundEnabledChange: { _ in },
        onBodyPositiveSlopeChange: { _ in },
        onBodyNegativeSlopeChange: { _ in },
        onFootPositiveSlopeChange: { _ in },
        onFootNegativeSlopeChange: { _ in },
        onKneePositiveSlopeChange: { _ in },
        onKneeNegativeSlopeChange: { _ in }
    )
}
