package com.exmachina.xvrtracker.ui.widget.setting

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.exmachina.xvrtracker.ui.R
import com.exmachina.xvrtracker.ui.theme.app.AppTheme
import com.exmachina.xvrtracker.ui.widget.dialog.ConfirmDialog

@Composable
fun ConfirmSettingItem(
    title: String,
    message: String? = null,
    onMakeSureShowRequest: (@Composable () -> Unit) -> Unit,
    onMakeSureCloseRequest: () -> Unit,
    onMakeSureAccept: () -> Unit,
    onMakeSureReject: (() -> Unit)? = null,
    makeSureTitle: String = title,
    makeSureMessage: String? = message,
    makeSureAccept: String = stringResource(R.string.confirm),
    makeSureReject: String? = stringResource(R.string.cancel),
) {
    SettingItem(
        title = title,
        description = message,
        onEditorShowRequest = onMakeSureShowRequest,
        editorContent = {
            ConfirmDialog(
                onDismissRequest = onMakeSureCloseRequest,
                onAccept = onMakeSureAccept,
                onReject = onMakeSureReject,
                title = makeSureTitle,
                text = makeSureMessage,
                accept = makeSureAccept,
                reject = makeSureReject,
            )
        },
    )
}

@Preview
@Composable
private fun ConfirmSettingItemPreview() {
    AppTheme {
        ConfirmSettingItem(
            title = "ConfirmItem",
            message = "Confirm Confirm Confirm Confirm",
            onMakeSureShowRequest = {},
            onMakeSureCloseRequest = {},
            onMakeSureAccept = {},
        )
    }
}
