package com.exmachina.xvrtracker.ui.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.exmachina.xvrtracker.common.type.limits
import com.exmachina.xvrtracker.ui.R
import com.exmachina.xvrtracker.ui.content.onboarding.AskBodyHeightPage
import com.exmachina.xvrtracker.ui.content.onboarding.AskConnectionAddressPage
import com.exmachina.xvrtracker.ui.content.onboarding.CompletePage
import com.exmachina.xvrtracker.ui.content.onboarding.HowToCalibrateTrackerInVrcPage
import com.exmachina.xvrtracker.ui.content.onboarding.HowToEnableOscInVrcPage
import com.exmachina.xvrtracker.ui.content.onboarding.HowToTestOscConnectionInVrcPage
import com.exmachina.xvrtracker.ui.content.onboarding.PermissionSetupPage
import com.exmachina.xvrtracker.ui.content.onboarding.WelcomePage
import com.exmachina.xvrtracker.ui.dialog.onboarding.CalibrateTrackerInVrcImageDisplayDialog
import com.exmachina.xvrtracker.ui.dialog.onboarding.EnableOscInVrcImageDisplayDialog
import com.exmachina.xvrtracker.ui.dialog.onboarding.HeightOutOfLimitsDialog
import com.exmachina.xvrtracker.ui.dialog.onboarding.IncorrectIpAddressDialog
import com.exmachina.xvrtracker.ui.dialog.onboarding.PermissionSetupImageDisplayDialog
import com.exmachina.xvrtracker.ui.dialog.onboarding.TestOscConnectionInVrcImageDisplayDialog
import com.exmachina.xvrtracker.ui.state.OnboardingUiState
import com.exmachina.xvrtracker.ui.state.OnboardingUiState.DialogState
import com.exmachina.xvrtracker.ui.state.OnboardingUiState.DialogState.BodyHeightOutOfRangeDialogState
import com.exmachina.xvrtracker.ui.state.OnboardingUiState.DialogState.IncorrectNetworkAddressDialogState
import com.exmachina.xvrtracker.ui.theme.app.AppTheme
import com.exmachina.xvrtracker.ui.type.OnboardingPageType
import com.exmachina.xvrtracker.ui.widget.TextButton
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingContent(
    uiState: OnboardingUiState,
    onHeightUpdate: (Float?) -> Boolean,
    onConnectionAddressUpdate: (String) -> Boolean,
    onComplete: () -> Unit,
    onSkip: () -> Unit,
    onPermissionSetupImageDisplay: () -> Unit,
    onEnableOscInVrcImageDisplay: () -> Unit,
    onTestOscConnectionInVrcImageDisplay: () -> Unit,
    onCalibrateTrackerInVrcImageDisplay: () -> Unit,
    onDialogClose: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val softwareKeyboardController = LocalSoftwareKeyboardController.current

    val keyboardVisible = WindowInsets.isImeVisible
    var keyboardHideNecessary by remember(keyboardVisible) {
        mutableStateOf(keyboardVisible)
    }

    val coroutineScope = rememberCoroutineScope()

    val initializedContent = uiState.content as? OnboardingUiState.Content.Initialized

    val (height, setHeight) = remember(initializedContent) {
        mutableStateOf(initializedContent?.bodyHeight?.value?.let { "%.1f".format(it * 100f) } ?: "0.0")
    }
    val (connectionAddress, setConnectionAddress) = remember(initializedContent) {
        mutableStateOf(initializedContent?.connectionAddress ?: "")
    }

    val pagerState = rememberPagerState(pageCount = { OnboardingPageType.entries.size })
    val gotoPage = { page: Int ->
        coroutineScope.launch {
            pagerState.animateScrollToPage(page)
        }
    }
    val prevPage: () -> Unit = prevPage@{
        if (keyboardHideNecessary) {
            keyboardHideNecessary = false
            softwareKeyboardController?.hide()
            focusManager.clearFocus()
            return@prevPage
        }

        gotoPage(pagerState.currentPage - 1)
    }
    val nextPage: (Boolean) -> Unit = nextPage@{ force: Boolean ->
        if (keyboardHideNecessary) {
            keyboardHideNecessary = false
            softwareKeyboardController?.hide()
            focusManager.clearFocus()
            return@nextPage
        }

        val pageType = OnboardingPageType.entries.getOrNull(pagerState.currentPage)
        val can = force || when (pageType) {
            OnboardingPageType.AskBodyHeight -> {
                onHeightUpdate(height.toFloatOrNull()?.let { it / 100f })
            }

            OnboardingPageType.AskConnectionAddress -> {
                onConnectionAddressUpdate(connectionAddress)
            }

            else -> true
        }
        if (can) {
            gotoPage(pagerState.currentPage + 1)
        }
    }

    Scaffold(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) { innerPadding ->
        when (uiState.content) {
            is OnboardingUiState.Content.Initializing ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }

            is OnboardingUiState.Content.Initialized -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                ) {
                    Box(
                        modifier = Modifier.weight(1.0f),
                    ) {
                        HorizontalPager(
                            state = pagerState,
                            userScrollEnabled = false,
                        ) { page ->
                            val pageType = OnboardingPageType.entries.getOrNull(page)
                            when (pageType) {
                                OnboardingPageType.Welcome ->
                                    WelcomePage()

                                OnboardingPageType.AskBodyHeight ->
                                    AskBodyHeightPage(
                                        height = height,
                                        setHeight = setHeight,
                                    )

                                OnboardingPageType.AskConnectionAddress ->
                                    AskConnectionAddressPage(
                                        connectionAddress = connectionAddress,
                                        setConnectionAddress = setConnectionAddress,
                                    )

                                OnboardingPageType.PermissionSetup ->
                                    PermissionSetupPage(
                                        onImageClick = onPermissionSetupImageDisplay,
                                    )

                                OnboardingPageType.HowToEnableOscInVrc ->
                                    HowToEnableOscInVrcPage(
                                        onImageClick = onEnableOscInVrcImageDisplay,
                                    )

                                OnboardingPageType.HowToTestOscConnectionVrc ->
                                    HowToTestOscConnectionInVrcPage(
                                        onImageClick = onTestOscConnectionInVrcImageDisplay,
                                    )

                                OnboardingPageType.HowToCalibrateInVrc ->
                                    HowToCalibrateTrackerInVrcPage(
                                        onImageClick = onCalibrateTrackerInVrcImageDisplay,
                                    )

                                OnboardingPageType.Complete ->
                                    CompletePage()

                                null ->
                                    onComplete()
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 16.dp),
                    ) {
                        if (pagerState.currentPage > 0) {
                            TextButton(
                                text = stringResource(R.string.back),
                                onClick = { prevPage() },
                            )
                        } else {
                            TextButton(
                                text = stringResource(R.string.onboarding_skip),
                                onClick = onSkip,
                            )
                        }

                        Spacer(modifier = Modifier.weight(1.0f))

                        if (pagerState.currentPage < pagerState.pageCount - 1) {
                            TextButton(
                                text = stringResource(R.string.next),
                                onClick = { nextPage(false) },
                            )
                        } else {
                            TextButton(
                                text = stringResource(R.string.onboarding_complete),
                                onClick = onComplete,
                            )
                        }
                    }
                }
            }
        }
    }

    when (uiState.dialogState) {
        is BodyHeightOutOfRangeDialogState ->
            HeightOutOfLimitsDialog(
                limits = uiState.dialogState.limits,
                onClose = onDialogClose,
                onSkip = {
                    onDialogClose()
                    nextPage(true)
                },
            )

        is IncorrectNetworkAddressDialogState ->
            IncorrectIpAddressDialog(
                onClose = onDialogClose,
                onSkip = {
                    onDialogClose()
                    nextPage(true)
                },
            )

        DialogState.PermissionSetupDialogState ->
            PermissionSetupImageDisplayDialog(
                onClose = onDialogClose,
            )

        DialogState.EnableOscInVrcDialogState ->
            EnableOscInVrcImageDisplayDialog(
                onClose = onDialogClose,
            )

        DialogState.TestOscConnectionInVrcDialogState ->
            TestOscConnectionInVrcImageDisplayDialog(
                onClose = onDialogClose,
            )

        DialogState.CalibrateTrackerInVrcDialogState ->
            CalibrateTrackerInVrcImageDisplayDialog(
                onClose = onDialogClose,
            )

        null -> Unit
    }
}

@Preview
@Composable
private fun OnboardingContentPreview() {
    AppTheme {
        OnboardingContent(
            uiState = OnboardingUiState(
                content = OnboardingUiState.Content.Initialized(
                    bodyHeight = 178f limits 0.5f..3f,
                    connectionAddress = "192.168.10.1",
                ),
            ),
            onHeightUpdate = { true },
            onConnectionAddressUpdate = { true },
            onComplete = {},
            onSkip = {},
            onPermissionSetupImageDisplay = {},
            onEnableOscInVrcImageDisplay = {},
            onTestOscConnectionInVrcImageDisplay = {},
            onCalibrateTrackerInVrcImageDisplay = {},
            onDialogClose = {},
        )
    }
}
