package com.exmachina.xvrtracker.ui.widget.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.exmachina.xvrtracker.common.type.RangedValue
import com.exmachina.xvrtracker.common.ui.iconSpec

@Composable
fun SettingItemList(
    modifier: Modifier = Modifier,
    content: @Composable SettingListScope.() -> Unit,
) {
    var editorState by remember { mutableStateOf<(@Composable () -> Unit)?>(null) }

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
    ) {
        SettingListScopeInternal(
            onEditorOpen = { editorState = it },
            onEditorClose = { editorState = null },
        ).content()

    }

    editorState?.let {
        editorState?.invoke()
    }
}

interface SettingListScope {

    val onEditorOpen: (@Composable () -> Unit) -> Unit
    val onEditorClose: () -> Unit

    @Composable
    fun Group(
        title: String,
        modifier: Modifier = Modifier,
        description: String? = null,
        leadingContent: @Composable (() -> Unit)? = null,
        trailingContent: @Composable (() -> Unit)? = null,
    )

    @Composable
    fun Item(
        title: String,
        modifier: Modifier = Modifier,
        onClick: (() -> Unit)? = null,
        description: String? = null,
        value: String? = null,
        leadingContent: @Composable (() -> Unit)? = null,
        trailingContent: @Composable (() -> Unit)? = null,
        enabled: Boolean = true,
    )

    @Composable
    fun IntItem(
        onValueApplyRequest: (Int) -> Boolean,
        title: String,
        modifier: Modifier = Modifier,
        description: String? = null,
        value: String,
        editorError: String? = null,
        onValueChange: ((String) -> String)? = null,
        enabled: Boolean = true,
    )

    @Composable
    fun IntItem(
        onValueApplyRequest: (Int) -> Boolean,
        title: String,
        modifier: Modifier = Modifier,
        description: String? = null,
        value: Int,
        editorError: String? = null,
        onValueChange: ((String) -> String)? = null,
        enabled: Boolean = true,
    )

    @Composable
    fun FloatItem(
        onValueApplyRequest: (Float) -> Boolean,
        title: String,
        modifier: Modifier = Modifier,
        description: String? = null,
        value: String,
        editorError: String? = null,
        onValueChange: ((String) -> String)? = null,
        enabled: Boolean = true,
    )

    @Composable
    fun FloatItem(
        onValueApplyRequest: (Float) -> Boolean,
        title: String,
        modifier: Modifier = Modifier,
        description: String? = null,
        value: Float,
        editorError: String? = null,
        onValueChange: ((String) -> String)? = null,
        enabled: Boolean = true,
    )

    @Composable
    fun SwitchItem(
        onChange: (Boolean) -> Unit,
        title: String,
        value: Boolean,
        modifier: Modifier = Modifier,
        description: String? = null,
        enabled: Boolean = true,
    )

    @Composable
    fun SliderItem(
        onValueApplyRequest: (Float) -> Unit,
        title: String,
        modifier: Modifier = Modifier,
        description: String? = null,
        valueString: (@Composable (Float) -> String)? = null,
        value: RangedValue<Float>,
        enabled: Boolean = true,
    )

    @Composable
    fun <T> ChoiceItem(
        onValueApplyRequest: (T) -> Unit,
        title: String,
        modifier: Modifier = Modifier,
        description: String? = null,
        itemString: (@Composable (T) -> String?)? = null,
        items: List<T>,
        selectedItem: T,
        radioButtonEnabled: Boolean = true,
        enabled: Boolean = true,
    )

    @Composable
    fun <T> ChoiceItem(
        onValueApplyRequest: (T) -> Unit,
        title: String,
        modifier: Modifier = Modifier,
        description: String? = null,
        itemString: Map<T, String?>,
        items: List<T>,
        selectedItem: T,
        radioButtonEnabled: Boolean = true,
        enabled: Boolean = true,
    )
}

@Composable
fun SettingListScope.Item(
    title: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    description: String? = null,
    value: String? = null,
    leadingIcon: ImageVector? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
) {
    Item(
        title = title,
        modifier = modifier,
        onClick = onClick,
        description = description,
        value = value,
        leadingContent = leadingIcon?.let { iconSpec(imageVector = it) },
        trailingContent = trailingContent,
        enabled = enabled,
    )
}

internal class SettingListScopeInternal(
    override val onEditorOpen: (@Composable () -> Unit) -> Unit,
    override val onEditorClose: () -> Unit,
) : SettingListScope {

    private var groupCount = 0

    @Composable
    override fun Group(
        title: String,
        modifier: Modifier,
        description: String?,
        leadingContent: @Composable (() -> Unit)?,
        trailingContent: @Composable (() -> Unit)?,
    ) {
        if (groupCount++ > 0) {
            SettingDivider()
        }

        SettingTitle(
            title = title,
            modifier = modifier,
            description = description,
            leadingContent = leadingContent,
            trailingContent = trailingContent,
        )
    }

    @Composable
    override fun Item(
        title: String,
        modifier: Modifier,
        onClick: (() -> Unit)?,
        description: String?,
        value: String?,
        leadingContent: @Composable (() -> Unit)?,
        trailingContent: @Composable (() -> Unit)?,
        enabled: Boolean,
    ) {
        SettingItem(
            onClick = onClick,
            title = title,
            description = description,
            value = value,
            leadingContent = leadingContent,
            trailingContent = trailingContent,
            modifier = modifier,
            enabled = enabled,
        )
    }

    @Composable
    override fun IntItem(
        onValueApplyRequest: (Int) -> Boolean,
        title: String,
        modifier: Modifier,
        description: String?,
        value: String,
        editorError: String?,
        onValueChange: ((String) -> String)?,
        enabled: Boolean,
    ) {
        IntSettingItem(
            title = title,
            description = description,
            value = value,
            editorError = editorError,
            onEditorShowRequest = onEditorOpen,
            onEditorCloseRequest = onEditorClose,
            onValueChange = onValueChange,
            onValueApplyRequest = onValueApplyRequest,
            modifier = modifier,
            enabled = enabled,
        )
    }

    @Composable
    override fun IntItem(
        onValueApplyRequest: (Int) -> Boolean,
        title: String,
        modifier: Modifier,
        description: String?,
        value: Int,
        editorError: String?,
        onValueChange: ((String) -> String)?,
        enabled: Boolean,
    ) {
        IntSettingItem(
            title = title,
            description = description,
            value = value,
            editorError = editorError,
            onEditorShowRequest = onEditorOpen,
            onEditorCloseRequest = onEditorClose,
            onValueChange = onValueChange,
            onValueApplyRequest = onValueApplyRequest,
            modifier = modifier,
            enabled = enabled,
        )
    }

    @Composable
    override fun FloatItem(
        onValueApplyRequest: (Float) -> Boolean,
        title: String,
        modifier: Modifier,
        description: String?,
        value: String,
        editorError: String?,
        onValueChange: ((String) -> String)?,
        enabled: Boolean,
    ) {
        FloatSettingItem(
            title = title,
            modifier = modifier,
            description = description,
            value = value.toFloatOrNull() ?: 0f,
            valueString = { it.toString() },
            onEditorShowRequest = onEditorOpen,
            onEditorCloseRequest = onEditorClose,
            onValueChange = onValueChange,
            onValueApplyRequest = onValueApplyRequest,
            editorError = editorError,
            enabled = enabled,
        )
    }

    @Composable
    override fun FloatItem(
        onValueApplyRequest: (Float) -> Boolean,
        title: String,
        modifier: Modifier,
        description: String?,
        value: Float,
        editorError: String?,
        onValueChange: ((String) -> String)?,
        enabled: Boolean,
    ) {
        FloatSettingItem(
            title = title,
            modifier = modifier,
            description = description,
            value = value,
            valueString = { it.toString() },
            onEditorShowRequest = onEditorOpen,
            onEditorCloseRequest = onEditorClose,
            onValueChange = onValueChange,
            onValueApplyRequest = onValueApplyRequest,
            editorError = editorError,
            enabled = enabled,
        )
    }

    @Composable
    override fun SwitchItem(
        onChange: (Boolean) -> Unit,
        title: String,
        value: Boolean,
        modifier: Modifier,
        description: String?,
        enabled: Boolean,
    ) {
        SwitchSettingItem(
            title = title,
            value = value,
            onValueChange = onChange,
            description = description,
            modifier = modifier,
            enabled = enabled,
        )
    }

    @Composable
    override fun SliderItem(
        onValueApplyRequest: (Float) -> Unit,
        title: String,
        modifier: Modifier,
        description: String?,
        valueString: @Composable ((Float) -> String)?,
        value: RangedValue<Float>,
        enabled: Boolean,
    ) {
        SliderSettingItem(
            title = title,
            modifier = modifier,
            description = description,
            value = value,
            valueString = valueString,
            onEditorOpenRequest = onEditorOpen,
            onEditorCloseRequest = onEditorClose,
            onValueApplyRequest = onValueApplyRequest,
            enabled = enabled,
        )
    }

    @Composable
    override fun <T> ChoiceItem(
        onValueApplyRequest: (T) -> Unit,
        title: String,
        modifier: Modifier,
        description: String?,
        itemString: @Composable ((T) -> String?)?,
        items: List<T>,
        selectedItem: T,
        radioButtonEnabled: Boolean,
        enabled: Boolean,
    ) {
        ChoiceSettingItem(
            title = title,
            description = description,
            itemString = itemString,
            items = items,
            selectedItem = selectedItem,
            onEditorOpenRequest = onEditorOpen,
            onEditorCloseRequest = onEditorClose,
            onValueApplyRequest = onValueApplyRequest,
            radioButtonEnabled = radioButtonEnabled,
            enabled = enabled,
        )
    }

    @Composable
    override fun <T> ChoiceItem(
        onValueApplyRequest: (T) -> Unit,
        title: String,
        modifier: Modifier,
        description: String?,
        itemString: Map<T, String?>,
        items: List<T>,
        selectedItem: T,
        radioButtonEnabled: Boolean,
        enabled: Boolean,
    ) {
        ChoiceItem(
            onValueApplyRequest = onValueApplyRequest,
            title = title,
            description = description,
            itemString = { itemString[it] ?: "" },
            items = items,
            selectedItem = selectedItem,
            radioButtonEnabled = radioButtonEnabled,
            enabled = enabled,
        )
    }
}
