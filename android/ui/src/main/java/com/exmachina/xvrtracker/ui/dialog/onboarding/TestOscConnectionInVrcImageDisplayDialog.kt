package com.exmachina.xvrtracker.ui.dialog.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.exmachina.xvrtracker.ui.R
import com.exmachina.xvrtracker.ui.theme.app.AppTheme
import com.exmachina.xvrtracker.ui.widget.dialog.ImageDialog

@Composable
fun TestOscConnectionInVrcImageDisplayDialog(
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ImageDialog(
        modifier = modifier,
        painter = painterResource(id = R.drawable.onboarding_osc_debug),
        onDismissRequest = onClose,
    )
}

@Preview
@Composable
private fun TestOscConnectionInVrcImageDisplayDialogPreview() {
    AppTheme {
        TestOscConnectionInVrcImageDisplayDialog(
            onClose = {},
        )
    }
}
