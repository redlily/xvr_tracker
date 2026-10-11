package com.exmachina.xvrtracker.common.math

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

// TODO: iOS版の実装に合わせてシンプルなdata classに書き換える
//       - sealed classではなくdata class化
//       - isDegreeフラグで度/ラジアンを管理
//       - toRadian()/toDegree()で変換処理を実装
@Immutable
@Serializable
sealed class EulerAngle(
    val alpha: Float,
    val beta: Float,
    val gamma: Float,
) {

    @Immutable
    class Radian(
        alpha: Float,
        beta: Float,
        gamma: Float,
    ) : EulerAngle(alpha, beta, gamma) {

        override val isRadian = true
        override val isDegree = false

        override fun copy(
            alpha: Float,
            beta: Float,
            gamma: Float,
        ) = Radian(alpha, beta, gamma)

        override fun toRadian() = this

        override fun toDegree() = Degree(alpha.toDegree(), beta.toDegree(), gamma.toDegree())
    }

    @Immutable
    class Degree(
        alpha: Float,
        beta: Float,
        gamma: Float,
    ) : EulerAngle(alpha, beta, gamma) {

        override val isRadian = false
        override val isDegree = true

        override fun copy(
            alpha: Float,
            beta: Float,
            gamma: Float,
        ) = Degree(alpha, beta, gamma)

        override fun toRadian() = Radian(alpha.toRadian(), beta.toRadian(), gamma.toRadian())

        override fun toDegree() = this
    }

    companion object {

        fun eulerAngleWithRadian() = Radian(0f, 0f, 0f)

        fun eulerAngleWithRadian(
            alpha: Float,
            beta: Float,
            gamma: Float,
        ) = Radian(alpha, beta, gamma)

        fun eulerAngleWithDegree() = Degree(0f, 0f, 0f)

        fun eulerAngleWithDegree(
            alpha: Float,
            beta: Float,
            gamma: Float,
        ) = Degree(alpha, beta, gamma)
    }

    abstract fun copy(
        alpha: Float = this.alpha,
        beta: Float = this.beta,
        gamma: Float = this.gamma,
    ): EulerAngle

    abstract val isRadian: Boolean

    abstract val isDegree: Boolean

    operator fun unaryMinus() = copy(-alpha, -beta, -gamma)

    operator fun times(v: Float) = copy(alpha * v, beta * v, gamma * v)

    abstract fun toRadian(): Radian

    abstract fun toDegree(): Degree

    operator fun component1() = alpha

    operator fun component2() = beta

    operator fun component3() = gamma

    fun toFloatArray() = floatArrayOf(alpha, beta, gamma)

    fun toArray() = arrayOf(alpha, beta, gamma)

    fun toList() = listOf(alpha, beta, gamma)
}
