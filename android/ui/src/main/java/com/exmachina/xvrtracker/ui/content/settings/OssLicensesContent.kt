package com.exmachina.xvrtracker.ui.content.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FormatListNumbered
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.exmachina.xvrtracker.ui.R
import com.exmachina.xvrtracker.ui.theme.app.AppTheme
import com.exmachina.xvrtracker.ui.widget.TopAppBar
import com.mikepenz.aboutlibraries.ui.compose.android.produceLibraries
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer

@Composable
fun OssLicensesContent(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val libraries by produceLibraries()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                icon = Icons.Rounded.FormatListNumbered,
                title = stringResource(R.string.settings_top_oss_info),
                canNavigateBack = true,
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        LibrariesContainer(
            modifier = Modifier.padding(innerPadding),
            libraries = libraries,
        )
    }
}

@Preview
@Composable
private fun OssLicensesContentPreview() {
    AppTheme {
        OssLicensesContent(
            onBack = {},
        )
    }
}
