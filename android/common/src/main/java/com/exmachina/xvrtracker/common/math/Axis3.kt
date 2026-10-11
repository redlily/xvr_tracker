package com.exmachina.xvrtracker.common.math

import androidx.compose.runtime.Immutable
import com.exmachina.xvrtracker.common.math.EulerAngle.Companion.eulerAngleWithDegree
import com.exmachina.xvrtracker.common.math.EulerAngle.Companion.eulerAngleWithRadian
import kotlinx.serialization.Serializable
import kotlin.math.atan2
import kotlin.math.sqrt

@Immutable
@Serializable
class Axis3 private constructor(
    val x: Vector3,
    val y: Vector3,
    val z: Vector3,
) {

    companion object {

        val identity = Axis3(Vector3.X, Vector3.Y, Vector3.Z)

        fun xzy(
            x: Vector3,
            ref: Vector3,
        ): Axis3 {
            val nx = x.normalize()
            val nz = (x cross ref).normalize()
            return Axis3(nx, nz cross nx, nz)
        }

        fun zxy(
            z: Vector3,
            ref: Vector3,
        ): Axis3 {
            val nz = z.normalize()
            val nx = (ref cross nz).normalize()
            return Axis3(nx, nz cross nx, nz)
        }

        fun zyx(
            z: Vector3,
            ref: Vector3,
        ): Axis3 {
            val nz = z.normalize()
            val ny = (nz cross ref).normalize()
            return Axis3(ny cross nz, ny, nz)
        }

        fun yzx(
            y: Vector3,
            ref: Vector3,
        ): Axis3 {
            val ny = y.normalize()
            val nz = (y cross ref).normalize()
            return Axis3(ny cross nz, ny, nz)
        }

        operator fun Matrix4x4.times(axis: Axis3): Axis3 =
            Axis3(
                x = this * axis.x,
                y = this * axis.y,
                z = this * axis.z,
            )
    }

    fun xzy(ref: Vector3): Axis3 {
        val nx = x.normalize()
        val nz = (x cross ref).normalize()
        return Axis3(nx, nz cross nx, nz)
    }

    fun zxy(ref: Vector3): Axis3 {
        val nz = z.normalize()
        val nx = (ref cross nz).normalize()
        return Axis3(nx, nz cross nx, nz)
    }

    fun zyx(ref: Vector3): Axis3 {
        val nz = z.normalize()
        val ny = (nz cross ref).normalize()
        return Axis3(ny cross nz, ny, nz)
    }

    fun toMatrix4x4(): Matrix4x4 =
        Matrix4x4.column(
            axis = this,
            t = Vector3.ZERO,
        )

    fun toEulerAngleZXY(degree: Boolean = false): EulerAngle {
        val xx = x.x.toDouble()
        val xy = x.y.toDouble()
        val yx = y.x.toDouble()
        val yy = y.y.toDouble()
        val zx = z.x.toDouble()
        val zy = z.y.toDouble()
        val zz = z.z.toDouble()

        val beta = atan2(zy, sqrt(zx * zx + zz * zz))
        val alpha: Double
        val gamma: Double
        if (beta != .0) {
            alpha = atan2(-xy, yy)
            gamma = atan2(-zx, zz)
        } else {
            alpha = atan2(yx, xx)
            gamma = .0
        }

        return if (!degree) {
            eulerAngleWithRadian(
                alpha.toFloat(),
                beta.toFloat(),
                gamma.toFloat(),
            )
        } else {
            eulerAngleWithDegree(
                alpha.toDegree().toFloat(),
                beta.toDegree().toFloat(),
                gamma.toDegree().toFloat(),
            )
        }
    }
}
