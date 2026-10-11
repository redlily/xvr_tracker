package com.exmachina.xvrtracker.ui.widget.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.exmachina.xvrtracker.ui.R
import com.exmachina.xvrtracker.ui.theme.app.AppTheme

@Composable
fun ImageDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Dialog(
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
        ),
        onDismissRequest = onDismissRequest,
    ) {
        Box(
            modifier = modifier.padding(32.dp),
        ) {
            Box(
                modifier = Modifier
                    .shadow(4.dp)
                    .background(color = Color.White)
                    .padding(8.dp),
            ) {
                content()
            }
            Icon(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .graphicsLayer {
                        translationX = size.width / 4
                        translationY = -size.height / 4
                    }
                    .background(color = Color.White, shape = CircleShape)
                    .clickable { onDismissRequest() }
                    .padding(4.dp),
                imageVector = Icons.Rounded.Close,
                contentDescription = null,
                tint = Color.Black,
            )
        }
    }
}

@Composable
fun ImageDialog(
    painter: Painter,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ImageDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
    ) {
        val size = painter.intrinsicSize

        Image(
            modifier = Modifier.aspectRatio(size.width / size.height),
            contentScale = ContentScale.FillWidth,
            painter = painter,
            contentDescription = null,
        )
    }
}

@Preview
@Composable
private fun ImageDialogPreview() {
    AppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            ImageDialog(
                onDismissRequest = {},
            ) {
                val painter = painterResource(id = R.drawable.onboarding_osc_enable)
                val size = painter.intrinsicSize

                Image(
                    modifier = Modifier.aspectRatio(size.width / size.height),
                    contentScale = ContentScale.FillWidth,
                    painter = painter,
                    contentDescription = null,
                )
            }
        }
    }
}
