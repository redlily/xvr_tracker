package com.exmachina.xvrtracker.ui.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.exmachina.xvrtracker.ui.R
import com.exmachina.xvrtracker.ui.theme.app.AppTheme

@Composable
fun LaunchContent() {
    val subtitle = remember {
        val subtitles = listOf(
            R.string.launch_subtitle_1,
            R.string.launch_subtitle_2,
        )
        subtitles.random()
    }

    Scaffold(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Spacer(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1.0f),
            )

            Image(
                modifier = Modifier.size(128.dp),
                painter = painterResource(R.drawable.app_logo),
                contentDescription = null,
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1.0f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
            ) {
                Text(
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    text = stringResource(R.string.launch_title),
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    text = stringResource(subtitle),
                )

                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }
}

@Preview
@Composable
private fun LaunchContentPreview() {
    AppTheme {
        LaunchContent()
    }
}
