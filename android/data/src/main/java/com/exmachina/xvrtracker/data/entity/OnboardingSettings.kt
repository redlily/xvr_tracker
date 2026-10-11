package com.exmachina.xvrtracker.data.entity

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class OnboardingSettings(
    val done: Boolean,
)
