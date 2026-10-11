import SwiftUI

struct TopContent: View {
    let onVrcConnectionSettingsClick: () -> Void
    let onBodyMeasurementsSettingsClick: () -> Void
    let onPoseAnalyzerSettingsClick: () -> Void
    let onPoseTrackerSettingsClick: () -> Void
    let onPoseTrackerAdvancedSettingsClick: () -> Void
    let onDebugSettingsClick: () -> Void
    let onOssLicensesClick: () -> Void
    let onOnboardingClick: () -> Void
    let onLaunchCameraTrackerClick: () -> Void
    
    private var appVersion: String {
        Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String ?? "0.0.0"
    }

    var body: some View {
        VStack(spacing: 0) {
            ScrollView {
                VStack(spacing: 0) {
                    SettingTitle(title: String(localized: "settings_top_game"))

                    SettingItem(
                        title: String(localized: "settings_top_vrc_connection"),
                        description: String(
                            localized: "settings_top_vrc_connection_description"
                        )
                    )
                    .onClick(onVrcConnectionSettingsClick)

                    SettingDivider()

                    SettingTitle(
                        title: String(localized: "settings_top_analysis"))

                    SettingItem(
                        title: String(
                            localized: "settings_top_body_measurements"),
                        description: String(
                            localized:
                                "settings_top_body_measurements_description")
                    )
                    .onClick(onBodyMeasurementsSettingsClick)

                    SettingItem(
                        title: String(localized: "settings_top_pose_analyzer"),
                        description: String(
                            localized: "settings_top_pose_analyzer_description")
                    )
                    .onClick(onPoseAnalyzerSettingsClick)

                    SettingDivider()

                    SettingTitle(
                        title: String(localized: "settings_top_pose_tracker"))

                    SettingItem(
                        title: String(localized: "settings_top_pose_tracker"),
                        description: String(
                            localized: "settings_top_pose_tracker_description")
                    )
                    .onClick(onPoseTrackerSettingsClick)

                    SettingItem(
                        title: String(
                            localized:
                                "settings_top_pose_tracker_advanced_settings"),
                        description: String(
                            localized:
                                "settings_top_pose_tracker_advanced_settings_description"
                        )
                    )
                    .onClick(onPoseTrackerAdvancedSettingsClick)

                    SettingDivider()

                    SettingTitle(title: String(localized: "settings_top_other"))

                    SettingItem(
                        title: String(localized: "settings_top_debug"),
                        description: String(
                            localized: "settings_top_debug_description")
                    )
                    .onClick(onDebugSettingsClick)

                    SettingItem(
                        title: String(localized: "settings_top_oss_info"),
                        description: String(
                            localized: "settings_top_oss_info_description")
                    )
                    .onClick(onOssLicensesClick)

                    SettingItem(
                        title: String(localized: "settings_top_onboarding"),
                        description: String(
                            localized: "settings_top_onboarding_description")
                    )
                    .onClick(onOnboardingClick)

                    SettingItem(
                        title: String(localized: "settings_top_version"),
                        description: appVersion
                    )
                }
            }
            .contentMargins(.bottom, 32, for: .scrollContent)

            VStack {
                Button(action: onLaunchCameraTrackerClick) {
                    Text(localized: "settings_top_launch_tracker")
                        .font(.body)
                        .frame(maxWidth: .infinity)
                        .padding(8)
                }
                .buttonStyle(.borderedProminent)
                .buttonBorderShape(.capsule)
                .padding(.horizontal, 16)
                .padding(.vertical, 8)
            }
        }
    }
}

#Preview {
    TopContent(
        onVrcConnectionSettingsClick: {},
        onBodyMeasurementsSettingsClick: {},
        onPoseAnalyzerSettingsClick: {},
        onPoseTrackerSettingsClick: {},
        onPoseTrackerAdvancedSettingsClick: {},
        onDebugSettingsClick: {},
        onOssLicensesClick: {},
        onOnboardingClick: {},
        onLaunchCameraTrackerClick: {}
    )
}
