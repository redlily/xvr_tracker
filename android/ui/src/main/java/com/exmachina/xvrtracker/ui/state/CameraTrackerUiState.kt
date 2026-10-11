package com.exmachina.xvrtracker.ui.state

import androidx.compose.runtime.Immutable
import com.exmachina.xvrtracker.data.type.CameraLensFacingType
import com.exmachina.xvrtracker.data.type.PoseLandmarksData
import com.exmachina.xvrtracker.domain.type.PoseTrackersData

data class CameraTrackerUiState(
    val lensFacingType: CameraLensFacingType? = null,
    val zoomRatio: Float? = null,
    val contentState: ContentState = ContentState.Initializing,
    val dialogState: DialogState? = null,
) {

    @Immutable
    sealed interface ContentState {

        data object Initializing : ContentState

        data class CameraActive(
            val poseLandmarksData: PoseLandmarksData = PoseLandmarksData.empty,
            val poseTrackersData: PoseTrackersData = PoseTrackersData.empty,
            val visibilityThreshold: Float = 0f,
            val presenceThreshold: Float = 0f,
        ) : ContentState

        data object CameraPermissionDenied : ContentState
    }

    @Immutable
    sealed interface DialogState {

        data object CameraPermissionDenied : DialogState

        data object PrivateNetworkNotConnected : DialogState

        data object DestinationAddressResolutionFailed : DialogState

        data object GlobalAddressSpecified : DialogState

        data object UnknownNetworkState : DialogState
    }
}
