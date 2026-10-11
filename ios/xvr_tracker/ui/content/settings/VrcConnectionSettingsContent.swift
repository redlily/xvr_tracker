import SwiftUI
import common
import data

struct VrcConnectionSettingsContent: View {
    let uiState: VrcConnectionSettingsUiState
    let onSendingIpOrDomainChange: (String) -> Bool
    let onSendingPortChange: (Int) -> Bool
    
    @State private var showingIpOrDomainEditor = false
    @State private var showingPortEditor = false
    
    var body: some View {
        ScrollView {
            VStack(spacing: 0) {
                SettingTitle(title: String(localized: "vrc_connection_settings_connection_destination"))
                
                StringItem(
                    title: String(localized: "vrc_connection_settings_sending_ip_or_domain"),
                    value: uiState.vrcConnectionSettings.sendingIpAddressOrDomain.ifEmpty(String(localized: "unset")),
                    editorValue: uiState.vrcConnectionSettings.sendingIpAddressOrDomain.ifEmpty(uiState.defaultSendingIpPrefix),
                    onClick: {
                        showingIpOrDomainEditor = true
                    }
                )
                
                StringItem(
                    title: String(localized: "vrc_connection_settings_sending_port"),
                    description: String(localized: "vrc_connection_settings_sending_port_description"),
                    value: "\(uiState.vrcConnectionSettings.sendingPort.value)",
                    onClick: {
                        showingPortEditor = true
                    }
                )
            }
        }
        .sheet(isPresented: $showingIpOrDomainEditor) {
            StringEditSheet(
                title: String(localized: "vrc_connection_settings_sending_ip_or_domain"),
                value: uiState.vrcConnectionSettings.sendingIpAddressOrDomain.ifEmpty(uiState.defaultSendingIpPrefix),
                defaultValue: "",
                onApplyRequest: onSendingIpOrDomainChange
            )
            .keyboardType(.numbersAndPunctuation)
            .autocorrectionDisabled(true)
            .error(
                String(localized: "vrc_connection_settings_sending_ip_or_domain_error"),
                errorVisible: false
            )
        }
        .sheet(isPresented: $showingPortEditor) {
            StringEditSheet(
                title: String(localized: "vrc_connection_settings_sending_port"),
                value: "\(uiState.vrcConnectionSettings.sendingPort.value)",
                defaultValue: "",
                onApplyRequest: { newValue in
                    guard let intValue = Int(newValue) else { return false }
                    return onSendingPortChange(intValue)
                }
            )
            .keyboardType(.numberPad)
            .onValueChange { newValue in
                // Filter to only allow positive integers
                return newValue.filter { $0.isNumber }
            }
            .error(
                String(localized: "vrc_connection_settings_sending_port_error"),
                errorVisible: false
            )
        }
        .contentMargins(.bottom, 32, for: .scrollContent)
        .navigationTitle(String(localized: "vrc_connection_settings_title"))
        .navigationBarTitleDisplayMode(.inline)
    }
}

#Preview {
    VrcConnectionSettingsContent(
        uiState: VrcConnectionSettingsUiState(
            vrcConnectionSettings: VrcConnectionSettings.default
        ),
        onSendingIpOrDomainChange: { _ in true },
        onSendingPortChange: { _ in true }
    )
}
