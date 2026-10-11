package com.exmachina.xvrtracker.ui.screen.settings

import androidx.compose.runtime.Composable
import com.exmachina.xvrtracker.ui.content.settings.OssLicensesContent

@Composable
fun OssLicensesScreen(
    onBack: () -> Unit,
) {
    OssLicensesContent(
        onBack = onBack,
    )
}
