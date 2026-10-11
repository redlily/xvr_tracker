package com.exmachina.xvrtracker.ui.widget.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.exmachina.xvrtracker.ui.R
import com.exmachina.xvrtracker.ui.theme.app.AppTheme
import kotlinx.coroutines.delay

@Composable
fun StringEditDialog(
    onDismissRequest: () -> Unit,
    onApplyRequest: (String) -> Boolean,
    title: String,
    modifier: Modifier = Modifier,
    onValueChange: ((String) -> String)? = null,
    value: String = "",
    defaultValue: String = "",
    description: String? = null,
    error: String? = null,
    initialErrorVisible: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    val focusRequester = remember { FocusRequester() }
    var editingValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = value,
                selection = TextRange(value.length),
            ),
        )
    }
    var errorVisible by remember { mutableStateOf(initialErrorVisible) }

    val keyboard = LocalSoftwareKeyboardController.current

    LaunchedEffect(value) {
        editingValue = TextFieldValue(
            text = value,
            selection = TextRange(value.length),
        )
    }

    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
        keyboard?.show()
        editingValue = editingValue.copy(selection = TextRange(editingValue.text.length))
    }

    AlertDialog(
        modifier = modifier,
        title = { Text(text = title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                description?.let {
                    Text(text = it)
                }

                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    value = editingValue,
                    placeholder = {
                        Text(text = defaultValue)
                    },
                    onValueChange = { input ->
                        val processedText = onValueChange?.invoke(input.text) ?: input.text
                        editingValue = TextFieldValue(
                            text = processedText,
                            selection = TextRange(processedText.length),
                        )
                    },
                    keyboardOptions = keyboardOptions.copy(imeAction = ImeAction.Done),
                )

                if (error != null && errorVisible) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                content = {
                    Text(text = stringResource(R.string.apply))
                },
                onClick = {
                    errorVisible = !onApplyRequest(editingValue.text)

                    if (!errorVisible) {
                        onDismissRequest()
                    }
                },
            )
        },
        dismissButton = {
            TextButton(
                content = {
                    Text(text = stringResource(R.string.cancel))
                },
                onClick = onDismissRequest,
            )
        },
    )
}

@Preview
@Composable
private fun StringDialogPreview() {
    AppTheme {
        StringEditDialog(
            onDismissRequest = {},
            onValueChange = { it },
            onApplyRequest = { false },
            title = "Edit IP Address",
            value = "192.168.10.2",
            defaultValue = "Enter IP address",
            description = "Enter the destination IP address or domain name",
            error = "Invalid IP address format",
            initialErrorVisible = true,
        )
    }
}

@Preview
@Composable
private fun StringDialogWithDefaultPreview() {
    AppTheme {
        StringEditDialog(
            onDismissRequest = {},
            onValueChange = { it },
            onApplyRequest = { false },
            title = "Edit IP Address",
            value = "",
            defaultValue = "Enter IP address",
            description = "Enter the destination IP address or domain name",
            error = "Invalid IP address format",
            initialErrorVisible = true,
        )
    }
}
