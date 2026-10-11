package com.exmachina.xvrtracker.ui.content.onboarding

import androidx.compose.foundation.Image
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
fun WelcomePage(
    modifier: Modifier = Modifier,
) {
    Onboarding(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Title(
            title = stringResource(R.string.onboarding_welcome_title),
            subTitle = stringResource(R.string.onboarding_welcome_subtitle),
        )

        Spacer(modifier = Modifier.height(64.dp))

        Image(
            modifier = Modifier.size(180.dp),
            painter = painterResource(R.drawable.app_logo),
            contentDescription = null,
        )

        Spacer(modifier = Modifier.height(64.dp))

        Body(body = stringResource(R.string.onboarding_welcome_body))
    }
}

@Preview
@Composable
fun WelcomePagePreview() {
    AppTheme {
        Surface {
            WelcomePage()
        }
    }
}
