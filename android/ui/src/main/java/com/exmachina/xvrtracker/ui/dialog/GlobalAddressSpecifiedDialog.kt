package com.exmachina.xvrtracker.ui.dialog

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Public
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
fun GlobalAddressSpecifiedDialog(
    onClose: () -> Unit,
) {
    AlertDialog(
        icon = iconSpec(imageVector = Icons.Outlined.Public),
        title = {
            Text(text = stringResource(R.string.camera_tracker_external_host_specified_dialog_title))
        },
        text = {
            Text(text = stringResource(R.string.camera_tracker_external_host_specified_dialog_description))
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
private fun GlobalAddressSpecifiedDialogPreview() {
    AppTheme {
        GlobalAddressSpecifiedDialog(
            onClose = {},
        )
    }
}
