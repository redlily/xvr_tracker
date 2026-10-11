package com.exmachina.xvrtracker.ui.widget.setting

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.exmachina.xvrtracker.ui.theme.app.AppTheme
import com.exmachina.xvrtracker.ui.util.NumberOnly

@Composable
fun IntSettingItem(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    value: String,
    onEditorShowRequest: (@Composable () -> Unit) -> Unit,
    onEditorCloseRequest: () -> Unit,
    onValueChange: ((String) -> String)? = null,
    onValueApplyRequest: (Int) -> Boolean,
    editorTitle: String = title,
    editorDescription: String? = description,
    editorDefaultValue: String = "",
    editorError: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.NumberOnly,
    enabled: Boolean = true,
) {
    StringSettingItem(
        title = title,
        description = description,
        value = value,
        onEditorShowRequest = onEditorShowRequest,
        onEditorCloseRequest = onEditorCloseRequest,
        onValueChange = onValueChange,
        onValueApplyRequest = { string: String ->
            string.toIntOrNull()?.let(onValueApplyRequest) ?: false
        },
        editorTitle = editorTitle,
        editorDescription = editorDescription,
        editorDefaultValue = editorDefaultValue,
        editorError = editorError,
        keyboardOptions = keyboardOptions,
        enabled = enabled,
        modifier = modifier,
    )
}

@Composable
fun IntSettingItem(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    value: Int,
    onEditorShowRequest: (@Composable () -> Unit) -> Unit,
    onEditorCloseRequest: () -> Unit,
    onValueChange: ((String) -> String)? = null,
    onValueApplyRequest: (Int) -> Boolean,
    editorTitle: String = title,
    editorDescription: String? = description,
    editorDefaultValue: String = "",
    editorError: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.NumberOnly,
    enabled: Boolean = true,
) {
    IntSettingItem(
        title = title,
        description = description,
        value = value.toString(),
        onEditorShowRequest = onEditorShowRequest,
        onEditorCloseRequest = onEditorCloseRequest,
        onValueChange = onValueChange,
        onValueApplyRequest = onValueApplyRequest,
        editorTitle = editorTitle,
        editorDescription = editorDescription,
        editorDefaultValue = editorDefaultValue,
        editorError = editorError,
        keyboardOptions = keyboardOptions,
        enabled = enabled,
        modifier = modifier,
    )
}

@Preview
@Composable
private fun IntSettingItemPreview() {
    AppTheme {
        IntSettingItem(
            title = "IntItem",
            description = "Int Int Int Int",
            value = "42",
            onEditorShowRequest = {},
            onEditorCloseRequest = {},
            onValueApplyRequest = { true },
        )
    }
}
