package com.exmachina.xvrtracker.ui.widget.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.exmachina.xvrtracker.ui.R
import com.exmachina.xvrtracker.ui.theme.app.AppTheme

@Composable
fun <T> ChoiceDialog(
    onDismissRequest: () -> Unit,
    onChoice: (T) -> Unit,
    title: String,
    description: String? = null,
    itemString: (@Composable (T) -> String?)? = null,
    items: List<T>,
    selectedItem: T,
    radioButtonEnabled: Boolean = true,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = title) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                description?.let {
                    Text(text = it)
                }

                Column(
                    modifier = Modifier.verticalScroll(state = rememberScrollState()),
                ) {
                    items.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onChoice(item)
                                    onDismissRequest()
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            if (radioButtonEnabled) {
                                RadioButton(
                                    selected = item == selectedItem,
                                    onClick = {
                                        onChoice(item)
                                        onDismissRequest()
                                    },
                                )
                            }

                            Text(
                                modifier = Modifier.padding(vertical = 16.dp),
                                text = itemString?.invoke(item) ?: item.toString(),
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                content = {
                    Text(
                        text = stringResource(R.string.cancel),
                    )
                },
                onClick = onDismissRequest,
            )
        },
    )
}

@Preview
@Composable
private fun ChoiceDialogPreview() {
    AppTheme {
        ChoiceDialog(
            onDismissRequest = {},
            onChoice = {},
            title = "Title",
            description = "Description",
            itemString = null,
            items = listOf("Item A", "Item B", "Item C"),
            selectedItem = "Item B",
        )
    }
}

@Preview
@Composable
private fun ChoiceDialogWithoutRadioButtonPreview() {
    AppTheme {
        ChoiceDialog(
            onDismissRequest = {},
            onChoice = {},
            title = "Title",
            description = "Description",
            itemString = null,
            items = listOf("Item A", "Item B", "Item C"),
            selectedItem = "Item B",
            radioButtonEnabled = false,
        )
    }
}
