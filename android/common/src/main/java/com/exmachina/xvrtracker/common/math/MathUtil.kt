package com.exmachina.xvrtracker.common.math

fun Float.toRadian() = Math.toRadians(this.toDouble()).toFloat()

fun Float.toDegree() = Math.toDegrees(this.toDouble()).toFloat()

fun Double.toRadian() = Math.toRadians(this)

fun Double.toDegree() = Math.toDegrees(this)
