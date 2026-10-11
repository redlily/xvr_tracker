package com.exmachina.xvrtracker.ui.widget.setting

import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.exmachina.xvrtracker.ui.theme.app.AppTheme

@Composable
fun SwitchSettingItem(
    title: String,
    value: Boolean,
    onValueChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
    enabled: Boolean = true,
) {
    SettingItem(
        title = title,
        description = description,
        trailingContent = {
            Switch(
                checked = value,
                onCheckedChange = onValueChange,
                enabled = enabled,
            )
        },
        modifier = modifier,
        enabled = enabled,
    )
}

@Preview
@Composable
private fun SwitchSettingItemPreview() {
    AppTheme {
        SwitchSettingItem(
            title = "SwitchItem",
            value = true,
            onValueChange = {},
            description = "Switch Switch Switch Switch",
        )
    }
}
