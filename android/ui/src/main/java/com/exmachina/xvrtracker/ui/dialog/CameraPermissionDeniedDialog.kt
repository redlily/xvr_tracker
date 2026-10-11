package com.exmachina.xvrtracker.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.VideocamOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.exmachina.xvrtracker.common.ui.iconSpec
import com.exmachina.xvrtracker.ui.R
import com.exmachina.xvrtracker.ui.theme.app.AppTheme
import com.exmachina.xvrtracker.ui.widget.TextButton

@Composable
fun CameraPermissionDeniedDialog(
    onClose: () -> Unit,
    onAppInfoOpen: () -> Unit,
) {
    AlertDialog(
        icon = iconSpec(imageVector = Icons.Rounded.VideocamOff),
        title = {
            Text(text = stringResource(R.string.camera_tracker_no_camera_permission_dialog_title))
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(text = stringResource(R.string.camera_tracker_no_camera_permission_dialog_description_android))

                TextButton(
                    text = stringResource(R.string.open_app_info_android),
                    onClick = onAppInfoOpen,
                )
            }
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
private fun CameraPermissionDeniedDialogPreview() {
    AppTheme {
        CameraPermissionDeniedDialog(
            onClose = {},
            onAppInfoOpen = {},
        )
    }
}
