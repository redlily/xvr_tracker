import SwiftUI
import common

struct SettingTitle<
    TitleLabel: View,
    DescriptionLabel: View,
    LeadingLabel: View,
    TrailingLabel: View
>: View {
    let title: TitleLabel
    let description: DescriptionLabel?
    let leading: LeadingLabel?
    let trailing: TrailingLabel?
    
    var enabled: Bool = true
    
    var body: some View {
        HStack(spacing: 16) {
            if let leading = leading {
                leading
                    .foregroundColor(enabled ? .primary : .secondary)
            }
            
            VStack(alignment: .leading, spacing: 8) {
                title
                    .font(.headline)
                    .foregroundColor(enabled ? .primary : .secondary)
                
                if let description = description {
                    description
                        .font(.caption)
                        .foregroundColor(enabled ? .secondary : .secondary.opacity(0.6))
                }
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            
            if let trailing = trailing {
                trailing
                    .foregroundColor(enabled ? .primary : .secondary)
            }
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 12)
    }
    
    init(
        title: TitleLabel,
        description: DescriptionLabel? = nil,
        leading: LeadingLabel? = nil,
        trailing: TrailingLabel? = nil
    ) {
        self.title = title
        self.description = description
        self.leading = leading
        self.trailing = trailing
    }
    
    func enabled(_ enabled: Bool) -> Self {
        return update(self) {
            $0.enabled = enabled
        }
    }
}

extension SettingTitle {
    init(
        @ViewBuilder title: () -> TitleLabel,
        @ViewBuilder description: () -> DescriptionLabel? = { EmptyView() },
        @ViewBuilder leading: () -> LeadingLabel? = { EmptyView() },
        @ViewBuilder trailing: () -> TrailingLabel? = { EmptyView() }
    ) {
        self.init(
            title: title(),
            description: description(),
            leading: leading(),
            trailing: trailing()
        )
    }
}

extension SettingTitle where TitleLabel == Text, DescriptionLabel == Text? {
    init(
        title: String,
        descriptionText: String? = nil,
        @ViewBuilder leading: () -> LeadingLabel? = { EmptyView() },
        @ViewBuilder trailing: () -> TrailingLabel? = { EmptyView() }
    ) {
        self.init(
            title: { Text(title) },
            description: { if let d = descriptionText { Text(d) } },
            leading: leading,
            trailing: trailing
        )
    }
}

#Preview {
    VStack(spacing: 0) {
        SettingTitle(title: "Title")
        
        SettingTitle(
            title: "Title",
            descriptionText: "Description"
        )
    }
}
