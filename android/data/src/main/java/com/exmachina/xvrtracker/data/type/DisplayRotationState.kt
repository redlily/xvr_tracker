package com.exmachina.xvrtracker.data.type

import android.view.Display
import androidx.compose.runtime.Stable

@Stable
interface DisplayRotationState {

    val display: Display?
    val rotation: Int
}
