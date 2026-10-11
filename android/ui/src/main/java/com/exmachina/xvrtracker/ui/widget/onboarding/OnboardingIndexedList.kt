package com.exmachina.xvrtracker.ui.widget.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.exmachina.xvrtracker.ui.theme.app.AppTheme

@Composable
fun OnboardingIndexedList(
    modifier: Modifier = Modifier,
    items: List<@Composable () -> Unit>,

    ) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items.forEachIndexed { index, item ->
            Row {
                Text(
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    text = "${index + 1}. ",
                )

                item()
            }
        }
    }
}

@Preview
@Composable
fun OnboardingListPreview() {
    AppTheme {
        Surface {
            OnboardingIndexedList(
                items = listOf(
                    {
                        OnboardingBody(body = "first first first")
                    },
                    {
                        OnboardingBody(body = "second second second")
                    },
                    {
                        OnboardingBody(body = "third third third")
                    },
                ),
            )
        }
    }
}
