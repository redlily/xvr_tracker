package com.exmachina.xvrtracker.ui.dialog.onboarding

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WrongLocation
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.exmachina.xvrtracker.ui.R
import com.exmachina.xvrtracker.ui.theme.app.AppTheme
import com.exmachina.xvrtracker.ui.widget.dialog.AlertDialog

@Composable
fun IncorrectIpAddressDialog(
    onClose: () -> Unit,
    onSkip: () -> Unit,
) {
    AlertDialog(
        icon = Icons.Rounded.WrongLocation,
        title = stringResource(R.string.onboarding_dialog_incorrect_ip_or_domain_title),
        text = stringResource(R.string.onboarding_dialog_incorrect_ip_or_domain_description),
        onDismissRequest = onClose,
        confirmButton = {
            TextButton(
                onClick = onSkip,
            ) {
                Text(text = stringResource(R.string.skip))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onClose,
            ) {
                Text(text = stringResource(R.string.confirm))
            }
        },
    )
}

@Preview
@Composable
private fun IncorrectIpAddressDialogPreview() {
    AppTheme {
        IncorrectIpAddressDialog(
            onClose = {},
            onSkip = {},
        )
    }
}
