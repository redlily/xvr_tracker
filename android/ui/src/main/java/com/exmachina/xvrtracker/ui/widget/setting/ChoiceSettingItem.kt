package com.exmachina.xvrtracker.ui.widget.setting

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.exmachina.xvrtracker.ui.theme.app.AppTheme
import com.exmachina.xvrtracker.ui.widget.dialog.ChoiceDialog

@Composable
fun <T> ChoiceSettingItem(
    title: String,
    description: String? = null,
    itemString: (@Composable (T) -> String?)? = null,
    items: List<T>,
    selectedItem: T,
    onEditorOpenRequest: (@Composable () -> Unit) -> Unit,
    onEditorCloseRequest: () -> Unit,
    onValueApplyRequest: (T) -> Unit,
    editorTitle: String = title,
    editorDescription: String? = description,
    radioButtonEnabled: Boolean = true,
    enabled: Boolean = true,
) {
    SettingItem(
        title = title,
        description = description,
        value = itemString?.invoke(selectedItem),
        onEditorShowRequest = onEditorOpenRequest,
        editorContent = {
            ChoiceDialog(
                onDismissRequest = onEditorCloseRequest,
                onChoice = onValueApplyRequest,
                title = editorTitle,
                description = editorDescription,
                itemString = itemString,
                items = items,
                selectedItem = selectedItem,
                radioButtonEnabled = radioButtonEnabled,
            )
        },
        enabled = enabled,
    )
}

@Preview
@Composable
private fun ChoiceSettingItemPreview() {
    AppTheme {
        ChoiceSettingItem(
            title = "ChoiceItem",
            description = "Choice Choice Choice Choice",
            items = listOf("Item1", "Item2", "Item3"),
            selectedItem = "Item2",
            onEditorOpenRequest = {},
            onEditorCloseRequest = {},
            onValueApplyRequest = {},
        )
    }
}
