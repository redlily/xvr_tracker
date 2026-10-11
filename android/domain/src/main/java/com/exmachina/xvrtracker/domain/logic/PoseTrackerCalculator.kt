package com.exmachina.xvrtracker.domain.logic

import com.exmachina.xvrtracker.common.math.Matrix4x4
import com.exmachina.xvrtracker.common.math.Vector3
import com.exmachina.xvrtracker.data.repository.PoseTrackerSettingsRepository
import com.exmachina.xvrtracker.data.type.PoseLandmarksData
import com.exmachina.xvrtracker.domain.type.PoseTrackerData
import com.exmachina.xvrtracker.domain.type.PoseTrackerType
import com.exmachina.xvrtracker.domain.type.PoseTrackersData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import kotlin.math.acos
import kotlin.math.max
import kotlin.math.min

class PoseTrackerCalculator @Inject constructor(
    poseTrackerSettingsRepository: PoseTrackerSettingsRepository,
    val poseDirectionAnalyzer: PoseDirectionAnalyzer,
    val poseJointAnalyzer: PoseJointAnalyzer,
) {

    private val poseTrackerSettingsFlow = poseTrackerSettingsRepository.dataFlow

    private val _poseTrackersDataFlow = MutableStateFlow(PoseTrackersData.empty)
    val poseTrackersDataFlow = _poseTrackersDataFlow.asStateFlow()

    private var upper: Vector3 = Vector3(0f, 1f, 0f)
    private var modificationMatrix = Matrix4x4.IDENTITY

    fun reset() {
        poseDirectionAnalyzer.reset()
        _poseTrackersDataFlow.value = PoseTrackersData.empty
    }

    fun updateUpperDirection(direction: Vector3) {
        upper = direction.normalize()
        val cs = max(-1f, min(1f, upper dot Vector3.Y))
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

    suspend fun updateLandmarksData(landmarksData: PoseLandmarksData) {
        val directionsData = poseDirectionAnalyzer.analyze(landmarksData = landmarksData)
        val jointsData = poseJointAnalyzer.analyze(directionsData = directionsData)
        val trackersData =
            jointsData.map { joints ->
                val trackersData = mutableMapOf<PoseTrackerType, PoseTrackerData>()
                val poseTrackerSettings = poseTrackerSettingsFlow.first()

                joints.head?.let { joint ->
                    trackersData[PoseTrackerType.Head] =
                        PoseTrackerData(
                            dataType = poseTrackerSettings.head,
                            attitude =
                                Matrix4x4.column(
                                    joint.axis,
                                    joint.position,
                                ),
                        )
                }

                joints.chest?.let { joint ->
                    trackersData[PoseTrackerType.Chest] =
                        PoseTrackerData(
                            dataType = poseTrackerSettings.chest,
                            attitude =
                                Matrix4x4.column(
                                    joint.axis,
                                    joint.position,
                                ),
                        )
                }

                joints.waist?.let { joint ->
                    trackersData[PoseTrackerType.Waist] =
                        PoseTrackerData(
                            dataType = poseTrackerSettings.waist,
                            attitude =
                                Matrix4x4.column(
                                    joint.axis,
                                    joint.position,
                                ),
                        )
                }

                joints.leftArm.elbow?.let { joint ->
                    trackersData[PoseTrackerType.LeftElbow] =
                        PoseTrackerData(
                            dataType = poseTrackerSettings.elbow,
                            attitude =
                                Matrix4x4.column(
                                    joint.axis,
                                    joint.position,
                                ),
                        )
                }

                joints.rightArm.elbow?.let { joint ->
                    trackersData[PoseTrackerType.RightElbow] =
                        PoseTrackerData(
                            dataType = poseTrackerSettings.elbow,
                            attitude =
                                Matrix4x4.column(
                                    joint.axis,
                                    joint.position,
                                ),
                        )
                }

                joints.leftLeg.knee?.let { joint ->
                    trackersData[PoseTrackerType.LeftKnee] =
                        PoseTrackerData(
                            dataType = poseTrackerSettings.knee,
                            attitude =
                                Matrix4x4.column(
                                    joint.axis,
                                    joint.position,
                                ),
                        )
                }

                joints.rightLeg.knee?.let { joint ->
                    trackersData[PoseTrackerType.RightKnee] =
                        PoseTrackerData(
                            dataType = poseTrackerSettings.knee,
                            attitude =
                                Matrix4x4.column(
                                    joint.axis,
                                    joint.position,
                                ),
                        )
                }

                joints.leftLeg.feet?.let { joint ->
                    trackersData[PoseTrackerType.LeftFeet] =
                        PoseTrackerData(
                            dataType = poseTrackerSettings.feet,
                            attitude =
                                Matrix4x4.column(
                                    joint.axis,
                                    joint.position,
                                ),
                        )
                }

                joints.rightLeg.feet?.let { joint ->
                    trackersData[PoseTrackerType.RightFeet] =
                        PoseTrackerData(
                            dataType = poseTrackerSettings.feet,
                            attitude =
                                Matrix4x4.column(
                                    joint.axis,
                                    joint.position,
                                ),
                        )
                }

                trackersData
            }

        _poseTrackersDataFlow.value =
            PoseTrackersData(
                trackers = trackersData,
                modificationMatrix = modificationMatrix,
                timestamp = landmarksData.timestamp,
            )
    }
}
