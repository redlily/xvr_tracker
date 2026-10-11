package com.exmachina.xvrtracker.ui.screen

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exmachina.xvrtracker.data.controller.rememberCameraController
import com.exmachina.xvrtracker.ui.content.CameraTrackerContent
import com.exmachina.xvrtracker.ui.viewmodel.CameraTrackerViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraTrackerScreen(
    onBack: () -> Unit,
    viewModel: CameraTrackerViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraController = rememberCameraController()
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA) {
        if (it) {
            viewModel.onCameraPermissionGranted()
        } else {
            viewModel.onCameraPermissionRejected()
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START ->
                    viewModel.onStart()

                Lifecycle.Event.ON_STOP ->
                    viewModel.onStop()

                Lifecycle.Event.ON_RESUME -> {
                    viewModel.onActive()

                    if (cameraPermissionState.status.isGranted) {
                        viewModel.onCameraPermissionGranted()
                        uiState.lensFacingType?.let { lensFacingType ->
                            cameraController.bind(lensFacingType)
                        }
                    } else if (cameraPermissionState.status.shouldShowRationale) {
                        viewModel.onCameraPermissionRejected()
                    } else {
                        cameraPermissionState.launchPermissionRequest()
                    }
                }

                Lifecycle.Event.ON_PAUSE ->
                    viewModel.onInactive()

                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(uiState.lensFacingType) {
        if (lifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED &&
            cameraPermissionState.status.isGranted
        ) {
            uiState.lensFacingType?.let { lensFacingType ->
                cameraController.bind(lensFacingType)
            }
        }
    }

    LaunchedEffect(cameraController) {
        withContext(Dispatchers.Default) {
            cameraController.analysisImageFlow
                .collect { frame ->
                    viewModel.onAnalysisImageUpdate(
                        bitmap = frame.bitmap,
                        rotation = frame.rotation,
                    )
                }
        }
    }

    CameraTrackerContent(
        uiState = uiState,
        cameraState = cameraController,
        onBack = onBack,
        onCameraSwitch = viewModel::onCameraSwitch,
        onZoomChange = { cameraController.updateZoomRatio(it) },
        onDialogClose = viewModel::onDialogClose,
        onAppInfoOpen = {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
            try {
                context.startActivity(intent)
            } catch (_: ActivityNotFoundException) {
                val intentAllApps = Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
                context.startActivity(intentAllApps)
            }
        },
        onInternetSettingsOpen = {
            try {
                context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            } catch (_: ActivityNotFoundException) {
            }
        },
    )
}
