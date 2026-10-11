package com.exmachina.xvrtracker.ui.state

import androidx.compose.runtime.Immutable
import com.exmachina.xvrtracker.common.type.RangedValue

@Immutable
data class OnboardingUiState(
    val defaultConnectionAddressPrefix: String = "",
    val content: Content = Content.Initializing,
    val dialogState: DialogState? = null,
) {

    @Immutable
    sealed interface Content {

        @Immutable
        data object Initializing : Content

        @Immutable
        data class Initialized(
            val bodyHeight: RangedValue<Float>,
            val connectionAddress: String,
        ) : Content
    }

    @Immutable
    sealed interface DialogState {

        @Immutable
        data class BodyHeightOutOfRangeDialogState(
            val limits: ClosedRange<Float>,
        ) : DialogState

        @Immutable
        data object IncorrectNetworkAddressDialogState : DialogState

        @Immutable
        data object PermissionSetupDialogState : DialogState

        @Immutable
        data object EnableOscInVrcDialogState : DialogState

        @Immutable
        data object TestOscConnectionInVrcDialogState : DialogState

        @Immutable
        data object CalibrateTrackerInVrcDialogState : DialogState
    }
}
