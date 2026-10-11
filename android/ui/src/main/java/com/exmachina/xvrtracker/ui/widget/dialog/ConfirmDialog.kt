package com.exmachina.xvrtracker.ui.widget.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter

@Composable
fun ConfirmDialog(
    onDismissRequest: () -> Unit,
    onAccept: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    onReject: (() -> Unit)? = null,
    icon: Painter? = null,
    text: String? = null,
    accept: String,
    reject: String? = null,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                content = { Text(text = accept) },
                onClick = {
                    onAccept()
                    onDismissRequest()
                },
            )
        },
        modifier = modifier,
        dismissButton = reject?.let {
            {
                TextButton(
                    content = { Text(text = reject) },
                    onClick = {
                        onReject?.invoke()
                        onDismissRequest()
                    },
                )
            }
        },
        icon = icon?.let { { Icon(painter = icon, contentDescription = null) } },
        title = { Text(text = title) },
        text = text?.let { { Text(text = text) } },
    )
}
