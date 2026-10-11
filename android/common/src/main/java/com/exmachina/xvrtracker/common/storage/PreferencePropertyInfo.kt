package com.exmachina.xvrtracker.common.storage

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

data class PreferencePropertyInfo<T>(
    val key: Preferences.Key<T>,
    val defaultValue: T,
)

fun <T> Preferences.value(info: PreferencePropertyInfo<T>) = this[info.key] ?: info.defaultValue

fun preferencePropertyInfo(
    key: String,
    defaultValue: Int,
) = PreferencePropertyInfo(
    key = intPreferencesKey(key),
    defaultValue = defaultValue,
)

fun preferencePropertyInfo(
    key: String,
    defaultValue: Float,
) = PreferencePropertyInfo(
    key = floatPreferencesKey(key),
    defaultValue = defaultValue,
)

fun preferencePropertyInfo(
    key: String,
    defaultValue: Boolean,
) = PreferencePropertyInfo(
    key = booleanPreferencesKey(key),
    defaultValue = defaultValue,
)

fun preferencePropertyInfo(
    key: String,
    defaultValue: String,
) = PreferencePropertyInfo(
    key = stringPreferencesKey(key),
    defaultValue = defaultValue,
)

fun preferencePropertyInfo(
    prefix: String,
    key: String,
    defaultValue: Int,
) = PreferencePropertyInfo(
    key = intPreferencesKey("${prefix}_$key"),
    defaultValue = defaultValue,
)

fun preferencePropertyInfo(
    prefix: String,
    key: String,
    defaultValue: Float,
) = PreferencePropertyInfo(
    key = floatPreferencesKey("${prefix}_$key"),
    defaultValue = defaultValue,
)

fun preferencePropertyInfo(
    prefix: String,
    key: String,
    defaultValue: Boolean,
) = PreferencePropertyInfo(
    key = booleanPreferencesKey("${prefix}_$key"),
    defaultValue = defaultValue,
)

fun preferencePropertyInfo(
    prefix: String,
    key: String,
    defaultValue: String,
) = PreferencePropertyInfo(
    key = stringPreferencesKey("${prefix}_$key"),
    defaultValue = defaultValue,
)
