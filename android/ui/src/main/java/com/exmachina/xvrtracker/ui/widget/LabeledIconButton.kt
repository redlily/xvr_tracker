package com.exmachina.xvrtracker.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.exmachina.xvrtracker.ui.theme.app.AppTheme

@Composable
fun LabeledIconButton(
    imageVector: ImageVector,
    text: String,
    onClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        IconButton(
            modifier = Modifier.background(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                shape = CircleShape,
            ),
            onClick = onClicked,
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = text,
                tint = MaterialTheme.colorScheme.onPrimary,
            )
        }

        Box {
            // 1. 背面の「白い縁取り」用テキスト
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    drawStyle = Stroke(
                        width = 5f,
                        join = StrokeJoin.Round,
                    ),
                ),
            )
            // 2. 前面の「黒いメイン」テキスト
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        }
    }
}

@Preview
@Composable
private fun LabeledIconButtonPreview() {
    AppTheme {
        LabeledIconButton(
            imageVector = Icons.Rounded.Close,
            text = "back",
            onClicked = {},
        )
    }
}
