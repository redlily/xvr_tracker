package com.exmachina.xvrtracker.ui.widget.dialog

import androidx.annotation.IntRange
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.exmachina.xvrtracker.common.type.RangedValue
import com.exmachina.xvrtracker.ui.R
import com.exmachina.xvrtracker.ui.theme.app.AppTheme
import com.exmachina.xvrtracker.ui.widget.TextButton

@Composable
fun SliderDialog(
    onDismissRequest: () -> Unit,
    onApplyRequest: (Float) -> Unit,
    title: String,
    value: Float,
    modifier: Modifier = Modifier,
    description: String? = null,
    valueString: (@Composable (Float) -> String)? = null,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    @IntRange(from = 0) steps: Int = 0,
) {
    var sliderValue by remember { mutableFloatStateOf(value) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        confirmButton = {
            TextButton(
                text = stringResource(R.string.apply),
                onClick = {
                    onApplyRequest(sliderValue)
                    onDismissRequest()
                },
            )
        },
        title = { Text(text = title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                description?.let {
                    Text(text = it)
                }

                Text(text = valueString?.invoke(sliderValue) ?: sliderValue.toString())

                Slider(
                    value = sliderValue,
                    valueRange = valueRange,
                    onValueChange = {
                        sliderValue = it
                    },
                    steps = steps,
                )
            }
        },
        dismissButton = {
            TextButton(
                text = stringResource(R.string.cancel),
                onClick = onDismissRequest,
            )
        },
    )
}

@Composable
fun SliderDialog(
    onDismissRequest: () -> Unit,
    onApplyRequest: (Float) -> Unit,
    title: String,
    value: RangedValue<Float>,
    modifier: Modifier = Modifier,
    description: String? = null,
    valueString: (@Composable (Float) -> String)? = null,
) {
    SliderDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        onApplyRequest = onApplyRequest,
        title = title,
        value = value.value,
        valueRange = value.range.start..value.range.endInclusive,
        valueString = valueString,
        description = description,
    )
}

@Preview
@Composable
fun SliderEditDialogPreview() {
    AppTheme {
        SliderDialog(
            onDismissRequest = {},
            onApplyRequest = {},
            title = "title",
            description = "description",
            value = 2f,
            valueString = { it.toString() },
            valueRange = 1f..3f,
        )
    }
}
