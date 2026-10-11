package com.exmachina.xvrtracker.data.source

import android.content.Context
import android.hardware.display.DisplayManager
import android.view.Display
import android.view.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import com.exmachina.xvrtracker.data.type.DisplayRotationState
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

class DisplayRotationDataSource @Inject constructor(
    @ApplicationContext context: Context,
) : DisplayRotationState {

    override var display by mutableStateOf<Display?>(null)
        private set
    override var rotation by mutableIntStateOf(Surface.ROTATION_0)
        private set

    private val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager

    private val listener = object : DisplayManager.DisplayListener {
        override fun onDisplayChanged(displayId: Int) {
            display?.let { display ->
                if (display.displayId == displayId) {
                    rotation = display.rotation
                }
            }
        }

        override fun onDisplayAdded(displayId: Int) {}

        override fun onDisplayRemoved(displayId: Int) {}
    }

    internal fun setDisplay(display: Display) {
        this.display = display
        rotation = display.rotation
    }

    fun start() {
        displayManager.registerDisplayListener(listener, null)
    }

    fun stop() {
        displayManager.unregisterDisplayListener(listener)
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface DisplayRotationDataSourceEntryPoint {

    fun displayRotationDataSource(): DisplayRotationDataSource
}

@Composable
fun rememberDisplayRotationState(): DisplayRotationState {
    val context = LocalContext.current
    val display = LocalView.current.display

    val dataSource = remember {
        EntryPointAccessors
            .fromApplication<DisplayRotationDataSourceEntryPoint>(context.applicationContext)
            .displayRotationDataSource()
    }

    LaunchedEffect(display) {
        dataSource.setDisplay(display)
    }

    DisposableEffect(dataSource) {
        dataSource.setDisplay(display)
        dataSource.start()
        onDispose { dataSource.stop() }
    }

    return dataSource
}
