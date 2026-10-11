import SwiftUI
import common

struct StringEditSheet<DescriptionLabel: View>: View {
    let title: String
    let description: DescriptionLabel?
    let value: String
    let defaultValue: String
    let applyRequestHandler: (String) -> Bool
    
    var error: String? = nil
    var keyboardType: UIKeyboardType = .default
    var autocorrectionDisabled: Bool = false
    var valueChangeHandler: ((String) -> String)? = nil
    
    @State private var editingValue: String
    @State private var errorVisible: Bool
    @FocusState private var isTextFieldFocused: Bool
    @Environment(\.dismiss) private var dismiss
    
    init(
        title: String,
        description: DescriptionLabel? = nil,
        value: String,
        defaultValue: String,
        onApplyRequest: @escaping (String) -> Bool
    ) {
        self.title = title
        self.description = description
        self.value = value
        self.defaultValue = defaultValue
        self.applyRequestHandler = onApplyRequest
        
        editingValue = value
        errorVisible = false
    }
    
    var body: some View {
        NavigationStack {
            VStack(spacing: 20) {
                if let description = description {
                    description
                }
                
                TextField(defaultValue, text: $editingValue)
                    .textFieldStyle(RoundedBorderTextFieldStyle())
                    .keyboardType(keyboardType)
                    .textInputAutocapitalization(.never)
                    .autocorrectionDisabled(autocorrectionDisabled)
                    .focused($isTextFieldFocused)
                    .onChange(of: editingValue) {
                        if let valueChangeHandler = valueChangeHandler,
                           let newValue = Optional(valueChangeHandler(editingValue)),
                           newValue != editingValue {
                            editingValue = newValue
                        }
                    }
                
                if errorVisible, let error = error {
                    Text(error)
                        .foregroundColor(.red)
                        .font(.caption)
                        .multilineTextAlignment(.center)
                        .frame(maxWidth: .infinity)
                }
                
                Spacer()
            }
            .padding()
            .navigationTitle(title)
            .navigationBarTitleDisplayMode(.inline)
            .navigationBarBackButtonHidden(true)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button(String(localized: "cancel")) {
                        dismiss()
                    }
                }
                
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button(String(localized: "apply")) {
                        let success = applyRequestHandler(editingValue)
                        if success {
                            dismiss()
                        } else {
                            errorVisible = true
                        }
                    }
                    .disabled(editingValue.isEmpty)
                }
            }
        }
        .onAppear {
            editingValue = value
            isTextFieldFocused = true
        }
        .presentationDetents([.medium])
        .presentationDragIndicator(.visible)
    }
    
    func error(_ error: String?, errorVisible: Bool = false) -> Self {
        return update(self) {
            $0.error = error
            $0._errorVisible = State(initialValue: errorVisible)
        }
    }
    
    func keyboardType(_ keyboardType: UIKeyboardType) -> Self {
        return update(self) {
            $0.keyboardType = keyboardType
        }
    }
    
    func onValueChange(_ action: ((String) -> String)?) -> Self {
        return update(self) {
            $0.valueChangeHandler = action
        }
    }
    
    func autocorrectionDisabled(_ disabled: Bool) -> Self {
        return update(self) {
            $0.autocorrectionDisabled = disabled
        }
    }
}

extension StringEditSheet {
    init(
        title: String,
        @ViewBuilder description: () -> DescriptionLabel? = { nil },
        value: String,
        defaultValue: String,
        onApplyRequest: @escaping (String) -> Bool
    ) {
        self.init(
            title: title,
            description: description(),
            value: value,
            defaultValue: defaultValue,
            onApplyRequest: onApplyRequest
        )
    }
}

extension StringEditSheet where DescriptionLabel == Text? {
    init(
        title: String,
        description: String? = nil,
        value: String,
        defaultValue: String,
        onApplyRequest: @escaping (String) -> Bool
    ) {
        self.init(
            title: title,
            description: { description.map { Text($0) } },
            value: value,
            defaultValue: defaultValue,
            onApplyRequest: onApplyRequest
        )
    }
}

#Preview {
    StringEditSheet(
        title: "Edit IP Address",
        description: "Enter the destination IP address or domain name",
        value: "192.168.10.2",
        defaultValue: "Enter IP address",
        onApplyRequest: { newValue in true }
    )
}
