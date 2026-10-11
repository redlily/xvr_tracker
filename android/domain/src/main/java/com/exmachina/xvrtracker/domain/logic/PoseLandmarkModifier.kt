package com.exmachina.xvrtracker.domain.logic

import com.exmachina.xvrtracker.common.math.Axis3
import com.exmachina.xvrtracker.common.math.Matrix4x4
import com.exmachina.xvrtracker.common.math.Vector3
import com.exmachina.xvrtracker.common.math.toRadian
import com.exmachina.xvrtracker.common.sound.beep
import com.exmachina.xvrtracker.common.util.toImmutableMap
import com.exmachina.xvrtracker.data.entity.PoseLandmarkerSettings
import com.exmachina.xvrtracker.data.entity.PoseModificationSettings
import com.exmachina.xvrtracker.data.repository.BodyMeasurementSettingsRepository
import com.exmachina.xvrtracker.data.repository.PoseLandmarkerSettingsRepository
import com.exmachina.xvrtracker.data.repository.PoseModificationSettingsRepository
import com.exmachina.xvrtracker.data.type.BodyPartCategory
import com.exmachina.xvrtracker.data.type.PoseLandmarkData
import com.exmachina.xvrtracker.data.type.PoseLandmarkType
import com.exmachina.xvrtracker.data.type.PoseLandmarksData
import com.exmachina.xvrtracker.domain.type.BodyMeasurements
import com.exmachina.xvrtracker.domain.type.PoseMotionType
import com.exmachina.xvrtracker.domain.type.bodyMeasurements
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.math.acos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

class PoseLandmarkModifier @Inject constructor(
    poseModificationSettingsRepository: PoseModificationSettingsRepository,
    poseLandmarkerSettingsRepository: PoseLandmarkerSettingsRepository,
    bodyMeasurementSettingsRepository: BodyMeasurementSettingsRepository,
) {

    private data class LandmarksStateData(
        val landmarks: Map<PoseLandmarkType, PoseLandmarkData>,
        val standTime: Long,
        val sitTime: Long,
        val lieDownTime: Long,
        val motionType: PoseMotionType,
    )

    private data class LandmarksStatesData(
        val data: List<LandmarksStateData>,
        val timestamp: Long,
    )

    private val poseLandmarkerSettingsFlow = poseLandmarkerSettingsRepository.dataFlow
    private val poseModificationSettingsFlow = poseModificationSettingsRepository.dataFlow
    private val bodyMeasurementsFlow = bodyMeasurementSettingsRepository.dataFlow.map { it.bodyMeasurements() }

    private var upper: Vector3 = Vector3(0f, 1f, 0f)
    private var modificationMatrix = Matrix4x4.IDENTITY

    private var landmarksStateData = LandmarksStatesData(
        data = emptyList(),
        timestamp = 0,
    )

    fun reset() {
        landmarksStateData =
            LandmarksStatesData(
                data = emptyList(),
                timestamp = 0,
            )
    }

    fun updateUpperDirection(direction: Vector3) {
        upper = direction.normalize()
        val cs = upper dot Vector3.Y
        modificationMatrix = if (cs > 0.99) {
            Matrix4x4.IDENTITY
        } else if (cs < -0.99) {
            Matrix4x4(
                -1f, 0f, 0f, 0f,
                0f, -1f, 0f, 0f,
                0f, 0f, 1f, 0f,
                0f, 0f, 0f, 1f,
            )
        } else {
            val axis = upper cross Vector3.Y
            Matrix4x4.rotation(axis, acos(cs))
        }
    }

    suspend fun modify(landmarksData: PoseLandmarksData): PoseLandmarksData {
        val landmarkerSettings = poseLandmarkerSettingsFlow.first()
        val modificationSettings = poseModificationSettingsFlow.first()
        val bodyMeasurements = bodyMeasurementsFlow.first()

        val deltaTime = landmarksData.timestamp - landmarksStateData.timestamp

        val filteredLandmarks = filter(
            landmarks = landmarksData.landmarks,
            landmarkerSettings = landmarkerSettings,
        )
        val correctedLandmarks =
            correct(
                landmarks = filteredLandmarks,
                modificationSettings = modificationSettings,
                bodyMeasurements = bodyMeasurements,
            )
        val stabilizedLandmarks =
            stabilize(
                landmarks = correctedLandmarks,
                landmarkerSettings = landmarkerSettings,
                modificationSettings = modificationSettings,
                deltaTime = deltaTime,
            )

        updateState(
            landmarksData = landmarksData,
            landmarks = stabilizedLandmarks,
            modificationSettings = modificationSettings,
            deltaTime = deltaTime,
        )

        return landmarksData.copy(landmarks = stabilizedLandmarks)
    }

    private fun filter(
        landmarks: List<Map<PoseLandmarkType, PoseLandmarkData>>,
        landmarkerSettings: PoseLandmarkerSettings,
    ): List<Map<PoseLandmarkType, PoseLandmarkData>> =
        landmarks.map { landmarks ->
            landmarks.filter { (_, landmark) ->
                landmark.visibility > landmarkerSettings.visibilityThreshold.value &&
                    landmark.presence > landmarkerSettings.presenceThreshold.value
            }
        }

    private fun correct(
        landmarks: List<Map<PoseLandmarkType, PoseLandmarkData>>,
        modificationSettings: PoseModificationSettings,
        bodyMeasurements: BodyMeasurements,
    ): List<Map<PoseLandmarkType, PoseLandmarkData>> =
        landmarks.mapIndexed { index, currentLandmarks ->
            val leftShoulder = currentLandmarks[PoseLandmarkType.LeftShoulder]
            val rightShoulder = currentLandmarks[PoseLandmarkType.RightShoulder]
            val leftHip = currentLandmarks[PoseLandmarkType.LeftHip]
            val rightHip = currentLandmarks[PoseLandmarkType.RightHip]

            val hipAxis: Axis3?
            val worldMatrix: Matrix4x4?

            if (leftShoulder != null && rightShoulder != null && leftHip != null && rightHip != null) {
                val shoulder = Vector3.center(
                    leftShoulder.worldLandmark,
                    rightShoulder.worldLandmark,
                )
                val hip = Vector3.center(
                    leftHip.worldLandmark,
                    rightHip.worldLandmark,
                )
                val hipToShoulder = Vector3.direction(
                    from = hip,
                    to = shoulder,
                )
                val hipToSide = Vector3.direction(
                    from = leftHip.worldLandmark,
                    to = rightHip.worldLandmark,
                )
                hipAxis = Axis3.xzy(
                    x = hipToSide,
                    ref = hipToShoulder,
                )

                val axis = (Vector3.Z cross hipToShoulder).normalize()
                val effective = hipAxis.z dot Vector3.Z
                worldMatrix = Matrix4x4.rotation(
                    axis = axis,
                    angle = if (effective > 0f) {
                        modificationSettings.bodyPositiveSlope.value * effective
                    } else {
                        modificationSettings.bodyNegativeSlope.value * -effective
                    },
                    isDegree = true,
                )
            } else {
                hipAxis = null
                worldMatrix = null
            }

            landmarksStateData.data.getOrNull(index)?.let { landmarksState ->
                currentLandmarks.toMutableMap().also { newLandmarks ->
                    correctHip(
                        landmarks = newLandmarks,
                        bodyMeasurements = bodyMeasurements,
                    )

                    if (hipAxis != null) {
                        imputeLegs(
                            landmarks = newLandmarks,
                            hipAxis = hipAxis,
                            bodyMeasurements = bodyMeasurements,
                        )

                        if (landmarksState.motionType == PoseMotionType.Stand) {
                            correctKnees(
                                landmarks = newLandmarks,
                                hipAxis = hipAxis,
                                modificationSettings = modificationSettings,
                            )
                        }

                        if (
                            landmarksState.motionType == PoseMotionType.Sit ||
                            landmarksState.motionType == PoseMotionType.LieDown
                        ) {
                            restrictLegs(
                                landmarks = newLandmarks,
                                hipAxis = hipAxis,
                                motionType = landmarksState.motionType,
                            )
                        }
                    }

                    if (worldMatrix != null) {
                        newLandmarks.entries.forEach {
                            it.setValue(it.value.copy(worldLandmark = worldMatrix * it.value.worldLandmark))
                        }
                    }
                }.toImmutableMap()
            } ?: currentLandmarks
        }

    private fun correctHip(
        landmarks: MutableMap<PoseLandmarkType, PoseLandmarkData>,
        bodyMeasurements: BodyMeasurements,
    ) {
        val leftShoulder = landmarks[PoseLandmarkType.LeftShoulder]
        val rightShoulder = landmarks[PoseLandmarkType.RightShoulder]
        val leftHip = landmarks[PoseLandmarkType.LeftHip]
        val rightHip = landmarks[PoseLandmarkType.RightHip]
        if (leftShoulder == null || rightShoulder == null || leftHip == null || rightHip == null) {
            return
        }

        val widthRatio = bodyMeasurements.hipWidth / bodyMeasurements.shoulderWidth
        val shoulderWidth = (rightShoulder.worldLandmark - leftShoulder.worldLandmark).length
        val hipWidth = shoulderWidth * widthRatio

        val shoulderCenter = Vector3.center(leftShoulder.worldLandmark, rightShoulder.worldLandmark)
        val hipCenter = Vector3.center(leftHip.worldLandmark, rightHip.worldLandmark)

        val shoulderAxis =
            Axis3.yzx(
                y = hipCenter - shoulderCenter,
                ref = rightShoulder.worldLandmark - leftShoulder.worldLandmark,
            )

        val leftToRight = rightHip.worldLandmark - leftHip.worldLandmark

        val depth = shoulderAxis.z * (leftToRight dot shoulderAxis.z)
        val height = shoulderAxis.y * (leftToRight dot shoulderAxis.y)
        val widthSq = hipWidth * hipWidth - (height dot height) - (depth dot depth)
        if (widthSq <= 0f) {
            return
        }
        val width = shoulderAxis.x * sqrt(widthSq)

        landmarks[PoseLandmarkType.LeftHip] =
            leftHip.copy(
                worldLandmark = hipCenter + (width + height + depth) / 2f,
            )
        landmarks[PoseLandmarkType.RightHip] =
            rightHip.copy(
                worldLandmark = hipCenter - (width + height + depth) / 2f,
            )
    }

    private fun correctKnees(
        landmarks: MutableMap<PoseLandmarkType, PoseLandmarkData>,
        hipAxis: Axis3,
        modificationSettings: PoseModificationSettings,
    ) {
        correctKnee(
            landmarks = landmarks,
            hipAxis = hipAxis,
            isLeft = true,
            modificationSettings = modificationSettings,
        )
        correctKnee(
            landmarks = landmarks,
            hipAxis = hipAxis,
            isLeft = false,
            modificationSettings = modificationSettings,
        )
    }

    private fun correctKnee(
        landmarks: MutableMap<PoseLandmarkType, PoseLandmarkData>,
        hipAxis: Axis3,
        modificationSettings: PoseModificationSettings,
        isLeft: Boolean,
    ) {
        val hip = landmarks[if (isLeft) PoseLandmarkType.LeftHip else PoseLandmarkType.RightHip]
        val knee = landmarks[if (isLeft) PoseLandmarkType.LeftKnee else PoseLandmarkType.RightKnee]
        if (hip == null || knee == null) {
            return
        }

        val kneeToHip = hip.worldLandmark - knee.worldLandmark
        val kneeAxis = Axis3.yzx(
            y = kneeToHip,
            ref = hipAxis.x,
        )

        val effective = kneeAxis.z dot Vector3.Z
        val matrix = Matrix4x4.translation(-hip.worldLandmark) *
            Matrix4x4.rotation(
                axis = kneeAxis.x,
                angle = if (effective < 0) {
                    modificationSettings.kneePositiveSlope.value * effective
                } else {
                    modificationSettings.kneeNegativeSlope.value * -effective
                },
                isDegree = true,
            ) * Matrix4x4.translation(hip.worldLandmark)

        applyMatrix(
            landmarks = landmarks,
            target = if (isLeft) PoseLandmarkType.LeftKnee else PoseLandmarkType.RightKnee,
            matrix = matrix,
        )
    }

    private fun restrictLegs(
        landmarks: MutableMap<PoseLandmarkType, PoseLandmarkData>,
        hipAxis: Axis3,
        motionType: PoseMotionType,
    ) {
        restrictLeg(
            landmarks = landmarks,
            hipAxis = hipAxis,
            isLeft = true,
            motionType = motionType,
        )
        restrictLeg(
            landmarks = landmarks,
            hipAxis = hipAxis,
            isLeft = false,
            motionType = motionType,
        )
    }

    private fun imputeLegs(
        landmarks: MutableMap<PoseLandmarkType, PoseLandmarkData>,
        hipAxis: Axis3,
        bodyMeasurements: BodyMeasurements,
    ) {
        imputeLeg(
            landmarks = landmarks,
            hipAxis = hipAxis,
            isLeft = true,
            bodyMeasurements = bodyMeasurements,
        )
        imputeLeg(
            landmarks = landmarks,
            hipAxis = hipAxis,
            isLeft = false,
            bodyMeasurements = bodyMeasurements,
        )
    }

    private fun imputeLeg(
        landmarks: MutableMap<PoseLandmarkType, PoseLandmarkData>,
        hipAxis: Axis3,
        isLeft: Boolean,
        bodyMeasurements: BodyMeasurements,
    ) {
        val hip = landmarks[if (isLeft) PoseLandmarkType.LeftHip else PoseLandmarkType.RightHip] ?: return

        val knee = landmarks[if (isLeft) PoseLandmarkType.LeftKnee else PoseLandmarkType.RightKnee]
        var ankle = landmarks[if (isLeft) PoseLandmarkType.LeftAnkle else PoseLandmarkType.RightAnkle]

        val heel = landmarks[if (isLeft) PoseLandmarkType.LeftHeel else PoseLandmarkType.RightHeel]
        if (heel != null && ankle == null) {
            ankle =
                heel.copy(
                    visibility = 0f,
                    presence = 0f,
                )
            landmarks[if (isLeft) PoseLandmarkType.LeftAnkle else PoseLandmarkType.RightAnkle] = ankle
        }

        if (
            ankle != null &&
            knee == null
        ) {
            val oppositeKnee = landmarks[if (isLeft) PoseLandmarkType.RightKnee else PoseLandmarkType.LeftKnee]
            val oppositeHip = landmarks[if (isLeft) PoseLandmarkType.RightHip else PoseLandmarkType.LeftHip]

            if (oppositeKnee != null && oppositeHip != null) {
                val hipToAnkle = ankle.worldLandmark - hip.worldLandmark
                val hypotenuse = (oppositeKnee.worldLandmark - oppositeHip.worldLandmark).length
                val base = hipToAnkle.length / 2f
                val heightSq = hypotenuse * hypotenuse - base * base
                val height = if (heightSq > 0f) sqrt(heightSq) else 0f

                val kneePosition =
                    Matrix4x4.translation(-hip.worldLandmark) *
                        Matrix4x4.rotation(from = -hipAxis.y, to = hipToAnkle) *
                        Matrix4x4.translation(hip.worldLandmark) *
                        (-hipAxis.y * base + hipAxis.z * height)

                landmarks[if (isLeft) PoseLandmarkType.LeftKnee else PoseLandmarkType.RightKnee] =
                    PoseLandmarkData(
                        normalizedLandmark = Vector3.ZERO,
                        worldLandmark = kneePosition,
                        visibility = 0f,
                        presence = 0f,
                    )
            }
        }
    }

    private fun restrictLeg(
        landmarks: MutableMap<PoseLandmarkType, PoseLandmarkData>,
        hipAxis: Axis3,
        isLeft: Boolean,
        motionType: PoseMotionType,
    ) {
        val hip = landmarks[if (isLeft) PoseLandmarkType.LeftHip else PoseLandmarkType.RightHip]
        if (hip == null) {
            return
        }

        val ankle: PoseLandmarkData
        val ankleData = landmarks[if (isLeft) PoseLandmarkType.LeftAnkle else PoseLandmarkType.RightAnkle]
        if (ankleData != null) {
            ankle = ankleData
        } else {
            val knee = landmarks[if (isLeft) PoseLandmarkType.LeftKnee else PoseLandmarkType.RightKnee]

            if (knee != null &&
                landmarks[if (isLeft) PoseLandmarkType.LeftHeel else PoseLandmarkType.RightHeel] == null &&
                landmarks[if (isLeft) PoseLandmarkType.LeftFootIndex else PoseLandmarkType.RightFootIndex] == null &&
                motionType == PoseMotionType.Sit
            ) {
                val hipToKnee = hip.worldLandmark - knee.worldLandmark
                val kneeAxis = (hipAxis.z cross upper).normalize()
                val kneeToAnkle = (kneeAxis cross upper).normalize() * hipToKnee.length

                landmarks[if (isLeft) PoseLandmarkType.LeftAnkle else PoseLandmarkType.RightAnkle] =
                    PoseLandmarkData(
                        normalizedLandmark = Vector3.ZERO,
                        worldLandmark = knee.worldLandmark + kneeToAnkle,
                        visibility = 0f,
                        presence = 0f,
                    )
            }
            return
        }

        val hipToAnkle = Vector3.direction(
            from = hip.worldLandmark,
            to = ankle.worldLandmark,
        )
        val cs = hipToAnkle dot -upper
        if (cs < 0) {
            return
        }

        val axis = (hipToAnkle cross upper).normalize()
        if (hipAxis.x dot axis > 0) {
            return
        }

        val to = (upper cross axis).normalize()

        val mat = Matrix4x4.translation(-hip.worldLandmark) *
            Matrix4x4.rotation(from = hipToAnkle, to = to) *
            Matrix4x4.translation(hip.worldLandmark)

        applyMatrix(
            landmarks = landmarks,
            target = if (isLeft) PoseLandmarkType.LeftKnee else PoseLandmarkType.RightKnee,
            matrix = mat,
        )
        applyMatrix(
            landmarks = landmarks,
            target = if (isLeft) PoseLandmarkType.LeftAnkle else PoseLandmarkType.RightAnkle,
            matrix = mat,
        )
        applyMatrix(
            landmarks = landmarks,
            target = if (isLeft) PoseLandmarkType.LeftHeel else PoseLandmarkType.RightHeel,
            matrix = mat,
        )
        applyMatrix(
            landmarks = landmarks,
            target = if (isLeft) PoseLandmarkType.LeftFootIndex else PoseLandmarkType.RightFootIndex,
            matrix = mat,
        )
    }

    private fun applyMatrix(
        landmarks: MutableMap<PoseLandmarkType, PoseLandmarkData>,
        target: PoseLandmarkType,
        matrix: Matrix4x4,
    ) {
        landmarks[target]?.let {
            landmarks[target] = it.copy(worldLandmark = matrix * it.worldLandmark)
        }
    }

    private fun stabilize(
        landmarks: List<Map<PoseLandmarkType, PoseLandmarkData>>,
        landmarkerSettings: PoseLandmarkerSettings,
        modificationSettings: PoseModificationSettings,
        deltaTime: Long,
    ): List<Map<PoseLandmarkType, PoseLandmarkData>> =
        landmarks.mapIndexed { index, currentLandmarks ->
            landmarksStateData.data.getOrNull(index)?.let { landmarksState ->
                currentLandmarks.mapValues { (type, currentLandmark) ->
                    val coefficient = when (landmarksState.motionType) {
                        PoseMotionType.Stand -> {
                            modificationSettings.standStabilizingCoefficient.value
                        }

                        PoseMotionType.Sit -> {
                            if (type.category == BodyPartCategory.Leg) {
                                modificationSettings.sitStabilizingCoefficient.value
                            } else {
                                modificationSettings.standStabilizingCoefficient.value
                            }
                        }

                        PoseMotionType.LieDown -> {
                            if (type.category == BodyPartCategory.Leg ||
                                type.category == BodyPartCategory.Body ||
                                type.category == BodyPartCategory.Head
                            ) {
                                modificationSettings.lieDownStabilizingCoefficient.value
                            } else {
                                modificationSettings.standStabilizingCoefficient.value
                            }
                        }
                    }
                    val alpha = coefficient.pow(deltaTime / 1000.0f * 10.0f)

                    landmarksState.landmarks[type]?.let { previousLandmark ->
                        val normalizedPosition =
                            if (
                                previousLandmark.visibility > landmarkerSettings.visibilityThreshold.value &&
                                previousLandmark.presence > landmarkerSettings.presenceThreshold.value
                            ) {
                                currentLandmark.normalizedLandmark * (1f - alpha) +
                                    previousLandmark.normalizedLandmark * alpha
                            } else {
                                currentLandmark.normalizedLandmark
                            }
                        val worldPosition =
                            currentLandmark.worldLandmark * (1f - alpha) +
                                previousLandmark.worldLandmark * alpha

                        currentLandmark.copy(
                            normalizedLandmark = normalizedPosition,
                            worldLandmark = worldPosition,
                        )
                    } ?: currentLandmark
                }
            } ?: currentLandmarks
        }

    private fun updateState(
        landmarksData: PoseLandmarksData,
        landmarks: List<Map<PoseLandmarkType, PoseLandmarkData>>,
        modificationSettings: PoseModificationSettings,
        deltaTime: Long,
    ) {
        landmarksStateData =
            landmarksData.landmarks
                .zip(landmarks)
                .mapIndexed { index, (rawLandmarks, modifiedLandmarks) ->
                    landmarksStateData.data.getOrNull(index)?.let { stateData ->
                        val leftHip = rawLandmarks[PoseLandmarkType.LeftHip]
                        val rightHip = rawLandmarks[PoseLandmarkType.RightHip]
                        if (leftHip != null && rightHip != null) {
                            val hip = (leftHip.worldLandmark + rightHip.worldLandmark) / 2f
                            val leftShoulder = rawLandmarks[PoseLandmarkType.LeftShoulder]
                            val rightShoulder = rawLandmarks[PoseLandmarkType.RightShoulder]
                            val isBodyDown =
                                if (leftShoulder != null && rightShoulder != null) {
                                    val shoulder = (leftShoulder.worldLandmark + rightShoulder.worldLandmark) / 2f
                                    acos((shoulder - hip).normalize() dot upper) > 50f.toRadian()
                                } else {
                                    false
                                }

                            val leftKnee = rawLandmarks[PoseLandmarkType.LeftKnee]
                            val rightKnee = rawLandmarks[PoseLandmarkType.RightKnee]
                            val leftAnkle = rawLandmarks[PoseLandmarkType.LeftAnkle]
                            val rightAnkle = rawLandmarks[PoseLandmarkType.RightAnkle]

                            val isKneesDown = if (leftKnee != null && rightKnee != null) {
                                val leftKneeDot = max(
                                    -1.0f,
                                    min(
                                        1.0f,
                                        Vector3.direction(
                                            from = leftKnee.worldLandmark,
                                            to = leftHip.worldLandmark,
                                        ).let { it dot upper },
                                    ),
                                )
                                val rightKneeDot = max(
                                    -1.0f,
                                    min(
                                        1.0f,
                                        Vector3.direction(
                                            from = rightKnee.worldLandmark,
                                            to = rightHip.worldLandmark,
                                        ).let { it dot upper },
                                    ),
                                )
                                acos(leftKneeDot) > 60f.toRadian() && acos(rightKneeDot) > 60f.toRadian()
                            } else {
                                false
                            }

                            val isAnklesDown = if (leftAnkle != null && rightAnkle != null && leftKnee != null && rightKnee != null) {
                                val leftAnkleDot = max(
                                    -1.0f,
                                    min(
                                        1.0f,
                                        Vector3.direction(
                                            from = leftAnkle.worldLandmark,
                                            to = leftKnee.worldLandmark,
                                        ).let { it dot upper },
                                    ),
                                )
                                val rightAnkleDot = max(
                                    -1.0f,
                                    min(
                                        1.0f,
                                        Vector3.direction(
                                            from = rightAnkle.worldLandmark,
                                            to = rightKnee.worldLandmark,
                                        ).let { it dot upper },
                                    ),
                                )
                                acos(leftAnkleDot) > 60f.toRadian() && acos(rightAnkleDot) > 60f.toRadian()
                            } else {
                                leftAnkle == null && rightAnkle == null
                            }

                            val isLegsDown = if (leftAnkle != null && rightAnkle != null) {
                                val leftLegDot = max(
                                    -1.0f,
                                    min(
                                        1.0f,
                                        Vector3.direction(
                                            from = leftAnkle.worldLandmark,
                                            to = leftHip.worldLandmark,
                                        ).let { it dot upper },
                                    ),
                                )
                                val rightLegDot = max(
                                    -1.0f,
                                    min(
                                        1.0f,
                                        Vector3.direction(
                                            from = rightAnkle.worldLandmark,
                                            to = rightHip.worldLandmark,
                                        ).let { it dot upper },
                                    ),
                                )
                                acos(leftLegDot) > 45f.toRadian() && acos(rightLegDot) > 45f.toRadian()
                            } else {
                                false
                            }

                            val motionType =
                                if (isBodyDown && (isKneesDown || isAnklesDown || isLegsDown)) {
                                    PoseMotionType.LieDown
                                } else if (isKneesDown || isAnklesDown || isLegsDown) {
                                    PoseMotionType.Sit
                                } else {
                                    PoseMotionType.Stand
                                }

                            var standTime = stateData.standTime
                            var sitTime = stateData.sitTime
                            var lieDownTime = stateData.lieDownTime
                            var currentMotionType = stateData.motionType

                            val thresholdTime = modificationSettings.stabilizingModeChangeThresholdTime.value.toLong()

                            when (motionType) {
                                PoseMotionType.Stand -> {
                                    standTime = min(standTime + deltaTime, thresholdTime)
                                    sitTime = max(sitTime - deltaTime, 0)
                                    lieDownTime = max(lieDownTime - deltaTime, 0)
                                    if (currentMotionType != motionType && standTime >= thresholdTime) {
                                        standTime = 0
                                        sitTime = 0
                                        lieDownTime = 0
                                        currentMotionType = motionType
                                        beep(modificationSettings)
                                    }
                                }

                                PoseMotionType.Sit -> {
                                    standTime = max(standTime - deltaTime, 0)
                                    sitTime = min(sitTime + deltaTime, thresholdTime)
                                    lieDownTime = max(lieDownTime - deltaTime, 0)
                                    if (currentMotionType != motionType && sitTime >= thresholdTime) {
                                        standTime = 0
                                        sitTime = 0
                                        lieDownTime = 0
                                        currentMotionType = motionType
                                        beep(modificationSettings)
                                    }
                                }

                                PoseMotionType.LieDown -> {
                                    standTime = max(standTime - deltaTime, 0)
                                    sitTime = max(sitTime - deltaTime, 0)
                                    lieDownTime = min(lieDownTime + deltaTime, thresholdTime)
                                    if (currentMotionType != motionType && lieDownTime >= thresholdTime) {
                                        standTime = 0
                                        sitTime = 0
                                        lieDownTime = 0
                                        currentMotionType = motionType
                                        beep(modificationSettings)
                                    }
                                }
                            }

                            stateData.copy(
                                landmarks = modifiedLandmarks,
                                standTime = standTime,
                                sitTime = sitTime,
                                lieDownTime = lieDownTime,
                                motionType = currentMotionType,
                            )
                        } else {
                            stateData.copy(
                                landmarks = modifiedLandmarks,
                            )
                        }
                    } ?: LandmarksStateData(
                        landmarks = modifiedLandmarks,
                        standTime = 0,
                        sitTime = 0,
                        lieDownTime = 0,
                        motionType = PoseMotionType.Stand,
                    )
                }.let {
                    LandmarksStatesData(
                        data = it,
                        timestamp = landmarksData.timestamp,
                    )
                }
    }

    private fun beep(modificationSettings: PoseModificationSettings) {
        if (modificationSettings.isStabilizingModeChangeSoundEnabled) {
            beep()
        }
    }
}
