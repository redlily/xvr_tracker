package com.exmachina.xvrtracker.ui.widget

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.exmachina.xvrtracker.ui.theme.app.AppTheme

@Composable
fun TextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TextButton(
        modifier = modifier,
        onClick = onClick,
    ) {
        Text(text = text)
    }
}

@Preview
@Composable
private fun ButtonPreview() {
    AppTheme {
        TextButton(
            text = "TextButton",
            onClick = {},
        )
    }
}
