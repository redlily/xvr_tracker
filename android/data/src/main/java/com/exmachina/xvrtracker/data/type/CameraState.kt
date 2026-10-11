package com.exmachina.xvrtracker.data.type

import androidx.camera.core.SurfaceRequest
import androidx.compose.runtime.Stable

@Stable
interface CameraState {

    val lensFacingType: CameraLensFacingType
    val zoomRatioRange: ClosedFloatingPointRange<Float>
    val zoomRatio: Float
    val rotation: Int
    val isBound: Boolean
    val surfaceRequest: SurfaceRequest?
}
