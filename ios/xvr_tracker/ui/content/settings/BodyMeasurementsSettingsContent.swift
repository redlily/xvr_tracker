import SwiftUI
import data

struct BodyMeasurementsSettingsContent: View {
    let uiState: BodyMeasurementsSettingsUiState
    let onBodyHeightChange: (Double) -> Void
    let onUpperBodyHeightRatioChange: (Double) -> Void
    let onHeadHeightRatioChange: (Double) -> Void
    let onHeadZOffsetRatioChange: (Double) -> Void
    let onNeckLengthRatioChange: (Double) -> Void
    let onShoulderWidthRatioChange: (Double) -> Void
    let onArmLengthRatioChange: (Double) -> Void
    let onChestPositionRatioChange: (Double) -> Void
    let onWaistPositionRatioChange: (Double) -> Void
    let onHipWidthRatioChange: (Double) -> Void
    let onAnkleHeightRatioChange: (Double) -> Void
    let onFootLengthRatioChange: (Double) -> Void
    let onRatioReset: () -> Void
    
    @State private var showingHeightEditor = false
    @State private var showingResetConfirmation = false
    
    var body: some View {
        ScrollView {
            VStack(spacing: 0) {
                SettingTitle(title: String(localized: "body_measurements_settings_base"))
                
                StringItem(
                    title: String(localized: "body_measurements_settings_body_height"),
                    value: getLengthAsString(uiState.bodyMeasurementsSettings.bodyHeight.value),
                    onClick: {
                        showingHeightEditor = true
                    }
                )
                
                SliderItem(
                    title: String(localized: "body_measurements_settings_upper_body_height"),
                    description: String(localized: "body_measurements_settings_upper_body_height_description"),
                    value: uiState.bodyMeasurementsSettings.upperBodyHeightRatio.value,
                    range: uiState.bodyMeasurementsSettings.upperBodyHeightRatio.range,
                    valueString: { ratio in
                        getRatioAndLengthAsString(
                            ratio: ratio,
                            value: uiState.bodyMeasurementsSettings.bodyMeasurements(
                                upperBodyHeightRatio: ratio
                            ).upperBodyHeight
                        )
                    },
                    onValueApplyRequest: onUpperBodyHeightRatioChange
                )
                
                SettingDivider()
                
                SettingTitle(title: String(localized: "body_measurements_settings_head"))
                
                SliderItem(
                    title: String(localized: "body_measurements_settings_head_height"),
                    description: String(localized: "body_measurements_settings_head_height_description"),
                    value: uiState.bodyMeasurementsSettings.headHeightRatio.value,
                    range: uiState.bodyMeasurementsSettings.headHeightRatio.range,
                    valueString: { ratio in
                        getRatioAndLengthAsString(
                            ratio: ratio,
                            value: uiState.bodyMeasurementsSettings.bodyMeasurements(
                                headHeightRatio: ratio
                            ).headHeight
                        )
                    },
                    onValueApplyRequest: onHeadHeightRatioChange
                )
                
                SliderItem(
                    title: String(localized: "body_measurements_settings_head_z_offset"),
                    description: String(localized: "body_measurements_settings_head_z_offset_description"),
                    value: uiState.bodyMeasurementsSettings.headZOffsetRatio.value,
                    range: uiState.bodyMeasurementsSettings.headZOffsetRatio.range,
                    valueString: { ratio in
                        getRatioAndLengthAsString(
                            ratio: ratio,
                            value: uiState.bodyMeasurementsSettings.bodyMeasurements(
                                headZOffsetRatio: ratio
                            ).headZOffset
                        )
                    },
                    onValueApplyRequest: onHeadZOffsetRatioChange
                )
                
                SliderItem(
                    title: String(localized: "body_measurements_settings_neck_length"),
                    description: String(localized: "body_measurements_settings_neck_length_description"),
                    value: uiState.bodyMeasurementsSettings.neckLengthRatio.value,
                    range: uiState.bodyMeasurementsSettings.neckLengthRatio.range,
                    valueString: { ratio in
                        getRatioAndLengthAsString(
                            ratio: ratio,
                            value: uiState.bodyMeasurementsSettings.bodyMeasurements(
                                neckLengthRatio: ratio
                            ).neckLength
                        )
                    },
                    onValueApplyRequest: onNeckLengthRatioChange
                )
                
                SettingDivider()
                
                SettingTitle(title: String(localized: "body_measurements_settings_body"))
                
                SliderItem(
                    title: String(localized: "body_measurements_settings_shoulder_width"),
                    description: String(localized: "body_measurements_settings_shoulder_width_description"),
                    value: uiState.bodyMeasurementsSettings.shoulderWidthRatio.value,
                    range: uiState.bodyMeasurementsSettings.shoulderWidthRatio.range,
                    valueString: { ratio in
                        getRatioAndLengthAsString(
                            ratio: ratio,
                            value: uiState.bodyMeasurementsSettings.bodyMeasurements(
                                shoulderWidthRatio: ratio
                            ).shoulderWidth
                        )
                    },
                    onValueApplyRequest: onShoulderWidthRatioChange
                )
                
                SliderItem(
                    title: String(localized: "body_measurements_settings_arm_length"),
                    description: String(localized: "body_measurements_settings_arm_length_description"),
                    value: uiState.bodyMeasurementsSettings.armLengthRatio.value,
                    range: uiState.bodyMeasurementsSettings.armLengthRatio.range,
                    valueString: { ratio in
                        getRatioAndLengthAsString(
                            ratio: ratio,
                            value: uiState.bodyMeasurementsSettings.bodyMeasurements(
                                armLengthRatio: ratio
                            ).armLength
                        )
                    },
                    onValueApplyRequest: onArmLengthRatioChange
                )
                
                SliderItem(
                    title: String(localized: "body_measurements_settings_chest_position"),
                    description: String(localized: "body_measurements_settings_chest_position_description"),
                    value: uiState.bodyMeasurementsSettings.chestPositionRatio.value,
                    range: uiState.bodyMeasurementsSettings.chestPositionRatio.range,
                    valueString: { ratio in
                        getRatioAndLengthAsString(
                            ratio: ratio,
                            value: uiState.bodyMeasurementsSettings.bodyMeasurements(
                                chestPositionRatio: ratio
                            ).shoulderToChestLength
                        )
                    },
                    onValueApplyRequest: onChestPositionRatioChange
                )
                
                SliderItem(
                    title: String(localized: "body_measurements_settings_waist_position"),
                    description: String(localized: "body_measurements_settings_waist_position_description"),
                    value: uiState.bodyMeasurementsSettings.waistPositionRatio.value,
                    range: uiState.bodyMeasurementsSettings.waistPositionRatio.range,
                    valueString: { ratio in
                        getRatioAndLengthAsString(
                            ratio: ratio,
                            value: uiState.bodyMeasurementsSettings.bodyMeasurements(
                                waistPositionRatio: ratio
                            ).waistToHipLength
                        )
                    },
                    onValueApplyRequest: onWaistPositionRatioChange
                )
                
                SettingDivider()
                
                SettingTitle(title: String(localized: "body_measurements_settings_lower_body"))
                
                SliderItem(
                    title: String(localized: "body_measurements_settings_hip_width"),
                    description: String(localized: "body_measurements_settings_hip_width_description"),
                    value: uiState.bodyMeasurementsSettings.hipWidthRatio.value,
                    range: uiState.bodyMeasurementsSettings.hipWidthRatio.range,
                    valueString: { ratio in
                        getRatioAndLengthAsString(
                            ratio: ratio,
                            value: uiState.bodyMeasurementsSettings.bodyMeasurements(
                                hipWidthRatio: ratio
                            ).hipWidth
                        )
                    },
                    onValueApplyRequest: onHipWidthRatioChange
                )
                
                SettingItem(
                    title: String(localized: "body_measurements_settings_leg_length"),
                    description: String(localized: "body_measurements_settings_leg_length_description"),
                    value: getLengthAsString(0.85)
                )
                .enabled(false)
                
                SliderItem(
                    title: String(localized: "body_measurements_settings_ankle_position"),
                    description: String(localized: "body_measurements_settings_ankle_position_description"),
                    value: uiState.bodyMeasurementsSettings.ankleHeightRatio.value,
                    range: uiState.bodyMeasurementsSettings.ankleHeightRatio.range,
                    valueString: { ratio in
                        getRatioAndLengthAsString(
                            ratio: ratio,
                            value: uiState.bodyMeasurementsSettings.bodyMeasurements(
                                ankleHeightRatio: ratio
                            ).ankleToHeelLength
                        )
                    },
                    onValueApplyRequest: onAnkleHeightRatioChange
                )
                
                SliderItem(
                    title: String(localized: "body_measurements_settings_foot_length"),
                    description: String(localized: "body_measurements_settings_foot_length_description"),
                    value: uiState.bodyMeasurementsSettings.footLengthRatio.value,
                    range: uiState.bodyMeasurementsSettings.footLengthRatio.range,
                    valueString: { ratio in
                        getRatioAndLengthAsString(
                            ratio: ratio,
                            value: uiState.bodyMeasurementsSettings.bodyMeasurements(
                                footLengthRatio: ratio
                            ).footLength
                        )
                    },
                    onValueApplyRequest: onFootLengthRatioChange
                )
                
                SettingDivider()
                
                SettingTitle(title: String(localized: "other"))
                
                SettingItem(
                    title: String(localized: "body_measurements_settings_reset_ratio")
                )
                .onClick {
                    showingResetConfirmation = true
                }
            }
        }
        .sheet(isPresented: $showingHeightEditor) {
            StringEditSheet(
                title: String(localized: "body_measurements_settings_body_height"),
                value: "\(uiState.bodyMeasurementsSettings.bodyHeight.value * 100)",
                defaultValue: String(localized: "body_measurements_settings_body_height_hint"),
                onApplyRequest: { newValue in
                    guard let floatValue = Double(newValue) else { return false }
                    let value = floatValue / 100.0
                    if value >= 1.0 && value <= 2.5 {
                        onBodyHeightChange(value)
                        return true
                    }
                    return false
                }
            )
            .keyboardType(.decimalPad)
            .onValueChange { newValue in
                return newValue.filter { $0.isNumber || $0 == "." }
            }
            .error(
                String(
                    localized: "body_measurements_settings_body_height_error",
                    uiState.bodyMeasurementsSettings.bodyHeight.range.lowerBound * 100,
                    uiState.bodyMeasurementsSettings.bodyHeight.range.upperBound * 100
                ),
                errorVisible: false
            )
        }
        .alert(
            String(localized: "body_measurements_settings_reset_ratio"),
            isPresented: $showingResetConfirmation
        ) {
            Button(String(localized: "cancel"), role: .cancel) { }
            Button(String(localized: "confirm")) {
                onRatioReset()
            }
        } message: {
            Text(String(localized: "body_measurements_settings_reset_ratio_message"))
        }
        .contentMargins(.bottom, 32, for: .scrollContent)
        .navigationTitle(String(localized: "body_measurements_settings_title"))
        .navigationBarTitleDisplayMode(.inline)
    }
    
    private func getLengthAsString(_ value: Double) -> String {
        return String(localized: "body_measurements_settings_value", value * 100)
    }
    
    private func getRatioAndLengthAsString(ratio: Double, value: Double) -> String {
        return String(localized: "body_measurements_settings_ratio_and_value", ratio * 100, value * 100)
    }
}

#Preview {
    BodyMeasurementsSettingsContent(
        uiState: BodyMeasurementsSettingsUiState(
            bodyMeasurementsSettings: BodyMeasurementsSettings.default
        ),
        onBodyHeightChange: { _ in },
        onUpperBodyHeightRatioChange: { _ in },
        onHeadHeightRatioChange: { _ in },
        onHeadZOffsetRatioChange: { _ in },
        onNeckLengthRatioChange: { _ in },
        onShoulderWidthRatioChange: { _ in },
        onArmLengthRatioChange: { _ in },
        onChestPositionRatioChange: { _ in },
        onWaistPositionRatioChange: { _ in },
        onHipWidthRatioChange: { _ in },
        onAnkleHeightRatioChange: { _ in },
        onFootLengthRatioChange: { _ in },
        onRatioReset: { }
    )
}
