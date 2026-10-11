package com.exmachina.xvrtracker.ui.content.onboarding

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.exmachina.xvrtracker.ui.R
import com.exmachina.xvrtracker.ui.theme.app.AppTheme
import com.exmachina.xvrtracker.ui.widget.onboarding.Onboarding

@Composable
fun AskConnectionAddressPage(
    connectionAddress: String,
    setConnectionAddress: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Onboarding(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Title(
            title = stringResource(R.string.onboarding_ask_machine_ip_address_title),
            subTitle = stringResource(R.string.onboarding_ask_machine_ip_address_subtitle),
        )

        Spacer(modifier = Modifier.height(32.dp))

        TextField(
            modifier = Modifier.width(250.dp),
            singleLine = true,
            textStyle = TextStyle(textAlign = TextAlign.Center),
            value = connectionAddress,
            onValueChange = { setConnectionAddress(it) },
        )

        Spacer(modifier = Modifier.height(32.dp))

        Body(body = stringResource(R.string.onboarding_ask_machine_ip_address_body))
    }
}

@Preview
@Composable
fun AskMachineIpAddressPagePreview() {
    val (ipAddress, setIpAddress) = remember { mutableStateOf("192.168.10.1") }

    AppTheme {
        Surface {
            AskConnectionAddressPage(
                connectionAddress = ipAddress,
                setConnectionAddress = setIpAddress,
            )
        }
    }
}
