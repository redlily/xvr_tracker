package com.exmachina.xvrtracker.ui.widget.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.exmachina.xvrtracker.common.ui.iconSpec
import com.exmachina.xvrtracker.ui.theme.app.AppTheme

@Composable
fun SettingTitle(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    description: (@Composable () -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
) {
    Surface(modifier = modifier) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ProvideContentColor(
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                enabled = enabled,
            ) {
                leadingContent?.invoke()
            }

            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .weight(1.0f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ProvideContentColorTextStyle(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    textStyle = MaterialTheme.typography.titleSmall,
                    enabled,
                ) {
                    title()
                }

                ProvideContentColorTextStyle(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    textStyle = MaterialTheme.typography.bodySmall,
                    enabled = enabled,
                ) {
                    description?.invoke()
                }
            }

            ProvideContentColor(
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                enabled = enabled,
            ) {
                trailingContent?.invoke()
            }
        }
    }
}

@Composable
fun SettingTitle(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
) {
    SettingTitle(
        title = { Text(text = title) },
        modifier = modifier,
        description = description?.let { { Text(text = description) } },
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        enabled = enabled,
    )
}

@Composable
fun SettingItem(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    value: (@Composable () -> Unit)? = null,
    description: (@Composable () -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
) {
    Surface(modifier = modifier) {
        Box(
            modifier = Modifier.clickable {
                onClick?.invoke()
            },
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                ProvideContentColor(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    enabled = enabled,
                ) {
                    leadingContent?.invoke()
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.0f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ProvideContentColorTextStyle(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        textStyle = MaterialTheme.typography.titleMedium,
                        enabled = enabled,
                    ) {
                        title()
                    }

                    ProvideContentColorTextStyle(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        textStyle = MaterialTheme.typography.bodySmall,
                        enabled = enabled,
                    ) {
                        description?.invoke()

                        value?.invoke()
                    }
                }

                ProvideContentColor(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    enabled = enabled,
                ) {
                    trailingContent?.invoke()
                }
            }
        }
    }
}

@Composable
private fun ProvideContentColor(
    contentColor: Color,
    enabled: Boolean,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalContentColor provides if (enabled) {
            contentColor
        } else {
            contentColor.copy(alpha = 0.38f)
        },
        content = content,
    )
}

@Composable
private fun ProvideContentColorTextStyle(
    contentColor: Color,
    textStyle: TextStyle,
    enabled: Boolean,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalContentColor provides if (enabled) {
            contentColor
        } else {
            contentColor.copy(alpha = 0.38f)
        },
        LocalTextStyle provides textStyle,
        content = content,
    )
}

@Composable
fun SettingItem(
    title: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    value: String? = null,
    description: String? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
) {
    SettingItem(
        title = { Text(text = title) },
        modifier = modifier,
        onClick = onClick,
        value = value?.let { { Text(text = value) } },
        description = description?.let { { Text(text = description) } },
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        enabled = enabled,
    )
}

@Composable
fun SettingItem(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onEditorShowRequest: (@Composable () -> Unit) -> Unit,
    value: (@Composable () -> Unit)? = null,
    description: (@Composable () -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    editorContent: @Composable () -> Unit,
    enabled: Boolean = true,
) {
    SettingItem(
        title = title,
        modifier = modifier,
        onClick = { onEditorShowRequest(editorContent) },
        value = value,
        description = description,
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        enabled = enabled,
    )
}

@Composable
fun SettingItem(
    title: String,
    modifier: Modifier = Modifier,
    onEditorShowRequest: (@Composable () -> Unit) -> Unit,
    value: String? = null,
    description: String? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    editorContent: @Composable () -> Unit,
    enabled: Boolean = true,
) {
    SettingItem(
        title = { Text(text = title) },
        modifier = modifier,
        onEditorShowRequest = onEditorShowRequest,
        value = value?.let { { Text(text = value) } },
        description = description?.let { { Text(text = description) } },
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        editorContent = editorContent,
        enabled = enabled,
    )
}

@Composable
fun SettingDivider(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
    ) {
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        )
    }
}

@Preview
@Composable
fun SettingItemPreview() {
    AppTheme {
        Column {
            SettingTitle(
                title = "Setting Title",
            )

            SettingTitle(
                title = "Setting Title",
                description = "Description",
            )

            SettingItem(
                title = "Setting Item",
            )

            SettingItem(
                title = "Setting Item",
                value = "Value",
            )

            SettingItem(
                title = "Setting Item",
                description = "Description",
                value = "Value",
            )

            SettingItem(
                title = "Setting Item",
                description = "Description",
                value = "Value",
                enabled = false,
            )

            SettingDivider()

            SettingTitle(
                title = "Setting Title",
                leadingContent = iconSpec(imageVector = Icons.AutoMirrored.Default.Help),
                trailingContent = iconSpec(imageVector = Icons.AutoMirrored.Default.Help),
            )

            SettingItem(
                title = "Setting Item",
                description = "Description",
                value = "Value",
                leadingContent = iconSpec(imageVector = Icons.AutoMirrored.Default.Help),
                trailingContent = {
                    Switch(
                        checked = true,
                        onCheckedChange = {},
                    )
                },
            )

            SettingItem(
                title = "Setting Item",
                description = "Description",
                value = "Value",
                enabled = false,
                leadingContent = iconSpec(imageVector = Icons.AutoMirrored.Default.Help),
                trailingContent = {
                    Switch(
                        checked = true,
                        onCheckedChange = {},
                        enabled = false,
                    )
                },
            )
        }
    }
}
