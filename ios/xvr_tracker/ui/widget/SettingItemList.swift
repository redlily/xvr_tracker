import SwiftUI

struct SwitchItem: View {
    let title: String
    let description: String?
    let value: Bool
    let onChange: (Bool) -> Void
    let enabled: Bool
    
    @State private var localValue: Bool
    
    init(
        title: String,
        description: String? = nil,
        value: Bool,
        onChange: @escaping (Bool) -> Void,
        enabled: Bool = true
    ) {
        self.title = title
        self.description = description
        self.value = value
        self.onChange = onChange
        self.enabled = enabled
        self._localValue = State(initialValue: value)
    }
    
    var body: some View {
        SettingItem(
            title: title,
            description: description,
            value: nil as String?,
            leading: { EmptyView() },
            trailing: {
                Toggle("", isOn: $localValue)
                    .onChange(of: localValue) {
                        onChange(localValue)
                    }
                    .disabled(!enabled)
            }
        )
        .id(value)
        .onAppear {
            localValue = value
        }
        .onChange(of: value) {
            localValue = value
        }
    }
}

struct IntItem: View {
    let title: String
    let description: String?
    let value: String
    let onValueChange: ((String) -> String)?
    let onValueApplyRequest: (Int) -> Bool
    let enabled: Bool
    
    @State private var showingEditor = false
    @State private var editingValue = ""
    
    init(
        title: String,
        description: String? = nil,
        value: String,
        onValueChange: ((String) -> String)? = nil,
        onValueApplyRequest: @escaping (Int) -> Bool,
        enabled: Bool = true
    ) {
        self.title = title
        self.description = description
        self.value = value
        self.onValueChange = onValueChange
        self.onValueApplyRequest = onValueApplyRequest
        self.enabled = enabled
    }
    
    var body: some View {
        SettingItem(
            title: title,
            description: description,
            value: value,
            leading: { EmptyView() },
            trailing: { EmptyView() }
        )
        .enabled(enabled)
        .onClick {
            if enabled {
                editingValue = value
                showingEditor = true
            }
        }
        .sheet(isPresented: $showingEditor) {
            NavigationView {
                VStack {
                    TextField("Value", text: $editingValue)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                        .keyboardType(.numberPad)
                        .onChange(of: editingValue) {
                            if let onValueChange = onValueChange {
                                editingValue = onValueChange(editingValue)
                            }
                        }
                    
                    Spacer()
                }
                .padding()
                .navigationTitle(title)
                .navigationBarTitleDisplayMode(.inline)
                .toolbar {
                    ToolbarItem(placement: .navigationBarLeading) {
                        Button(String(localized: "cancel")) {
                            showingEditor = false
                        }
                    }
                    ToolbarItem(placement: .navigationBarTrailing) {
                        Button(String(localized: "apply")) {
                            if let intValue = Int(editingValue),
                               onValueApplyRequest(intValue) {
                                showingEditor = false
                            }
                        }
                    }
                }
            }
            .presentationDetents([.medium])
            .presentationDragIndicator(.visible)
        }
    }
}

struct FloatItem: View {
    let title: String
    let description: String?
    let value: String
    let onValueChange: ((String) -> String)?
    let onValueApplyRequest: (Double) -> Bool
    let enabled: Bool
    
    @State private var showingEditor = false
    @State private var editingValue = ""
    
    init(
        title: String,
        description: String? = nil,
        value: String,
        onValueChange: ((String) -> String)? = nil,
        onValueApplyRequest: @escaping (Double) -> Bool,
        enabled: Bool = true
    ) {
        self.title = title
        self.description = description
        self.value = value
        self.onValueChange = onValueChange
        self.onValueApplyRequest = onValueApplyRequest
        self.enabled = enabled
    }
    
    var body: some View {
        SettingItem(
            title: title,
            description: description,
            value: value,
            leading: { EmptyView() },
            trailing: { EmptyView() }
        )
        .enabled(enabled)
        .onClick {
            if enabled {
                editingValue = value
                showingEditor = true
            }
        }
        .sheet(isPresented: $showingEditor) {
            NavigationView {
                VStack {
                    TextField("Value", text: $editingValue)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                        .keyboardType(.decimalPad)
                        .onChange(of: editingValue) {
                            if let onValueChange = onValueChange {
                                editingValue = onValueChange(editingValue)
                            }
                        }
                    
                    Spacer()
                }
                .padding()
                .navigationTitle(title)
                .navigationBarTitleDisplayMode(.inline)
                .toolbar {
                    ToolbarItem(placement: .navigationBarLeading) {
                        Button(String(localized: "cancel")) {
                            showingEditor = false
                        }
                    }
                    ToolbarItem(placement: .navigationBarTrailing) {
                        Button(String(localized: "apply")) {
                            if let doubleValue = Double(editingValue),
                               onValueApplyRequest(doubleValue) {
                                showingEditor = false
                            }
                        }
                    }
                }
            }
            .presentationDetents([.medium])
            .presentationDragIndicator(.visible)
        }
    }
}

struct SliderItem: View {
    let title: String
    let description: String?
    let value: Double
    let range: ClosedRange<Double>
    let valueString: (Double) -> String
    let onValueApplyRequest: (Double) -> Void
    let enabled: Bool
    
    @State private var showingEditor = false
    @State private var editingValue: Double
    
    init(
        title: String,
        description: String? = nil,
        value: Double,
        range: ClosedRange<Double>,
        valueString: @escaping (Double) -> String,
        onValueApplyRequest: @escaping (Double) -> Void,
        enabled: Bool = true
    ) {
        self.title = title
        self.description = description
        self.value = value
        self.range = range
        self.valueString = valueString
        self.onValueApplyRequest = onValueApplyRequest
        self.enabled = enabled
        self._editingValue = State(initialValue: value)
    }
    
    var body: some View {
        SettingItem(
            title: title,
            description: description,
            value: valueString(value),
            leading: { EmptyView() },
            trailing: { EmptyView() }
        )
        .enabled(enabled)
        .onClick {
            if enabled {
                editingValue = value
                showingEditor = true
            }
        }
        .sheet(isPresented: $showingEditor) {
            NavigationView {
                VStack(spacing: 20) {
                    if let description = description {
                        Text(description)
                            .font(.body)
                            .foregroundColor(.secondary)
                            .padding(.horizontal)
                            .frame(maxWidth: .infinity, alignment: .leading)
                    }
                    
                    Text(valueString(editingValue))
                        .font(.title2)
                        .padding()
                    
                    Slider(
                        value: $editingValue,
                        in: range
                    )
                    .padding()
                    
                    Spacer()
                }
                .padding()
                .background(Color(.systemBackground))
                .navigationTitle(title)
                .navigationBarTitleDisplayMode(.inline)
                .toolbar {
                    ToolbarItem(placement: .navigationBarLeading) {
                        Button(String(localized: "cancel")) {
                            showingEditor = false
                        }
                    }
                    ToolbarItem(placement: .navigationBarTrailing) {
                        Button(String(localized: "apply")) {
                            onValueApplyRequest(editingValue)
                            showingEditor = false
                        }
                    }
                }
            }
            .presentationDetents([.medium])
            .presentationDragIndicator(.visible)
        }
    }
}

struct ChoiceItem<T: Hashable>: View {
    let title: String
    let description: String?
    let items: [T]
    let selectedItem: T
    let itemString: (T) -> String
    let onValueApplyRequest: (T) -> Void
    let enabled: Bool
    let showValue: Bool
    
    @State private var showingEditor = false
    
    init(
        title: String,
        description: String? = nil,
        items: [T],
        selectedItem: T,
        itemString: @escaping (T) -> String,
        onValueApplyRequest: @escaping (T) -> Void,
        enabled: Bool = true,
        showValue: Bool = true
    ) {
        self.title = title
        self.description = description
        self.items = items
        self.selectedItem = selectedItem
        self.itemString = itemString
        self.onValueApplyRequest = onValueApplyRequest
        self.enabled = enabled
        self.showValue = showValue
    }
    
    var body: some View {
        SettingItem(
            title: title,
            description: description,
            value: showValue ? itemString(selectedItem) : nil,
            leading: { EmptyView() },
            trailing: { EmptyView() }
        )
        .enabled(enabled)
        .onClick {
            if enabled {
                showingEditor = true
            }
        }
        .sheet(isPresented: $showingEditor) {
            NavigationView {
                VStack(spacing: 0) {
                    if let description = description {
                        Text(description)
                            .font(.body)
                            .foregroundColor(.secondary)
                            .padding()
                            .frame(maxWidth: .infinity)
                            .background(Color(.systemBackground))
                    }
                    
                    List(items, id: \.self) { item in
                        Button(action: {
                            onValueApplyRequest(item)
                            showingEditor = false
                        }) {
                            HStack {
                                Text(itemString(item))
                                Spacer()
                                if item == selectedItem {
                                    Image(systemName: "checkmark")
                                        .foregroundColor(.blue)
                                }
                            }
                        }
                    }
                    .scrollContentBackground(.hidden)
                    .background(Color(.systemBackground))
                }
                .background(Color(.systemBackground))
                .navigationTitle(title)
                .navigationBarTitleDisplayMode(.inline)
                .toolbar {
                    ToolbarItem(placement: .navigationBarTrailing) {
                        Button("Done") {
                            showingEditor = false
                        }
                    }
                }
            }
            .presentationDetents([.medium])
            .presentationDragIndicator(.visible)
        }
    }
}

struct StringItem: View {
    let title: String
    let description: String?
    let value: String
    let editorValue: String
    let onClick: () -> Void
    let enabled: Bool
    
    init(
        title: String,
        description: String? = nil,
        value: String,
        editorValue: String? = nil,
        onClick: @escaping () -> Void,
        enabled: Bool = true
    ) {
        self.title = title
        self.description = description
        self.value = value
        self.editorValue = editorValue ?? value
        self.onClick = onClick
        self.enabled = enabled
    }
    
    var body: some View {
        SettingItem(
            title: title,
            description: description,
            value: value,
            leading: { EmptyView() },
            trailing: { EmptyView() }
        )
        .enabled(enabled)
        .onClick {
            if enabled {
                onClick()
            }
        }
    }
}

#Preview {
    VStack {
        SettingTitle(title: "Group Title")
        
        SwitchItem(
            title: "Switch Item",
            description: "Description",
            value: true,
            onChange: { _ in }
        )
        
        IntItem(
            title: "Int Item",
            description: "Description",
            value: "123",
            onValueApplyRequest: { _ in true }
        )
        
        FloatItem(
            title: "Float Item",
            description: "Description",
            value: "1.23",
            onValueApplyRequest: { _ in true }
        )
        
        SliderItem(
            title: "Slider Item",
            description: "Description",
            value: 0.5,
            range: 0...1,
            valueString: { "\($0)" },
            onValueApplyRequest: { _ in }
        )
        
        ChoiceItem(
            title: "Choice Item",
            description: "Description",
            items: ["Option 1", "Option 2", "Option 3"],
            selectedItem: "Option 1",
            itemString: { $0 },
            onValueApplyRequest: { _ in }
        )
        
        StringItem(
            title: "String Item",
            description: "Description",
            value: "Sample Value",
            onClick: { }
        )
    }
}
