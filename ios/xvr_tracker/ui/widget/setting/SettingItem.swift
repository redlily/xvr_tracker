import SwiftUI
import common

struct SettingItem<
    TitleLabel: View,
    DescriptionLabel: View,
    ValueLabel: View,
    LeadingLabel: View,
    TrailingLabel: View
>: View {
    let title: TitleLabel
    let description: DescriptionLabel?
    let value: ValueLabel?
    let leading: LeadingLabel?
    let trailing: TrailingLabel?
    
    var enabled: Bool = true
    
    var clickHandler: (() -> Void)? = nil
    
    var body: some View {
        Button(action: clickHandler ?? {}) {
            HStack(spacing: 16) {
                if let leading {
                    leading
                }
                
                VStack(alignment: .leading, spacing: 8) {
                    title
                    
                    if let description {
                        description
                            .font(.caption)
                            .foregroundColor(enabled ? .secondary : .secondary.opacity(0.6))
                    }
                    
                    if let value {
                        value
                            .font(.caption)
                            .foregroundColor(enabled ? .secondary : .secondary.opacity(0.6))
                    }
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                
                if let trailing {
                    trailing
                }
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 12)
            .frame(maxWidth: .infinity, alignment: .leading)
            .contentShape(Rectangle())
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .buttonStyle(PlainButtonStyle())
        .disabled(!enabled)
    }
    
    init(
        title: TitleLabel,
        description: DescriptionLabel? = EmptyView(),
        value: ValueLabel? = EmptyView(),
        leading: LeadingLabel? = EmptyView(),
        trailing: TrailingLabel? = EmptyView()
    ) {
        self.title = title
        self.description = description
        self.value = value
        self.leading = leading
        self.trailing = trailing
    }
    
    func enabled(_ enabled: Bool) -> Self {
        return update(self) {
            $0.enabled = enabled
        }
    }
    
    func onClick(_ action: (() -> Void)?) -> Self {
        return update(self) {
            $0.clickHandler = action
        }
    }
}

extension SettingItem {
    init(
        @ViewBuilder title: () -> TitleLabel,
        @ViewBuilder description: () -> DescriptionLabel? = { EmptyView() },
        @ViewBuilder value: () -> ValueLabel? = { EmptyView() },
        @ViewBuilder leading: () -> LeadingLabel? = { EmptyView() },
        @ViewBuilder trailing: () -> TrailingLabel? = { EmptyView() }
    ) {
        self.init(
            title: title(),
            description: description(),
            value: value(),
            leading: leading(),
            trailing: trailing()
        )
    }
}

extension SettingItem where TitleLabel == Text, DescriptionLabel == Text, ValueLabel == Text {
    init(
      title: String,
      description: String? = nil,
      value: String? = nil,
      @ViewBuilder leading: () -> LeadingLabel? = { EmptyView() },
      @ViewBuilder trailing: () -> TrailingLabel? = { EmptyView() }
    ) {
      self.init(
        title: { Text(title) },
        description: { if let description { Text(description) } },
        value: { if let value { Text(value) } },
        leading: leading,
        trailing: trailing
      )
    }
}


#Preview {
    @Previewable @State var count = 0
    
    VStack(spacing: 0) {
        SettingItem(
            title: { Text("Title") },
            description: { Text("Description") },
            value: { if count % 2 == 0 { Text("Value") } else { EmptyView() } }
        )
    }
}
