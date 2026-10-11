package com.exmachina.xvrtracker.ui.content

import android.view.Surface
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.LocalActivity
import androidx.activity.enableEdgeToEdge
import androidx.camera.compose.CameraXViewfinder
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.exmachina.xvrtracker.data.type.CameraLensFacingType
import com.exmachina.xvrtracker.data.type.CameraState
import com.exmachina.xvrtracker.ui.R
import com.exmachina.xvrtracker.ui.dialog.CameraPermissionDeniedDialog
import com.exmachina.xvrtracker.ui.dialog.DestinationAddressResolutionFailedDialog
import com.exmachina.xvrtracker.ui.dialog.GlobalAddressSpecifiedDialog
import com.exmachina.xvrtracker.ui.dialog.PrivateNetworkNotConnectedDialog
import com.exmachina.xvrtracker.ui.dialog.UnknownNetworkStateDialog
import com.exmachina.xvrtracker.ui.state.CameraTrackerUiState
import com.exmachina.xvrtracker.ui.state.CameraTrackerUiState.ContentState
import com.exmachina.xvrtracker.ui.state.CameraTrackerUiState.DialogState
import com.exmachina.xvrtracker.ui.theme.app.AppTheme
import com.exmachina.xvrtracker.ui.theme.camera.CameraViewTheme
import com.exmachina.xvrtracker.ui.widget.LabeledIconButton
import com.exmachina.xvrtracker.ui.widget.PlotPoseData
import kotlin.math.log10
import kotlin.math.pow

@Composable
fun CameraTrackerContent(
    uiState: CameraTrackerUiState,
    cameraState: CameraState,
    onBack: () -> Unit,
    onCameraSwitch: () -> Unit,
    onZoomChange: (Float) -> Unit,
    onDialogClose: () -> Unit,
    onAppInfoOpen: () -> Unit,
    onInternetSettingsOpen: () -> Unit,
) {
    val view = LocalView.current

    DisposableEffect(view) {
        view.keepScreenOn = true
        onDispose {
            view.keepScreenOn = false
        }
    }

    val activity = LocalActivity.current as? ComponentActivity

    DisposableEffect(activity) {
        activity?.enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )

        onDispose {
            activity?.enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.auto(
                    android.graphics.Color.TRANSPARENT,
                    android.graphics.Color.TRANSPARENT,
                ),
                navigationBarStyle = SystemBarStyle.auto(
                    android.graphics.Color.TRANSPARENT,
                    android.graphics.Color.TRANSPARENT,
                ),
            )
        }
    }

    CameraViewTheme(
        darkTheme = false,
        dynamicColor = false,
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(innerPadding),
            ) {
                when (val contentState = uiState.contentState) {
                    is ContentState.Initializing ->
                        Initializing()

                    is ContentState.CameraActive ->
                        CameraActive(
                            contentState = contentState,
                            cameraState = cameraState,
                        )

                    is ContentState.CameraPermissionDenied ->
                        NoCameraPermission()
                }

                Controller(
                    uiState = uiState,
                    cameraState = cameraState,
                    onBack = onBack,
                    onCameraSwitch = onCameraSwitch,
                    onZoomChange = onZoomChange,
                )
            }
        }
    }

    when (uiState.dialogState) {
        DialogState.CameraPermissionDenied ->
            CameraPermissionDeniedDialog(
                onClose = onDialogClose,
                onAppInfoOpen = onAppInfoOpen,
            )

        DialogState.DestinationAddressResolutionFailed ->
            DestinationAddressResolutionFailedDialog(
                onClose = onDialogClose,
            )

        DialogState.GlobalAddressSpecified ->
            GlobalAddressSpecifiedDialog(
                onClose = onDialogClose,
            )

        DialogState.PrivateNetworkNotConnected ->
            PrivateNetworkNotConnectedDialog(
                onClose = onDialogClose,
                onInternetSettingsOpen = onInternetSettingsOpen,
            )

        DialogState.UnknownNetworkState ->
            UnknownNetworkStateDialog(
                onClose = onDialogClose,
            )

        null -> Unit
    }
}

@Composable
private fun Controller(
    uiState: CameraTrackerUiState,
    cameraState: CameraState,
    onBack: () -> Unit,
    onCameraSwitch: () -> Unit,
    onZoomChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .weight(1.0f),
        ) {
            Column(
                modifier = Modifier.weight(1.0f),
                horizontalAlignment = Alignment.Start,
            ) {
                LabeledIconButton(
                    imageVector = Icons.Rounded.Close,
                    text = stringResource(R.string.back),
                    onClicked = onBack,
                )
            }

            Column {
                if (uiState.contentState is ContentState.CameraActive) {
                    LabeledIconButton(
                        imageVector = Icons.Filled.FlipCameraAndroid,
                        text = stringResource(R.string.camera_tracker_switch),
                        onClicked = onCameraSwitch,
                    )
                }
            }
        }

        if (uiState.contentState is ContentState.CameraActive) {
            ZoomController(
                cameraState = cameraState,
                onZoomChange = onZoomChange,
            )
        }
    }
}

@Composable
private fun ZoomController(
    cameraState: CameraState,
    onZoomChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val modifiedZoomRatioRange by remember(cameraState.zoomRatioRange) {
        mutableStateOf(log10(cameraState.zoomRatioRange.start)..log10(cameraState.zoomRatioRange.endInclusive))
    }
    val modifiedZoomRatio by remember(cameraState.zoomRatio) { mutableFloatStateOf(log10(cameraState.zoomRatio)) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .padding(
                top = 16.dp,
                bottom = 32.dp,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            modifier = Modifier
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
                .padding(
                    vertical = 4.dp,
                    horizontal = 16.dp,
                ),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            text = "x %.1f".format(cameraState.zoomRatio),
        )

        Slider(
            colors = SliderDefaults.colors(
                activeTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                thumbColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            valueRange = modifiedZoomRatioRange,
            value = modifiedZoomRatio,
            onValueChange = { onZoomChange(10f.pow(it)) },
        )
    }
}

@Composable
private fun Initializing(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            style = MaterialTheme.typography.bodyLarge,
            text = stringResource(R.string.camera_tracker_camera_launch),
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
}

@Composable
private fun CameraActive(
    contentState: ContentState.CameraActive,
    cameraState: CameraState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        cameraState.surfaceRequest?.let { surfaceRequest ->
            CameraXViewfinder(
                modifier = Modifier.fillMaxSize(),
                surfaceRequest = surfaceRequest,
            )
            PlotPoseData(
                rotation = cameraState.rotation,
                flip = cameraState.lensFacingType == CameraLensFacingType.FRONT,
                poseLandmarksData = contentState.poseLandmarksData,
                poseTrackersData = contentState.poseTrackersData,
                isDrawLandmarksDataEnabled = true,
                isDrawTrackersDataEnabled = true,
                visibilityThreshold = contentState.visibilityThreshold,
                presenceThreshold = contentState.presenceThreshold,
            )
        } ?: run {
            Text(
                style = MaterialTheme.typography.bodyLarge,
                text = stringResource(R.string.camera_tracker_camera_launch),
                color = Color.White,
            )
        }
    }
}

@Composable
private fun NoCameraPermission(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            style = MaterialTheme.typography.bodyLarge,
            text = stringResource(R.string.camera_tracker_no_camera_permission),
            color = Color.White,
        )
    }
}

@Preview
@Composable
fun CameraTrackerContentInitializingPreview() {
    AppTheme {
        CameraTrackerContent(
            uiState = CameraTrackerUiState(
                contentState = ContentState.Initializing,
                dialogState = null,
            ),
            cameraState = object : CameraState {
                override val isBound: Boolean = true
                override val lensFacingType = CameraLensFacingType.BACK
                override val zoomRatioRange = 0.5f..10f
                override val zoomRatio = 1f
                override val rotation = Surface.ROTATION_0
                override val surfaceRequest = null
            },
            onBack = {},
            onCameraSwitch = {},
            onZoomChange = {},
            onDialogClose = {},
            onAppInfoOpen = {},
            onInternetSettingsOpen = {},
        )
    }
}

@Preview
@Composable
fun CameraTrackerContentCameraActivePreview() {
    AppTheme {
        CameraTrackerContent(
            uiState = CameraTrackerUiState(
                contentState = ContentState.CameraActive(),
                dialogState = null,
            ),
            cameraState = object : CameraState {
                override val isBound: Boolean = true
                override val lensFacingType = CameraLensFacingType.BACK
                override val zoomRatioRange = 0.5f..10f
                override val zoomRatio = 1f
                override val rotation = Surface.ROTATION_0
                override val surfaceRequest = null
            },
            onBack = {},
            onCameraSwitch = {},
            onZoomChange = {},
            onDialogClose = {},
            onAppInfoOpen = {},
            onInternetSettingsOpen = {},
        )
    }
}
