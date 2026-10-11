package com.exmachina.xvrtracker.ui.dialog.onboarding

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Accessibility
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.exmachina.xvrtracker.ui.R
import com.exmachina.xvrtracker.ui.theme.app.AppTheme
import com.exmachina.xvrtracker.ui.widget.dialog.AlertDialog

@Composable
fun HeightOutOfLimitsDialog(
    limits: ClosedRange<Float>,
    onClose: () -> Unit,
    onSkip: () -> Unit,
) {
    AlertDialog(
        icon = Icons.Rounded.Accessibility,
        title = stringResource(R.string.onboarding_dialog_height_out_of_limits_title),
        text = stringResource(R.string.onboarding_dialog_height_out_of_limits_description)
            .format(
                limits.start * 100,
                limits.endInclusive * 100,
            ),
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
private fun HeightOutOfLimitsDialogPreview() {
    AppTheme {
        HeightOutOfLimitsDialog(
            limits = 0.5f..3f,
            onClose = {},
            onSkip = {},
        )
    }
}
