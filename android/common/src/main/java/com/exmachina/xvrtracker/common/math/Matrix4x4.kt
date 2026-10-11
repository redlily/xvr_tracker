package com.exmachina.xvrtracker.common.math

import com.exmachina.xvrtracker.common.math.EulerAngle.Companion.eulerAngleWithDegree
import com.exmachina.xvrtracker.common.math.EulerAngle.Companion.eulerAngleWithRadian
import kotlinx.serialization.Serializable
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Serializable
data class Matrix4x4(
    val m00: Float, val m01: Float, val m02: Float, val m03: Float,
    val m10: Float, val m11: Float, val m12: Float, val m13: Float,
    val m20: Float, val m21: Float, val m22: Float, val m23: Float,
    val m30: Float, val m31: Float, val m32: Float, val m33: Float,
) {

    companion object {

        private const val EPSILON = 1e-6f

        val IDENTITY =
            Matrix4x4(
                1f, 0f, 0f, 0f,
                0f, 1f, 0f, 0f,
                0f, 0f, 1f, 0f,
                0f, 0f, 0f, 1f,
            )

        fun column(
            x: Vector3,
            y: Vector3,
            z: Vector3,
            t: Vector3,
        ) = Matrix4x4(
            x.x, y.x, z.x, t.x,
            x.y, y.y, z.y, t.y,
            x.z, y.z, z.z, t.z,
            0f, 0f, 0f, 1f,
        )

        fun column(
            axis: Axis3,
            t: Vector3,
        ) = column(axis.x, axis.y, axis.z, t)

        fun scale(
            x: Float,
            y: Float,
            z: Float,
        ) = Matrix4x4(
            x, 0f, 0f, 0f,
            0f, y, 0f, 0f,
            0f, 0f, z, 0f,
            0f, 0f, 0f, 1f,
        )

        fun scale(
            axis: Vector3,
            s: Float,
        ) {
            val (x, y, z) = axis
            val sm1 = s - 1f
            Matrix4x4(
                1f + sm1 * x * x, sm1 * x * y, sm1 * x * z, 0f,
                sm1 * x * y, 1f + sm1 * y * y, sm1 * y * z, 0f,
                sm1 * x * z, sm1 * y * z, 1f + sm1 * z * z, 0f,
                0f, 0f, 0f, 1f,
            )
        }

        fun translation(t: Vector3) = column(Vector3.X, Vector3.Y, Vector3.Z, t)

        fun rotation(
            axis: Vector3,
            angle: Float,
            isDegree: Boolean = false,
        ): Matrix4x4 {
            val (x, y, z) = axis.normalize()
            val radian = if (isDegree) angle.toRadian() else angle
            val cs = cos(radian)
            val sn = sin(radian)
            val omcs = 1 - cs
            return Matrix4x4(
                x * x * omcs + cs, x * y * omcs - z * sn, x * z * omcs + y * sn, 0f,
                x * y * omcs + z * sn, y * y * omcs + cs, y * z * omcs - x * sn, 0f,
                x * z * omcs - y * sn, y * z * omcs + x * sn, z * z * omcs + cs, 0f,
                0f, 0f, 0f, 1f,
            )
        }

        fun rotation(
            from: Vector3,
            to: Vector3,
            rate: Float = 1f,
        ): Matrix4x4 {
            val nf = from.normalize()
            val nt = to.normalize()
            val cs = nf dot nt
            if (cs < 1f) {
                val axis = (nf cross nt).normalize()
                return rotation(axis, acos(cs) * rate)
            } else {
                return IDENTITY
            }
        }
    }

    fun columnVector3(index: Int): Vector3 =
        when (index) {
            0 -> Vector3(m00, m10, m20)
            1 -> Vector3(m01, m11, m21)
            2 -> Vector3(m02, m12, m22)
            3 -> Vector3(m03, m13, m23)
            else -> throw IndexOutOfBoundsException()
        }

    fun column(
        index: Int,
        v: Vector3,
    ) = when (index) {
        0 -> {
            Matrix4x4(
                v.x, m10, m20, m30,
                v.y, m11, m21, m31,
                v.z, m12, m22, m32,
                m03, m13, m23, m33,
            )
        }

        1 -> {
            Matrix4x4(
                m00, v.x, m20, m30,
                m01, v.y, m21, m31,
                m02, v.z, m22, m32,
                m03, m13, m23, m33,
            )
        }

        2 -> {
            Matrix4x4(
                m00, m10, v.x, m30,
                m01, m11, v.y, m31,
                m02, m12, v.z, m32,
                m03, m13, m23, m33,
            )
        }

        3 -> {
            Matrix4x4(
                m00, m10, m20, v.x,
                m01, m11, m21, v.y,
                m02, m12, m22, v.z,
                m03, m13, m23, m33,
            )
        }

        else -> {
            throw IndexOutOfBoundsException()
        }
    }

    fun transpose() =
        Matrix4x4(
            m00, m10, m20, m30,
            m01, m11, m21, m31,
            m02, m12, m22, m32,
            m03, m13, m23, m33,
        )

    operator fun times(m: Matrix4x4): Matrix4x4 {
        val r11 = m00 * m.m00 + m01 * m.m10 + m02 * m.m20 + m03 * m.m30
        val r12 = m00 * m.m01 + m01 * m.m11 + m02 * m.m21 + m03 * m.m31
        val r13 = m00 * m.m02 + m01 * m.m12 + m02 * m.m22 + m03 * m.m32
        val r14 = m00 * m.m03 + m01 * m.m13 + m02 * m.m23 + m03 * m.m33
        val r21 = m10 * m.m00 + m11 * m.m10 + m12 * m.m20 + m13 * m.m30
        val r22 = m10 * m.m01 + m11 * m.m11 + m12 * m.m21 + m13 * m.m31
        val r23 = m10 * m.m02 + m11 * m.m12 + m12 * m.m22 + m13 * m.m32
        val r24 = m10 * m.m03 + m11 * m.m13 + m12 * m.m23 + m13 * m.m33
        val r31 = m20 * m.m00 + m21 * m.m10 + m22 * m.m20 + m23 * m.m30
        val r32 = m20 * m.m01 + m21 * m.m11 + m22 * m.m21 + m23 * m.m31
        val r33 = m20 * m.m02 + m21 * m.m12 + m22 * m.m22 + m23 * m.m32
        val r34 = m20 * m.m03 + m21 * m.m13 + m22 * m.m23 + m23 * m.m33
        val r41 = m30 * m.m00 + m31 * m.m10 + m32 * m.m20 + m33 * m.m30
        val r42 = m30 * m.m01 + m31 * m.m11 + m32 * m.m21 + m33 * m.m31
        val r43 = m30 * m.m02 + m31 * m.m12 + m32 * m.m22 + m33 * m.m32
        val r44 = m30 * m.m03 + m31 * m.m13 + m32 * m.m23 + m33 * m.m33
        return Matrix4x4(
            r11, r12, r13, r14,
            r21, r22, r23, r24,
            r31, r32, r33, r34,
            r41, r42, r43, r44,
        )
    }

    operator fun times(v: Vector3): Vector3 {
        val x = m00 * v.x + m01 * v.y + m02 * v.z + m03
        val y = m10 * v.x + m11 * v.y + m12 * v.z + m13
        val z = m20 * v.x + m21 * v.y + m22 * v.z + m23
        val w = m30 + m31 + m32 + m33
        return Vector3(x / w, y / w, z / w)
    }

    fun toEulerAngleZXY(isDegree: Boolean = false): EulerAngle {
        val beta = atan2(m21, sqrt(m20 * m20 + m22 * m22))
        val alpha: Float
        val gamma: Float
        if (abs(beta) > EPSILON) {
            alpha = atan2(-m01, m11)
            gamma = atan2(-m20, m22)
        } else {
            alpha = atan2(m10, m00)
            gamma = 0f
        }

        return if (!isDegree) {
            eulerAngleWithRadian(
                alpha,
                beta,
                gamma,
            )
        } else {
            eulerAngleWithDegree(
                alpha.toDegree(),
                beta.toDegree(),
                gamma.toDegree(),
            )
        }
    }

    override fun toString(): String =
        "Matrix4x4(\n" +
            "\t$m00, $m01, $m02, $m03\n" +
            "\t$m10, $m11, $m12, $m13\n" +
            "\t$m20, $m21, $m22, $m23\n" +
            "\t$m30, $m31, $m32, $m33\n" +
            ")"
}
