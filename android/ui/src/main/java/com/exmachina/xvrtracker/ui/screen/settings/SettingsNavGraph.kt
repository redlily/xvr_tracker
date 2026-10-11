package com.exmachina.xvrtracker.ui.screen.settings

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.exmachina.xvrtracker.ui.type.ScreenType.SettingsScreen

fun NavGraphBuilder.addSettingsGraph(navController: NavHostController) {
    composableWithTransition<SettingsScreen.VrcConnectionScreen> {
        VrcConnectionSettingScreen(onBack = { navController.popBackStack() })
    }

    composableWithTransition<SettingsScreen.BodyMeasurementsScreen> {
        BodyMeasurementsSettingScreen(onBack = { navController.popBackStack() })
    }

    composableWithTransition<SettingsScreen.PoseLandmarkerScreen> {
        PoseLandmarkerSettingsScreen(onBack = { navController.popBackStack() })
    }

    composableWithTransition<SettingsScreen.PoseTrackerScreen> {
        PoseTrackerSettingsScreen(onBack = { navController.popBackStack() })
    }

    composableWithTransition<SettingsScreen.PoseTrackerAdvancedScreen> {
        PoseTrackerAdvancedSettingsScreen(onBack = { navController.popBackStack() })
    }

    composableWithTransition<SettingsScreen.DebugScreen> {
        DebugSettingsScreen(onBack = { navController.popBackStack() })
    }

    composableWithTransition<SettingsScreen.OssLicensesScreen> {
        OssLicensesScreen(onBack = { navController.popBackStack() })
    }
}

private inline fun <reified T : Any> NavGraphBuilder.composableWithTransition(
    noinline content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit,
) {
    composable<T>(
        enterTransition = { slideIn { fullSize -> IntOffset(fullSize.width, 0) } },
        exitTransition = { slideOut { fullSize -> IntOffset(-fullSize.width, 0) } },
        popEnterTransition = { slideIn { fullSize -> IntOffset(-fullSize.width, 0) } },
        popExitTransition = { slideOut { fullSize -> IntOffset(fullSize.width, 0) } },
        content = content,
    )
}
