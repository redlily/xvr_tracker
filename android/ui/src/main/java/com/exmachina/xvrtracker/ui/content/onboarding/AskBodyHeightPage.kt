package com.exmachina.xvrtracker.ui.content.onboarding

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.exmachina.xvrtracker.ui.R
import com.exmachina.xvrtracker.ui.theme.app.AppTheme
import com.exmachina.xvrtracker.ui.widget.onboarding.Onboarding

@Composable
fun AskBodyHeightPage(
    height: String,
    setHeight: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Onboarding(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Title(
            title = stringResource(R.string.onboarding_ask_user_height_title),
            subTitle = stringResource(R.string.onboarding_ask_user_height_subtitle),
        )

        Spacer(modifier = Modifier.height(32.dp))

        TextField(
            modifier = Modifier.width(150.dp),
            textStyle = TextStyle(textAlign = TextAlign.Center),
            suffix = { Text(text = "cm") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
            ),
            value = height,
            onValueChange = { setHeight(it) },
        )

        Spacer(modifier = Modifier.height(32.dp))

        Body(body = stringResource(R.string.onboarding_ask_user_height_body))
    }
}

@Preview
@Composable
fun AskUserHeightPagePreview() {
    val (height, setHeight) = remember { mutableStateOf(175.0f.toString()) }

    AppTheme {
        Surface {
            AskBodyHeightPage(
                height = height,
                setHeight = setHeight,
            )
        }
    }
}
