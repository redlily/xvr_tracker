package com.exmachina.xvrtracker.data.type

import com.exmachina.xvrtracker.common.util.toImmutableMap
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class PoseLandmarkerModelType(
    val id: Int,
    val modelFilePath: String,
) {

    @SerialName("lite")
    LITE(
        id = 0,
        modelFilePath = "pose_landmarker/pose_landmarker_lite.task",
    ),

    @SerialName("standard")
    STANDARD(
        id = 1,
        modelFilePath = "pose_landmarker/pose_landmarker_full.task",
    );

    companion object {

        private val entryMap = entries.associateBy { it.id }.toImmutableMap()

        fun fromId(id: Int): PoseLandmarkerModelType = entryMap[id] ?: STANDARD
    }
}

fun Int.toPoseLandmarkAnalyzerModel() = PoseLandmarkerModelType.fromId(this)
