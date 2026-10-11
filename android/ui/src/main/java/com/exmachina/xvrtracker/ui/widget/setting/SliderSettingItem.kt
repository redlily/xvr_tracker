package com.exmachina.xvrtracker.ui.widget.setting

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.exmachina.xvrtracker.common.type.RangedValue
import com.exmachina.xvrtracker.ui.widget.dialog.SliderDialog

@Composable
fun SliderSettingItem(
    title: String,
    value: Float,
    valueString: (@Composable (Float) -> String)?,
    onValueApplyRequest: (Float) -> Unit,
    editorValueRange: ClosedFloatingPointRange<Float>,
    onEditorOpenRequest: (@Composable () -> Unit) -> Unit,
    onEditorCloseRequest: () -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
    editorTitle: String = title,
    editorDescription: String? = description,
    editorValueString: (@Composable (Float) -> String)? = valueString,
    enabled: Boolean = true,
) {
    SettingItem(
        title = title,
        modifier = modifier,
        description = description,
        value = valueString?.invoke(value),
        onEditorShowRequest = onEditorOpenRequest,
        editorContent = {
            SliderDialog(
                onDismissRequest = onEditorCloseRequest,
                onApplyRequest = onValueApplyRequest,
                title = editorTitle,
                description = editorDescription,
                valueString = editorValueString,
                value = value,
                valueRange = editorValueRange,
            )
        },
        enabled = enabled,
    )
}

@Composable
fun SliderSettingItem(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    valueString: (@Composable (Float) -> String)? = null,
    value: RangedValue<Float>,
    onEditorOpenRequest: (@Composable () -> Unit) -> Unit,
    onEditorCloseRequest: () -> Unit,
    onValueApplyRequest: (Float) -> Unit,
    editorTitle: String = title,
    editorDescription: String? = description,
    editorValueString: (@Composable (Float) -> String)? = valueString,
    enabled: Boolean = true,
) {
    SliderSettingItem(
        title = title,
        modifier = modifier,
        description = description,
        valueString = valueString,
        value = value.value,
        onEditorOpenRequest = onEditorOpenRequest,
        onEditorCloseRequest = onEditorCloseRequest,
        onValueApplyRequest = onValueApplyRequest,
        editorTitle = editorTitle,
        editorDescription = editorDescription,
        editorValueString = editorValueString,
        editorValueRange = value.range.start..value.range.endInclusive,
        enabled = enabled,
    )
}
