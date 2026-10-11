package com.exmachina.xvrtracker.ui.widget.setting

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.exmachina.xvrtracker.ui.theme.app.AppTheme
import com.exmachina.xvrtracker.ui.widget.dialog.StringEditDialog

@Composable
fun StringSettingItem(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    onEditorShowRequest: (@Composable () -> Unit) -> Unit,
    onEditorCloseRequest: () -> Unit,
    onValueChange: ((String) -> String)? = null,
    onValueApplyRequest: (String) -> Boolean,
    editorTitle: String = title,
    editorDescription: String? = description,
    editorDefaultValue: String = "",
    editorValue: String = value,
    editorError: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    enabled: Boolean = true,
) {
    SettingItem(
        modifier = modifier,
        title = title,
        value = value,
        description = description,
        onEditorShowRequest = onEditorShowRequest,
        editorContent = {
            StringEditDialog(
                onDismissRequest = onEditorCloseRequest,
                onValueChange = onValueChange,
                onApplyRequest = onValueApplyRequest,
                title = editorTitle,
                description = editorDescription,
                value = editorValue,
                defaultValue = editorDefaultValue,
                error = editorError,
                keyboardOptions = keyboardOptions.copy(imeAction = ImeAction.Done),
            )
        },
        enabled = enabled,
    )
}

@Preview
@Composable
private fun StringSettingItemPreview() {
    AppTheme {
        StringSettingItem(
            title = "StringItem",
            description = "String String String String",
            value = "Input",
            onEditorShowRequest = {},
            onEditorCloseRequest = {},
            onValueApplyRequest = { true },
        )
    }
}
