package com.exmachina.xvrtracker.ui.widget.setting

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.exmachina.xvrtracker.common.util.floatFilter
import com.exmachina.xvrtracker.ui.theme.app.AppTheme
import com.exmachina.xvrtracker.ui.util.NumberOnly

@Composable
fun FloatSettingItem(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    valueString: (@Composable (Float) -> String) = { it.toString() },
    value: Float,
    onEditorShowRequest: (@Composable () -> Unit) -> Unit,
    onEditorCloseRequest: () -> Unit,
    onValueChange: ((String) -> String)? = ::floatFilter,
    onValueApplyRequest: (Float) -> Boolean,
    editorTitle: String = title,
    editorDescription: String? = description,
    editorDefaultValue: String = "",
    editorValueString: (@Composable (Float) -> String) = valueString,
    editorError: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.NumberOnly,
    enabled: Boolean = true,
) {
    StringSettingItem(
        title = title,
        modifier = modifier,
        description = description,
        value = valueString(value),
        onEditorShowRequest = onEditorShowRequest,
        onEditorCloseRequest = onEditorCloseRequest,
        onValueChange = onValueChange,
        onValueApplyRequest = { string: String ->
            string.toFloatOrNull()?.let(onValueApplyRequest) ?: false
        },
        editorTitle = editorTitle,
        editorDescription = editorDescription,
        editorDefaultValue = editorDefaultValue,
        editorValue = editorValueString(value),
        editorError = editorError,
        keyboardOptions = keyboardOptions,
        enabled = enabled,
    )
}

@Preview
@Composable
private fun FloatSettingItemPreview() {
    AppTheme {
        FloatSettingItem(
            title = "FloatItem",
            description = "Float Float Float Float",
            value = 3.14f,
            onEditorShowRequest = {},
            onEditorCloseRequest = {},
            onValueApplyRequest = { true },
        )
    }
}
