package com.exmachina.xvrtracker.data.controller

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.view.Surface
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.core.ZoomState
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.exmachina.xvrtracker.data.controller.CameraController.AnalysisImageState
import com.exmachina.xvrtracker.data.source.rememberDisplayRotationState
import com.exmachina.xvrtracker.data.type.CameraLensFacingType
import com.exmachina.xvrtracker.data.type.CameraState
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

@Stable
interface CameraController : CameraState {

    data class AnalysisImageState(
        val bitmap: Bitmap,
        val rotation: Float,
        val timestamp: Long,
        val fps: Float,
    )

    val analysisImageFlow: SharedFlow<AnalysisImageState>

    fun bind(lensFacingType: CameraLensFacingType)

    fun unbind()

    fun updateZoomRatio(zoomRatio: Float)
}

@Stable
class MutableCameraController @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : CameraController {

    override var lensFacingType by mutableStateOf(CameraLensFacingType.BACK)
        private set
    override var zoomRatioRange by mutableStateOf(1f..1f)
        private set
    override var zoomRatio by mutableFloatStateOf(1f)
        private set
    override var rotation by mutableIntStateOf(Surface.ROTATION_0)
        private set
    override var isBound: Boolean by mutableStateOf(false)
        private set
    override var surfaceRequest by mutableStateOf<SurfaceRequest?>(null)
        private set

    private val _analysisImageFlow = MutableSharedFlow<AnalysisImageState>(extraBufferCapacity = 1)
    override val analysisImageFlow = _analysisImageFlow.asSharedFlow()

    private var isStart = false
    private var isBindingRequired = false
    private var latestTimestamp: Long = 0

    private var lifecycleOwner: LifecycleOwner? = null
    private val zoomStateObserver =
        Observer<ZoomState> { state ->
            zoomRatioRange = state.minZoomRatio..state.maxZoomRatio
            zoomRatio = state.zoomRatio
        }

    private var processCameraProvider: ProcessCameraProvider? = null
    private var camera: Camera? = null
    private var previewUseCase: Preview? = null
    private var imageAnalysisUseCase: ImageAnalysis? = null
    private var imageAnalysisExecutor: ExecutorService? = null

    private val modelScope = CoroutineScope(Dispatchers.Main)
    private val mutex = Mutex()

    private fun internalBind(lensFacingType: CameraLensFacingType) {
        modelScope.launch {
            internalUnbind()
            lifecycleOwner?.let { lifecycleOwner ->
                mutex.withLock {
                    val processCameraProvider = ProcessCameraProvider.awaitInstance(context)
                    val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacingType.value).build()

                    val previewUseCase = Preview
                        .Builder()
                        .build()
                        .apply {
                            setSurfaceProvider { newSurfaceRequest ->
                                surfaceRequest = newSurfaceRequest
                            }
                        }

                    val imageAnalysisExecutor = Executors.newSingleThreadExecutor()
                    val imageAnalysisUseCase = ImageAnalysis
                        .Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                        .setTargetRotation(Surface.ROTATION_0)
                        .setOutputImageRotationEnabled(true)
                        .build()
                        .apply {
                            setAnalyzer(imageAnalysisExecutor) { imageProxy ->
                                val currentTimestamp = SystemClock.elapsedRealtime()
                                val fps = 1000.0f / (currentTimestamp - latestTimestamp).toFloat()
                                latestTimestamp = currentTimestamp
                                _analysisImageFlow.tryEmit(
                                    AnalysisImageState(
                                        bitmap = imageProxy.toBitmap(),
                                        rotation = imageProxy.imageInfo.rotationDegrees.toFloat(),
                                        timestamp = latestTimestamp,
                                        fps = fps,
                                    ),
                                )
                                imageProxy.close()
                            }
                        }

                    try {
                        val camera = processCameraProvider.bindToLifecycle(
                            lifecycleOwner = lifecycleOwner,
                            cameraSelector = cameraSelector,
                            previewUseCase,
                            imageAnalysisUseCase,
                        )
                        camera.cameraInfo.zoomState.observe(lifecycleOwner, zoomStateObserver)
                        isBound = true

                        this@MutableCameraController.processCameraProvider = processCameraProvider
                        this@MutableCameraController.camera = camera
                        this@MutableCameraController.previewUseCase = previewUseCase
                        this@MutableCameraController.imageAnalysisUseCase = imageAnalysisUseCase
                        this@MutableCameraController.imageAnalysisExecutor = imageAnalysisExecutor
                    } catch (e: Exception) {
                        Timber.e(e)
                    }
                }
            }
        }
    }

    private suspend fun internalUnbind() {
        mutex.withLock {
            surfaceRequest = null
            processCameraProvider?.unbindAll()
            processCameraProvider = null
            camera?.cameraInfo?.zoomState?.removeObserver(zoomStateObserver)
            camera = null
            previewUseCase = null
            imageAnalysisUseCase = null
            imageAnalysisExecutor?.shutdown()
            imageAnalysisExecutor = null
            isBound = false
        }
    }

    fun setLifecycleOwner(lifecycleOwner: LifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner

        if (isStart && lifecycleOwner.lifecycle.currentState != Lifecycle.State.RESUMED) {
            stop()
        } else if (!isStart && lifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
            start()
        }
    }

    fun updateRotation(rotation: Int) {
        this.rotation = rotation
    }

    fun start() {
        isStart = true
        if (isBindingRequired) {
            internalBind(lensFacingType)
        }
    }

    fun stop() {
        isStart = false
        modelScope.launch {
            internalUnbind()
        }
    }

    override fun bind(lensFacingType: CameraLensFacingType) {
        isBindingRequired = true
        this.lensFacingType = lensFacingType
        if (isStart) {
            internalBind(lensFacingType)
        }
    }

    override fun unbind() {
        isBindingRequired = false
        modelScope.launch {
            internalUnbind()
        }
    }

    override fun updateZoomRatio(zoomRatio: Float) {
        camera?.cameraControl?.setZoomRatio(zoomRatio)
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface CameraControllerEntryPoint {

    fun cameraController(): MutableCameraController
}

@Composable
fun rememberCameraController(): CameraController {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraController = remember {
        EntryPointAccessors
            .fromApplication<CameraControllerEntryPoint>(context.applicationContext)
            .cameraController()
    }
    val displayRotationState = rememberDisplayRotationState()

    DisposableEffect(lifecycleOwner) {
        cameraController.setLifecycleOwner(lifecycleOwner)
        cameraController.updateRotation(displayRotationState.rotation)

        val observer =
            LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> cameraController.start()
                    Lifecycle.Event.ON_PAUSE -> cameraController.stop()
                    else -> Unit
                }
            }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            cameraController.unbind()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(displayRotationState) {
        cameraController.updateRotation(displayRotationState.rotation)
    }

    return cameraController
}
