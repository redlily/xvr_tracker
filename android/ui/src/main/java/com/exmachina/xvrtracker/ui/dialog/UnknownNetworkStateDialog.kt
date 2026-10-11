package com.exmachina.xvrtracker.ui.dialog

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PhonelinkOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.exmachina.xvrtracker.common.ui.iconSpec
import com.exmachina.xvrtracker.ui.R
import com.exmachina.xvrtracker.ui.theme.app.AppTheme
import com.exmachina.xvrtracker.ui.widget.TextButton

@Composable
fun UnknownNetworkStateDialog(
    onClose: () -> Unit,
) {
    AlertDialog(
        icon = iconSpec(imageVector = Icons.Rounded.PhonelinkOff),
        title = {
            Text(text = stringResource(R.string.camera_tracker_unknown_network_error_dialog_title))
        },
        text = {
            Text(text = stringResource(R.string.camera_tracker_unknown_network_error_dialog_description_android))
        },
        onDismissRequest = onClose,
        confirmButton = {
            TextButton(
                text = stringResource(R.string.ignore),
                onClick = onClose,
            )
        },
    )
}

@Preview
@Composable
private fun UnknownNetworkStateDialogPreview() {
    AppTheme {
        UnknownNetworkStateDialog(
            onClose = {},
        )
    }
}
