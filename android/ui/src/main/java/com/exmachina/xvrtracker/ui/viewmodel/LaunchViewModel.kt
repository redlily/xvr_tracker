package com.exmachina.xvrtracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exmachina.xvrtracker.data.repository.AnalyzedDataPlotServerSettingsRepository
import com.exmachina.xvrtracker.data.repository.BodyMeasurementSettingsRepository
import com.exmachina.xvrtracker.data.repository.CameraSettingsRepository
import com.exmachina.xvrtracker.data.repository.OnboardingSettingsRepository
import com.exmachina.xvrtracker.data.repository.PoseLandmarkerSettingsRepository
import com.exmachina.xvrtracker.data.repository.PoseModificationSettingsRepository
import com.exmachina.xvrtracker.data.repository.PoseTrackerSettingsRepository
import com.exmachina.xvrtracker.data.repository.VrcConnectionSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LaunchViewModel @Inject constructor(
    private val onboardingSettingsRepository: OnboardingSettingsRepository,
    private val vrcConnectionSettingsRepository: VrcConnectionSettingsRepository,
    private val bodyMeasurementSettingsRepository: BodyMeasurementSettingsRepository,
    private val poseLandmarkerSettingsRepository: PoseLandmarkerSettingsRepository,
    private val poseTrackerSettingsRepository: PoseTrackerSettingsRepository,
    private val poseModificationSettingsRepository: PoseModificationSettingsRepository,
    private val analyzedDataPlotServerSettingsRepository: AnalyzedDataPlotServerSettingsRepository,
    private val cameraSettingsRepository: CameraSettingsRepository,
) : ViewModel() {

    companion object {

        private const val MIN_SHOWING_TIME = 2000L
    }

    private val _onBoardingNavigateFlow = MutableSharedFlow<Unit>()
    val onBoardingNavigateFlow = _onBoardingNavigateFlow.asSharedFlow()

    private val _onTopNavigateFlow = MutableSharedFlow<Unit>()
    val onTopNavigateFlow = _onTopNavigateFlow.asSharedFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val startTime = System.currentTimeMillis()

            onboardingSettingsRepository.preload()
            vrcConnectionSettingsRepository.preload()
            bodyMeasurementSettingsRepository.preload()
            poseLandmarkerSettingsRepository.preload()
            poseTrackerSettingsRepository.preload()
            poseModificationSettingsRepository.preload()
            analyzedDataPlotServerSettingsRepository.preload()
            cameraSettingsRepository.preload()

            val delayTime = MIN_SHOWING_TIME - (System.currentTimeMillis() - startTime)
            if (delayTime > 0) {
                delay(delayTime)
            }

            if (!onboardingSettingsRepository.done()) {
                _onBoardingNavigateFlow.emit(Unit)
            } else {
                _onTopNavigateFlow.emit(Unit)
            }
        }
    }
}
