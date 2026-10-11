import Foundation

public struct UserDefaultsPropertyInfo<
    OutputType: Sendable,
    InputType: Sendable,
    PrimitiveType: Sendable
>: Sendable {
    let key: String
    let defaultValue: OutputType
    let checkValid: @Sendable (InputType) -> Bool
    let transformOutput: @Sendable (PrimitiveType) -> OutputType
    let transformInput: @Sendable (InputType) -> PrimitiveType
    
    public init(
        key: String,
        defaultValue: OutputType,
        checkValid: @escaping @Sendable (InputType) -> Bool = { _ in true },
        transformOutput: @escaping @Sendable (PrimitiveType) -> OutputType = { (value: OutputType) in value },
        transformInput: @escaping @Sendable (InputType) -> PrimitiveType = { (value: OutputType) in value }
    ) {
        self.key = key
        self.defaultValue = defaultValue
        self.checkValid = checkValid
        self.transformOutput = transformOutput
        self.transformInput = transformInput
    }
}

extension UserDefaultsPropertyInfo {
    public init(
        prefix: String,
        key: String,
        defaultValue: OutputType,
        checkValid: @escaping @Sendable (InputType) -> Bool = { _ in true },
        transformOutput: @escaping @Sendable (PrimitiveType) -> OutputType = { (value: PrimitiveType) in value },
        transformInput: @escaping @Sendable (InputType) -> PrimitiveType = { (value: PrimitiveType) in value }
    ) {
        self.init(
            key: "\(prefix).\(key)",
            defaultValue: defaultValue,
            checkValid: checkValid,
            transformOutput: transformOutput,
            transformInput: transformInput
        )
    }
    
    public func getValue(from: UserDefaults) -> OutputType {
        if let value = from.object(forKey: key) as? PrimitiveType {
            return transformOutput(value)
        } else {
            return defaultValue
        }
    }
    
    @discardableResult
    public func setValue(_ value: InputType, to: UserDefaults) -> Bool {
        if !checkValid(value) {
            return false
        }
        to.set(transformInput(value), forKey: key)
        return true
    }
}

extension UserDefaults {
    public func getValue<OutputType, InputType, PrimitiveType>(
        info: UserDefaultsPropertyInfo<OutputType, InputType, PrimitiveType>
    ) -> OutputType {
        return info.getValue(from: self)
    }
    
    @discardableResult
    public func setValue<PrimitiveType, OutputType, InputType>(
        _ value: InputType,
        info: UserDefaultsPropertyInfo<OutputType, InputType, PrimitiveType>
    ) -> Bool {
        return info.setValue(value, to: self)
    }
}
