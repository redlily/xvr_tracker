import Foundation
import Combine

public struct UserDefaultsProperty<
    OutputType: Sendable,
    InputType: Sendable,
    PrimitiveType: Sendable
>: @unchecked Sendable {
    let userDefaults: UserDefaults
    let info: UserDefaultsPropertyInfo<OutputType, InputType, PrimitiveType>
    
    public init(
        userDefaults: UserDefaults,
        info: UserDefaultsPropertyInfo<OutputType, InputType, PrimitiveType>)
    {
        self.userDefaults = userDefaults
        self.info = info
    }
    
    public func getValue() -> OutputType {
        return info.getValue(from: userDefaults)
    }
    
    @discardableResult
    public func setValue(_ value: InputType) -> Bool {
        return info.setValue(value, to: userDefaults)
    }
}

extension UserDefaults {
    public func property<OutputType, InputType, PrimitiveType>(
        prefix: String,
        key: String,
        defaultValue: OutputType,
        checkValid: @escaping @Sendable (InputType) -> Bool = { _ in true },
        transformOutput: @escaping @Sendable (PrimitiveType) -> OutputType = { (value: PrimitiveType) in value },
        transformInput: @escaping @Sendable (InputType) -> PrimitiveType = { (value: PrimitiveType) in value }
    ) -> UserDefaultsProperty<OutputType, InputType, PrimitiveType> {
        return UserDefaultsProperty(
            userDefaults: self,
            info: UserDefaultsPropertyInfo(
                prefix: prefix,
                key: key,
                defaultValue: defaultValue,
                checkValid: checkValid,
                transformOutput: transformOutput,
                transformInput: transformInput
            )
        )
    }
}

public typealias UserDefaultsIntProperty = UserDefaultsProperty<Int, Int, Int>
public typealias UserDefaultsDoubleProperty = UserDefaultsProperty<Double, Double, Double>
public typealias UserDefaultsStringProperty = UserDefaultsProperty<String, String, String>
public typealias UserDefaultsBoolProperty = UserDefaultsProperty<Bool, Bool, Bool>
