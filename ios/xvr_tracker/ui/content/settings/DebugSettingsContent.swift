import SwiftUI
import data

struct DebugSettingsContent: View {
    let uiState: DebugSettingsUiState
    let onAnalyzedDataPlotServerEnabledChange: (Bool) -> Void
    let onAnalyzedDataPlotServerPortChange: (Int) -> Void
    let onAnalyzedDataPlotServerPingChange: (Int) -> Void
    let onAnalyzedDataPlotServerTimeoutChange: (Int) -> Void
    let onCopyAnalyzedDataPlotServerUrl: (String) -> Void
    @Binding var showCopiedAlert: Bool
    
    private var analyzedDataPlotServerUrl: String {
        if let localIpAddress = uiState.localIpAddress {
            let format = String(localized: "debug_settings_analyzed_data_plot_server_url_format")
            let formatted = format.replacingOccurrences(of: "%1$s", with: "%@")
                .replacingOccurrences(of: "%2$d", with: "%d")
            return String(format: formatted, localIpAddress, uiState.analyzedDataPlotServerSettings.port.value)
        } else {
            return String(localized: "debug_settings_analyzed_data_plot_server_url_unavailable")
        }
    }
    
    var body: some View {
        ScrollView {
            VStack(spacing: 0) {
                SwitchItem(
                    title: String(localized: "debug_settings_analyzed_data_plot_server_group"),
                    description: String(localized: "debug_settings_analyzed_data_plot_server_group_description"),
                    value: uiState.analyzedDataPlotServerSettings.isEnabled,
                    onChange: onAnalyzedDataPlotServerEnabledChange
                )
                
                IntItem(
                    title: String(localized: "debug_settings_analyzed_data_plot_server_port"),
                    description: String(localized: "debug_settings_analyzed_data_plot_server_port_description"),
                    value: "\(uiState.analyzedDataPlotServerSettings.port.value)",
                    onValueApplyRequest: { value in
                        if uiState.analyzedDataPlotServerSettings.port.range.contains(value) {
                            onAnalyzedDataPlotServerPortChange(value)
                            return true
                        }
                        return false
                    },
                    enabled: uiState.analyzedDataPlotServerSettings.isEnabled
                )
                
                IntItem(
                    title: String(localized: "debug_settings_analyzed_data_plot_server_ping"),
                    description: String(localized: "debug_settings_analyzed_data_plot_server_ping_description"),
                    value: "\(uiState.analyzedDataPlotServerSettings.ping.value)",
                    onValueApplyRequest: { value in
                        if uiState.analyzedDataPlotServerSettings.ping.range.contains(value) {
                            onAnalyzedDataPlotServerPingChange(value)
                            return true
                        }
                        return false
                    },
                    enabled: uiState.analyzedDataPlotServerSettings.isEnabled
                )
                
                IntItem(
                    title: String(localized: "debug_settings_analyzed_data_plot_server_timeout"),
                    description: String(localized: "debug_settings_analyzed_data_plot_server_timeout_description"),
                    value: "\(uiState.analyzedDataPlotServerSettings.timeout.value)",
                    onValueApplyRequest: { value in
                        if uiState.analyzedDataPlotServerSettings.timeout.range.contains(value) {
                            onAnalyzedDataPlotServerTimeoutChange(value)
                            return true
                        }
                        return false
                    },
                    enabled: uiState.analyzedDataPlotServerSettings.isEnabled
                )
                
                StringItem(
                    title: String(localized: "debug_settings_analyzed_data_plot_server_url"),
                    value: analyzedDataPlotServerUrl,
                    onClick: {
                        onCopyAnalyzedDataPlotServerUrl(analyzedDataPlotServerUrl)
                    },
                    enabled: uiState.localIpAddress != nil && uiState.analyzedDataPlotServerSettings.isEnabled
                )
            }
        }
        .contentMargins(.bottom, 32, for: .scrollContent)
        .navigationTitle(String(localized: "settings_top_debug"))
        .navigationBarTitleDisplayMode(.inline)
        .alert(String(localized: "debug_settings_analyzed_data_plot_server_url_copied"), isPresented: $showCopiedAlert) {
            Button("OK", role: .cancel) { }
        }
    }
}

#Preview {
    DebugSettingsContent(
        uiState: DebugSettingsUiState(
            analyzedDataPlotServerSettings: AnalyzedDataPlotServerSettings.default,
            localIpAddress: "192.168.11.5"
        ),
        onAnalyzedDataPlotServerEnabledChange: { _ in },
        onAnalyzedDataPlotServerPortChange: { _ in },
        onAnalyzedDataPlotServerPingChange: { _ in },
        onAnalyzedDataPlotServerTimeoutChange: { _ in },
        onCopyAnalyzedDataPlotServerUrl: { _ in },
        showCopiedAlert: .constant(false)
    )
}
