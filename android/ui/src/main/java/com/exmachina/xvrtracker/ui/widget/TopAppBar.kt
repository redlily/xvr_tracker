package com.exmachina.xvrtracker.ui.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.exmachina.xvrtracker.common.ui.iconSpec
import com.exmachina.xvrtracker.ui.R
import com.exmachina.xvrtracker.ui.theme.app.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    canNavigateBack: Boolean = false,
    onBack: (() -> Unit)? = null,
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                icon?.invoke()

                Text(title)
            }
        },
        navigationIcon = {
            if (canNavigateBack && onBack != null) {
                IconButton(
                    onClick = onBack,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                    )
                }
            }
        },
    )
}

@Composable
fun TopAppBar(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = false,
    onBack: (() -> Unit)? = null,
) {
    TopAppBar(
        title = title,
        modifier = modifier,
        canNavigateBack = canNavigateBack,
        icon = iconSpec(imageVector = icon),
        onBack = onBack,
    )
}

@Preview
@Composable
private fun TopAppBarPreview() {
    AppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    icon = Icons.Rounded.Build,
                    title = "Settings",
                )
            },
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding))
        }
    }
}

@Preview
@Composable
private fun TopAppBarWithBackPreview() {
    AppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    icon = Icons.Rounded.Build,
                    title = "Settings",
                    canNavigateBack = true,
                    onBack = {},
                )
            },
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding))
        }
    }
}
