package com.exmachina.xvrtracker.ui.content.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.exmachina.xvrtracker.ui.R
import com.exmachina.xvrtracker.ui.theme.app.AppTheme
import com.exmachina.xvrtracker.ui.widget.onboarding.Onboarding

@Composable
fun HowToTestOscConnectionInVrcPage(
    onImageClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Onboarding(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Title(
            title = stringResource(R.string.onboarding_how_to_test_osc_connection_title),
            subTitle = stringResource(R.string.onboarding_how_to_test_osc_connection_subtitle),
        )

        Spacer(modifier = Modifier.height(32.dp))

        Image(
            modifier = Modifier
                .size(180.dp)
                .clickable(onClick = onImageClick),
            painter = painterResource(R.drawable.onboarding_osc_debug),
            contentDescription = null,
        )

        Spacer(modifier = Modifier.height(32.dp))

        List(
            modifier = Modifier.padding(horizontal = 16.dp),
            items = listOf(
                {
                    Body(body = stringResource(R.string.onboarding_how_to_test_osc_connection_body_1))
                },
                {
                    Body(body = stringResource(R.string.onboarding_how_to_test_osc_connection_body_2))
                },
                {
                    Body(body = stringResource(R.string.onboarding_how_to_test_osc_connection_body_3))
                },
                {
                    Body(body = stringResource(R.string.onboarding_how_to_test_osc_connection_body_4))
                },
                {
                    Body(body = stringResource(R.string.onboarding_how_to_test_osc_connection_body_5))
                },
                {
                    Body(body = stringResource(R.string.onboarding_how_to_test_osc_connection_body_6))
                },
            ),
        )
    }
}

@Preview
@Composable
fun HowToTestOscConnectionPagePreview() {
    AppTheme {
        Surface {
            HowToTestOscConnectionInVrcPage(
                onImageClick = {},
            )
        }
    }
}

