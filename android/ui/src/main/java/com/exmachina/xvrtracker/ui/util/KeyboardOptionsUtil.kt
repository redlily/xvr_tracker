package com.exmachina.xvrtracker.ui.util

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

val KeyboardOptions.Companion.NumberOnly
    get() = Default.copy(keyboardType = KeyboardType.Number)
