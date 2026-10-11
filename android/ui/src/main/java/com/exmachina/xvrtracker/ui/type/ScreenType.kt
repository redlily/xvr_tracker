package com.exmachina.xvrtracker.ui.type

import kotlinx.serialization.Serializable

@Serializable
object ScreenType {

    @Serializable
    object LaunchScreen

    @Serializable
    object OnboardingScreen

    @Serializable
    object TopScreen

    @Serializable
    object SettingsScreen {

        @Serializable
        object VrcConnectionScreen

        @Serializable
        object BodyMeasurementsScreen

        @Serializable
        object PoseLandmarkerScreen

        @Serializable
        object PoseTrackerAdvancedScreen

        @Serializable
        object PoseTrackerScreen

        @Serializable
        object DebugScreen

        @Serializable
        object OssLicensesScreen
    }

    @Serializable
    object CameraTracker
}
