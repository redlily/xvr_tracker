package com.exmachina.xvrtracker.ui.widget.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.exmachina.xvrtracker.ui.theme.app.AppTheme

@Composable
fun OnboardingTitle(
    title: String,
    modifier: Modifier = Modifier,
    subTitle: String? = null,
) {
    val typography = MaterialTheme.typography
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            style = typography.titleLarge,
            color = colorScheme.onSurface,
            textAlign = TextAlign.Center,
            text = title,
        )

        if (subTitle != null) {
            Text(
                style = typography.titleMedium,
                color = colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                text = subTitle,
            )
        }
    }
}

@Preview
@Composable
fun TitlePreview() {
    AppTheme {
        Surface {
            OnboardingTitle(
                modifier = Modifier.padding(16.dp),
                title = "title",
                subTitle = "subtitle",
            )
        }
    }
}
