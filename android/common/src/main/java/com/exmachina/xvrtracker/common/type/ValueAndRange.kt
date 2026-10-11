package com.exmachina.xvrtracker.common.type

import androidx.compose.runtime.Immutable
import com.exmachina.xvrtracker.common.type.RangedValue.Companion.make
import kotlinx.serialization.Serializable

@Immutable
@Serializable
open class RangedValue<T : Comparable<T>> private constructor(
    val value: T,
    val range: ClosedRange<T>,
) {

    companion object {

        fun <T : Comparable<T>> make(
            value: T,
            range: ClosedRange<T>,
        ) = RangedValue(
            value =
                when {
                    value < range.start -> range.start
                    value > range.endInclusive -> range.endInclusive
                    else -> value
                },
            range = range,
        )
    }

    fun contains(value: T) = value in range

    fun update(value: T) = make(value, range)
}

infix fun <T : Comparable<T>> T.limits(range: ClosedRange<T>) = make(this, range)

fun <T> RangedValue<T>.toInt(): RangedValue<Int> where T : Comparable<T>, T : Number =
    make(value.toInt(), range.start.toInt()..range.endInclusive.toInt())

fun <T> RangedValue<T>.toFloat(): RangedValue<Float> where T : Comparable<T>, T : Number =
    make(value.toFloat(), range.start.toFloat()..range.endInclusive.toFloat())
