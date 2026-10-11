package com.exmachina.xvrtracker.ui.screen

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.exmachina.xvrtracker.ui.screen.settings.addSettingsGraph
import com.exmachina.xvrtracker.ui.theme.app.AppTheme
import com.exmachina.xvrtracker.ui.type.ScreenType
import com.exmachina.xvrtracker.ui.type.ScreenType.SettingsScreen

@Composable
fun AppScreen() {
    val navController = rememberNavController()

    AppTheme {
        NavHost(
            navController = navController,
            startDestination = ScreenType.LaunchScreen,
            enterTransition = { slideIn { fullSize -> IntOffset(0, fullSize.height) } },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { slideOut { fullSize -> IntOffset(0, fullSize.height) } },
        ) {
            composable<ScreenType.LaunchScreen> {
                LaunchScreen(
                    onOnboarding = {
                        navController.navigate(ScreenType.OnboardingScreen) {
                            launchSingleTop = true
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                        }
                    },
                    onTop = {
                        navController.navigate(ScreenType.TopScreen) {
                            launchSingleTop = true
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                        }
                    },
                )
            }

            composable<ScreenType.OnboardingScreen> {
                OnboardingScreen(
                    onBoardingExit = {
                        if (!navController.popBackStack()) {
                            navController.navigate(ScreenType.TopScreen) {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = true
                                }
                            }
                        }
                    },
                )
            }

            composable<ScreenType.TopScreen> {
                TopScreen(
                    onVrcConnectionSettingsClick = {
                        navController.navigate(SettingsScreen.VrcConnectionScreen) {
                            launchSingleTop = true
                        }
                    },
                    onBodyMeasurementsSettingsClick = {
                        navController.navigate(SettingsScreen.BodyMeasurementsScreen) {
                            launchSingleTop = true
                        }
                    },
                    onPoseAnalyzerSettingsClick = {
                        navController.navigate(SettingsScreen.PoseLandmarkerScreen) {
                            launchSingleTop = true
                        }
                    },
                    onPoseTrackerSettingsClick = {
                        navController.navigate(SettingsScreen.PoseTrackerScreen) {
                            launchSingleTop = true
                        }
                    },
                    onPoseTrackerAdvancedSettingsClick = {
                        navController.navigate(SettingsScreen.PoseTrackerAdvancedScreen) {
                            launchSingleTop = true
                        }
                    },
                    onDebugSettingsClick = {
                        navController.navigate(SettingsScreen.DebugScreen) {
                            launchSingleTop = true
                        }
                    },
                    onOssLicensesClick = {
                        navController.navigate(SettingsScreen.OssLicensesScreen) {
                            launchSingleTop = true
                        }
                    },
                    onOnboardingClick = {
                        navController.navigate(ScreenType.OnboardingScreen) {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                        }
                    },
                    onLaunchCameraTrackerClick = {
                        navController.navigate(ScreenType.CameraTracker) {
                            launchSingleTop = true
                        }
                    },
                )
            }

            composable<ScreenType.CameraTracker> {
                CameraTrackerScreen(onBack = { navController.popBackStack() })
            }

            addSettingsGraph(navController = navController)
        }
    }
}
