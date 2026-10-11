import Combine
import SwiftUI
import data
import di

@MainActor
class LaunchViewModel: ObservableObject {
    private static let minShowingTime: TimeInterval = 2.0
    
    private let onboardingSettingsRepository: OnboardingSettingsRepository
    private let vrcConnectionSettingsRepository: VrcConnectionSettingsRepository
    private let bodyMeasurementsSettingsRepository: BodyMeasurementsSettingsRepository
    private let poseLandmarkerSettingsRepository: PoseLandmarkerSettingsRepository
    private let poseTrackerSettingsRepository: PoseTrackerSettingsRepository
    private let poseModificationSettingsRepository: PoseModificationSettingsRepository
    private let analyzedDataPlotServerSettingsRepository: AnalyzedDataPlotServerSettingsRepository
    private let cameraSettingsRepository: CameraSettingsRepository
    
    private let _onboardingNavigatePublisher = PassthroughSubject<Void, Never>()
    var onboardingNavigatePublisher: AnyPublisher<Void, Never> { _onboardingNavigatePublisher.eraseToAnyPublisher() }
    
    private let _topNavigatePublisher = PassthroughSubject<Void, Never>()
    var topNavigatePublisher: AnyPublisher<Void, Never> { _topNavigatePublisher.eraseToAnyPublisher() }
    
    init(
        onboardingSettingsRepository: OnboardingSettingsRepository = AppContainer.shared.resolve(),
        vrcConnectionSettingsRepository: VrcConnectionSettingsRepository = AppContainer.shared.resolve(),
        bodyMeasurementsSettingsRepository: BodyMeasurementsSettingsRepository = AppContainer.shared.resolve(),
        poseLandmarkerSettingsRepository: PoseLandmarkerSettingsRepository = AppContainer.shared.resolve(),
        poseTrackerSettingsRepository: PoseTrackerSettingsRepository = AppContainer.shared.resolve(),
        poseModificationSettingsRepository: PoseModificationSettingsRepository = AppContainer.shared.resolve(),
        analyzedDataPlotServerSettingsRepository: AnalyzedDataPlotServerSettingsRepository = AppContainer.shared.resolve(),
        cameraSettingsRepository: CameraSettingsRepository = AppContainer.shared.resolve()
    ) {
        self.onboardingSettingsRepository = onboardingSettingsRepository
        self.vrcConnectionSettingsRepository = vrcConnectionSettingsRepository
        self.bodyMeasurementsSettingsRepository = bodyMeasurementsSettingsRepository
        self.poseLandmarkerSettingsRepository = poseLandmarkerSettingsRepository
        self.poseTrackerSettingsRepository = poseTrackerSettingsRepository
        self.poseModificationSettingsRepository = poseModificationSettingsRepository
        self.analyzedDataPlotServerSettingsRepository = analyzedDataPlotServerSettingsRepository
        self.cameraSettingsRepository = cameraSettingsRepository
    }

    func onReady() {
        Task {
            let startTime = Date()

            await onboardingSettingsRepository.fetch()
            await vrcConnectionSettingsRepository.fetch()
            await bodyMeasurementsSettingsRepository.fetch()
            await poseLandmarkerSettingsRepository.fetch()
            await poseTrackerSettingsRepository.fetch()
            await poseModificationSettingsRepository.fetch()
            await analyzedDataPlotServerSettingsRepository.fetch()
            await cameraSettingsRepository.fetch()
            
            let delayTime = Self.minShowingTime - Date().timeIntervalSince(startTime)
            if delayTime > 0 {
                try? await Task.sleep(
                    nanoseconds: UInt64(delayTime * 1_000_000_000))
            }

            navigateNext()
        }
    }

    private func navigateNext() {
        if onboardingSettingsRepository.dataConsumer.value.done {
            _topNavigatePublisher.send(())
        } else {
            _onboardingNavigatePublisher.send(())
        }
        
    }
}
