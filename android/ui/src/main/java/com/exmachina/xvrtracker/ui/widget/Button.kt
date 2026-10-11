package com.exmachina.xvrtracker.ui.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.exmachina.xvrtracker.common.ui.iconSpec
import com.exmachina.xvrtracker.ui.theme.app.AppTheme

@Composable
fun Button(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
) {
    Button(
        modifier = modifier.padding(8.dp),
        onClick = onClick,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            icon?.invoke()

            Text(
                modifier = Modifier.padding(4.dp),
                text = text,
            )
        }
    }
}

@Composable
fun Button(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        text = text,
        onClick = onClick,
        modifier = modifier,
        icon = iconSpec(imageVector = icon),
    )
}

@Preview
@Composable
private fun ButtonPreview() {
    AppTheme {
        Button(
            text = "Button",
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun ButtonWithIconPreview() {
    AppTheme {
        Button(
            icon = Icons.Rounded.Build,
            text = "Button",
            onClick = {},
        )
    }
}
