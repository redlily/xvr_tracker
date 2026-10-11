package com.exmachina.xvrtracker.common.math

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import kotlin.math.sqrt

@Immutable
@Serializable
open class Vector3(
    val x: Float,
    val y: Float,
    val z: Float,
) {

    @Immutable
    private class Zero : Vector3(0f, 0f, 0f) {

        override val isZero get() = true
        override val length get() = 0f
        override fun normalize() = this
    }

    @Immutable
    private class Normalized(
        x: Float,
        y: Float,
        z: Float,
    ) : Vector3(x, y, z) {

        override val isZero get() = false
        override val length get() = 1f
        override fun normalize() = this
    }

    @Immutable
    data class DotCross(
        val dot: Float,
        val cross: Vector3,
    )

    companion object {

        val ZERO: Vector3 = Zero()
        val X: Vector3 = Normalized(1f, 0f, 0f)
        val Y: Vector3 = Normalized(0f, 1f, 0f)
        val Z: Vector3 = Normalized(0f, 0f, 1f)

        fun center(a: Vector3, b: Vector3): Vector3 = (a + b) / 2f

        @JvmName("safeCenter")
        fun center(a: Vector3?, b: Vector3?): Vector3? =
            if (a != null && b != null) {
                center(a, b)
            } else {
                null
            }

        fun direction(from: Vector3, to: Vector3): Vector3 = (to - from).normalize()

        @JvmName("safeDirection")
        fun direction(from: Vector3?, to: Vector3?): Vector3? =
            if (from != null && to != null) {
                direction(from, to)
            } else {
                null
            }
    }

    open val isZero: Boolean by lazy {
        x == 0f && y == 0f && z == 0f
    }

    open val length: Float by lazy {
        sqrt(x * x + y * y + z * z)
    }

    open fun normalize(): Vector3 = length.let {
        Normalized(x / it, y / it, z / it)
    }

    fun Vector3.safeNormalize() = length.let {
        if (it == 0f) {
            ZERO
        } else {
            Vector3(x / it, y / it, z / it)
        }
    }

    infix fun dot(other: Vector3) = x * other.x + y * other.y + z * other.z

    infix fun cross(other: Vector3): Vector3 {
        val x = this.y * other.z - this.z * other.y
        val y = this.z * other.x - this.x * other.z
        val z = this.x * other.y - this.y * other.x
        return if (
            this is Normalized &&
            other is Normalized &&
            this dot other == 0f
        ) {
            Normalized(x, y, z)
        } else {
            Vector3(x, y, z)
        }
    }

    infix fun dotCross(other: Vector3) = DotCross(dot = this dot other, cross = this cross other)

    operator fun plus(other: Vector3) = Vector3(x + other.x, y + other.y, z + other.z)

    operator fun unaryPlus() = this

    operator fun minus(other: Vector3) = Vector3(x - other.x, y - other.y, z - other.z)

    operator fun unaryMinus() = Vector3(-x, -y, -z)

    operator fun times(other: Float) = Vector3(x * other, y * other, z * other)

    operator fun times(other: Vector3) = Vector3(x * other.x, y * other.y, z * other.z)

    operator fun div(other: Float): Vector3 = Vector3(x / other, y / other, z / other)

    operator fun div(other: Vector3) = Vector3(x / other.x, y / other.y, z / other.z)

    operator fun component1() = x

    operator fun component2() = y

    operator fun component3() = z

    fun toFloatArray() = floatArrayOf(x, y, z)

    fun toArray() = arrayOf(x, y, z)

    fun toList() = listOf(x, y, z)
}

fun Vector3(
    values: FloatArray,
    offset: Int = 0,
    stride: Int = 0,
) = when {
    stride <= -1 -> {
        throw IllegalArgumentException()
    }

    stride == 0 -> {
        Vector3(
            values[offset],
            values[offset + 1],
            values[offset + 2],
        )
    }

    else -> {
        Vector3(
            values[offset],
            values[offset + stride * 1],
            values[offset + stride * 2],
        )
    }
}

fun Vector3(
    values: List<Float>,
    offset: Int = 0,
    stride: Int = 0,
) = when {
    stride <= -1 -> {
        throw IllegalArgumentException()
    }

    stride == 0 -> {
        Vector3(
            values[offset],
            values[offset + 1],
            values[offset + 2],
        )
    }

    else -> {
        Vector3(
            values[offset],
            values[offset + stride * 1],
            values[offset + stride * 2],
        )
    }
}

operator fun Float.times(other: Vector3) = Vector3(this * other.x, this * other.y, this * other.z)

operator fun Float.div(other: Vector3) = Vector3(this / other.x, this / other.y, this / other.z)
