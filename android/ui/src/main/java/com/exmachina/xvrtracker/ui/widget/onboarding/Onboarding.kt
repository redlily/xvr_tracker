package com.exmachina.xvrtracker.ui.widget.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun Onboarding(
    modifier: Modifier = Modifier,
    content: @Composable OnboardingScope.() -> Unit,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        content = { OnboardingScopeImpl().content() },
    )
}

interface OnboardingScope {

    @Composable
    fun Title(
        title: String,
        subTitle: String,
        modifier: Modifier = Modifier,
    )

    @Composable
    fun Body(
        body: String,
        modifier: Modifier = Modifier,
    )

    @Composable
    fun List(
        items: List<@Composable () -> Unit>,
        modifier: Modifier = Modifier,
    )
}

private class OnboardingScopeImpl : OnboardingScope {

    @Composable
    override fun Title(
        title: String,
        subTitle: String,
        modifier: Modifier,
    ) {
        OnboardingTitle(
            title = title,
            subTitle = subTitle,
            modifier = modifier,
        )
    }

    @Composable
    override fun Body(body: String, modifier: Modifier) {
        OnboardingBody(
            body = body,
            modifier = modifier,
        )
    }

    @Composable
    override fun List(
        items: List<@Composable (() -> Unit)>,
        modifier: Modifier,
    ) {
        OnboardingIndexedList(
            items = items,
            modifier = modifier,
        )
    }
}
