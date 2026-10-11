package com.exmachina.xvrtracker.common.math

fun Vector3.mediaPipeToUnity() = Matrix4x4.scale(-1f, -1f, -1f) * this

fun Vector3.unityToScreen() = Matrix4x4.scale(-1f, -1f, 1f) * this

fun Vector3.screenToUnity() = Matrix4x4.scale(-1f, -1f, 1f) * this

fun Vector3.sensorToUnity() = Matrix4x4.scale(-1f, 1f, 1f) * this

fun Matrix4x4.mediaPipeToUnity() = Matrix4x4.scale(-1f, -1f, -1f) * this

fun Matrix4x4.unityToScreen() = Matrix4x4.scale(-1f, -1f, 1f) * this

fun Matrix4x4.screenToUnity() = Matrix4x4.scale(-1f, -1f, 1f) * this

fun Matrix4x4.sensorToUnity() = Matrix4x4.scale(-1f, 1f, 1f) * this
