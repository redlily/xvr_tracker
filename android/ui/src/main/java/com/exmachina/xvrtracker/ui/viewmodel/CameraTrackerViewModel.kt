package com.exmachina.xvrtracker.ui.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exmachina.xvrtracker.data.repository.AnalyzedDataPlotServerSettingsRepository
import com.exmachina.xvrtracker.data.repository.CameraSettingsRepository
import com.exmachina.xvrtracker.data.repository.PoseLandmarkerSettingsRepository
import com.exmachina.xvrtracker.data.type.CameraLensFacingType
import com.exmachina.xvrtracker.domain.service.ImageTrackerService
import com.exmachina.xvrtracker.domain.service.PlotAnalyzedDataServerService
import com.exmachina.xvrtracker.domain.workflow.TrackingDataNetworkHealthCheckWorkflow
import com.exmachina.xvrtracker.domain.workflow.TrackingDataNetworkHealthCheckWorkflow.ResumeState
import com.exmachina.xvrtracker.domain.workflow.TrackingDataNetworkHealthCheckWorkflow.SuspendState
import com.exmachina.xvrtracker.ui.state.CameraTrackerUiState
import com.exmachina.xvrtracker.ui.state.CameraTrackerUiState.ContentState
import com.exmachina.xvrtracker.ui.state.CameraTrackerUiState.DialogState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class CameraTrackerViewModel @Inject constructor(
    private val poseLandmarkerSettingsRepository: PoseLandmarkerSettingsRepository,
    private val cameraSettingsRepository: CameraSettingsRepository,
    private val imageTrackerService: ImageTrackerService,
    private val analyzedDataPlotServerSettingsRepository: AnalyzedDataPlotServerSettingsRepository,
    private val plotAnalyzedDataServerService: PlotAnalyzedDataServerService,
    private val trackingDataNetworkHealthCheckWorkflow: TrackingDataNetworkHealthCheckWorkflow,
) : ViewModel() {

    private var isActive: Boolean = false

    private var networkHealthCheckJob: Job? = null

    private val _uiStateFlow = MutableStateFlow(CameraTrackerUiState())
    val uiStateFlow = _uiStateFlow.asStateFlow()

    init {
        viewModelScope.launch {
            cameraSettingsRepository.lensFacingType.collect { lensFacingType ->
                _uiStateFlow.update { it.copy(lensFacingType = lensFacingType) }
            }
        }

        viewModelScope.launch {
            cameraSettingsRepository.zoomRatio.collect { zoomRatio ->
                _uiStateFlow.update { it.copy(zoomRatio = zoomRatio) }
            }
        }

        viewModelScope.launch {
            combine(
                imageTrackerService.poseLandmarksDataFlow,
                imageTrackerService.poseTrackersDataFlow,
            ) { poseLandmarksData, poseTrackersData ->
                _uiStateFlow.update {
                    val current = it.contentState as? ContentState.CameraActive ?: return@update it
                    it.copy(
                        contentState = current.copy(
                            poseLandmarksData = poseLandmarksData,
                            poseTrackersData = poseTrackersData,
                        ),
                    )
                }
            }.launchIn(this)
        }

        viewModelScope.launch {
            poseLandmarkerSettingsRepository.dataFlow.collect { poseLandmarkerSettings ->
                if (uiStateFlow.value.contentState is ContentState.CameraActive) {
                    _uiStateFlow.update {
                        val current = it.contentState as? ContentState.CameraActive ?: return@update it
                        it.copy(
                            contentState = current.copy(
                                visibilityThreshold = poseLandmarkerSettings.visibilityThreshold.value,
                                presenceThreshold = poseLandmarkerSettings.presenceThreshold.value,
                            ),
                        )
                    }
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            imageTrackerService.poseLandmarksDataFlow.collect {
                plotAnalyzedDataServerService.updatePoseLandmarksData(it)
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            imageTrackerService.poseTrackersDataFlow.collect {
                plotAnalyzedDataServerService.updatePoseTrackersData(it)
            }
        }
    }

    override fun onCleared() {
        imageTrackerService.dispose()
    }

    fun onStart() {
        imageTrackerService.resume()
    }

    fun onStop() {
        isActive = false
        networkHealthCheckJob?.cancel()
        networkHealthCheckJob = null
        imageTrackerService.suspend()
        viewModelScope.launch(Dispatchers.IO) {
            plotAnalyzedDataServerService.stop()
        }
    }

    fun onActive() {
        isActive = true
        viewModelScope.launch {
            val isRunning = trackingDataNetworkHealthCheckWorkflow.isRunning()
            val isSuspended = trackingDataNetworkHealthCheckWorkflow.isSuspended()
            if (isRunning && isSuspended) {
                trackingDataNetworkHealthCheckWorkflow.resume(ResumeState.Retry)
            }
        }
    }

    fun onInactive() {
        isActive = false
        when (uiStateFlow.value.dialogState) {
            is DialogState.CameraPermissionDenied,
            is DialogState.PrivateNetworkNotConnected,
            is DialogState.DestinationAddressResolutionFailed,
            is DialogState.GlobalAddressSpecified,
            is DialogState.UnknownNetworkState,
                -> _uiStateFlow.update { it.copy(dialogState = null) }

            null -> Unit
        }
    }

    fun onCameraPermissionGranted() {
        _uiStateFlow.update {
            it.copy(contentState = ContentState.CameraActive())
        }
        onNetworkHealthCheck()
    }

    fun onCameraPermissionRejected() {
        _uiStateFlow.update {
            it.copy(
                contentState = ContentState.CameraPermissionDenied,
                dialogState = DialogState.CameraPermissionDenied,
            )
        }
    }

    fun onCameraSwitch() {
        viewModelScope.launch {
            val newLensFacingType = if (cameraSettingsRepository.lensFacingType() == CameraLensFacingType.BACK) {
                CameraLensFacingType.FRONT
            } else {
                CameraLensFacingType.BACK
            }
            cameraSettingsRepository.lensFacingType(newLensFacingType)
        }
    }

    private fun onNetworkHealthCheck() {
        Timber.d("network health check")
        viewModelScope.launch(Dispatchers.IO) {
            trackingDataNetworkHealthCheckWorkflow.execute(
                onSuspend = { state ->
                    when (state) {
                        SuspendState.PrivateNetworkNotConnected ->
                            onPrivateNetworkNotConnected()

                        SuspendState.DestinationAddressResolvingFailed ->
                            onDestinationAddressResolutionFailed()

                        SuspendState.GlobalAddressSpecified ->
                            onGlobalAddressSpecified()

                        SuspendState.UnknownNetworkState ->
                            onUnknownNetworkError()
                    }
                },
                onComplete = {
                    viewModelScope.launch {
                        onNetworkReady()
                    }
                },
                onAbort = {},
            )
        }
    }

    private fun onNetworkReady() {
        Timber.d("network ready")
        viewModelScope.launch {
            imageTrackerService.startNetwork()
            if (analyzedDataPlotServerSettingsRepository.dataFlow.first().isEnabled) {
                plotAnalyzedDataServerService.start()
            }
        }
    }

    private fun onPrivateNetworkNotConnected() {
        if (isActive) {
            _uiStateFlow.update {
                it.copy(dialogState = DialogState.PrivateNetworkNotConnected)
            }
        }
    }

    private fun onDestinationAddressResolutionFailed() {
        if (isActive) {
            _uiStateFlow.update {
                it.copy(dialogState = DialogState.DestinationAddressResolutionFailed)
            }
        }
    }

    private fun onGlobalAddressSpecified() {
        if (isActive) {
            _uiStateFlow.update {
                it.copy(dialogState = DialogState.GlobalAddressSpecified)
            }
        }
    }

    private fun onUnknownNetworkError() {
        if (isActive) {
            _uiStateFlow.update {
                it.copy(dialogState = DialogState.UnknownNetworkState)
            }
        }
    }

    fun onAnalysisImageUpdate(
        bitmap: Bitmap,
        rotation: Float,
    ) {
        imageTrackerService.updateAnalysisImage(
            bitmap = bitmap,
            rotation = rotation,
        )
    }

    fun onDialogClose() {
        when (uiStateFlow.value.dialogState) {
            DialogState.CameraPermissionDenied,
            DialogState.DestinationAddressResolutionFailed,
            DialogState.GlobalAddressSpecified,
            DialogState.PrivateNetworkNotConnected,
            DialogState.UnknownNetworkState,
                ->
                viewModelScope.launch {
                    trackingDataNetworkHealthCheckWorkflow.resume(ResumeState.Confirm)
                }

            null -> Unit
        }
        _uiStateFlow.update { it.copy(dialogState = null) }
    }
}
