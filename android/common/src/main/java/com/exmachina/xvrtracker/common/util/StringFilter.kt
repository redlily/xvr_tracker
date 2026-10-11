package com.exmachina.xvrtracker.common.util

fun intFilter(value: String) = "[+-]?\\d*".toRegex().find(value)?.groupValues?.getOrNull(0) ?: ""

fun positiveIntFilter(value: String) = "\\+?\\d*".toRegex().find(value)?.groupValues?.getOrNull(0) ?: ""

fun floatFilter(value: String) = "[+-]?\\d*\\.?\\d*".toRegex().find(value)?.groupValues?.getOrNull(0) ?: ""

fun positiveFloatFilter(value: String) = "\\+?\\d*\\.?\\d*".toRegex().find(value)?.groupValues?.getOrNull(0) ?: ""
