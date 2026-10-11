package com.exmachina.xvrtracker.common.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.exmachina.xvrtracker.common.util.onTrue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

interface PreferencesProperty<Input, Output> : Flow<Output> {

    suspend operator fun invoke(): Output

    suspend operator fun invoke(value: Input): Boolean

    fun checkValid(value: Input): Boolean
}

interface CachedPreferencesProperty<Input, Output> : PreferencesProperty<Input, Output> {

    suspend fun refresh()

    suspend fun reset()
}

private class CachedPreferencesPropertyImpl<T>(
    val data: DataStore<Preferences>,
    val info: PreferencePropertyInfo<T>,
    val checkValid: ((T) -> Boolean)? = null,
) : CachedPreferencesProperty<T, T> {

    private val updateFlow = MutableSharedFlow<T>(replay = 1)

    override suspend fun refresh() {
        updateFlow.emit(data.data.map { it.value(info) }.first())
    }

    override suspend fun reset() {
        data.edit { it.clear() }
        updateFlow.emit(info.defaultValue)
    }

    override suspend fun invoke() =
        if (updateFlow.replayCache.isEmpty()) {
            val value = data.data.map { it.value(info) }.first()
            updateFlow.emit(value)
            value
        } else {
            updateFlow.first()
        }

    override suspend fun invoke(value: T) =
        if (checkValid?.invoke(value) != false) {
            updateFlow.emit(value)
            data.edit { it[info.key] = value }
            true
        } else {
            false
        }

    override fun checkValid(value: T) =
        checkValid?.invoke(value) != false

    override suspend fun collect(collector: FlowCollector<T>) {
        if (updateFlow.replayCache.isEmpty()) {
            collector.emit(invoke())
        }
        updateFlow.collect(collector)
    }
}

fun <Input1, Output1, Input2, Output2> CachedPreferencesProperty<Input1, Output1>.convert(
    inputTransform: (Input2) -> Input1,
    outputTransform: (Output1) -> Output2,
    checkValid: ((Input2) -> Boolean)? = null,
): CachedPreferencesProperty<Input2, Output2> =
    object : CachedPreferencesProperty<Input2, Output2> {
        override suspend fun refresh() =
            this@convert.refresh()

        override suspend fun reset() =
            this@convert.reset()

        override suspend fun invoke() =
            outputTransform(this@convert.invoke())

        override suspend fun invoke(value: Input2) =
            if (checkValid(value)) this@convert.invoke(inputTransform(value)) else false

        override fun checkValid(value: Input2) =
            checkValid?.invoke(value) != false

        override suspend fun collect(collector: FlowCollector<Output2>) =
            this@convert.map { outputTransform(it) }.collect(collector)
    }

fun <Input, Output1, Output2> CachedPreferencesProperty<Input, Output1>.convert(
    outputTransform: (Output1) -> Output2,
    checkValid: ((Input) -> Boolean)? = null,
): CachedPreferencesProperty<Input, Output2> =
    convert(
        inputTransform = { it },
        outputTransform = outputTransform,
        checkValid = checkValid,
    )

operator fun <Input, Output> CachedPreferencesProperty<Input, Output>.invoke(
    value: Input,
    scope: CoroutineScope,
): Boolean =
    checkValid(value).onTrue {
        scope.launch {
            invoke(value)
        }
    }

fun <T> DataStore<Preferences>.cachedProperty(
    info: PreferencePropertyInfo<T>,
    checkValid: ((T) -> Boolean)? = null,
): CachedPreferencesProperty<T, T> =
    CachedPreferencesPropertyImpl(
        data = this,
        info = info,
        checkValid = checkValid,
    )

fun DataStore<Preferences>.cachedProperty(
    prefix: String,
    key: String,
    defaultValue: Int,
    checkValid: ((Int) -> Boolean)? = null,
) = cachedProperty(
    info = preferencePropertyInfo(
        prefix = prefix,
        key = key,
        defaultValue = defaultValue,
    ),
    checkValid = checkValid,
)

fun DataStore<Preferences>.cachedProperty(
    prefix: String,
    key: String,
    defaultValue: Float,
    checkValid: ((Float) -> Boolean)? = null,
) = cachedProperty(
    info = preferencePropertyInfo(
        prefix = prefix,
        key = key,
        defaultValue = defaultValue,
    ),
    checkValid = checkValid,
)

fun DataStore<Preferences>.cachedProperty(
    prefix: String,
    key: String,
    defaultValue: Boolean,
    checkValid: ((Boolean) -> Boolean)? = null,
) = cachedProperty(
    info = preferencePropertyInfo(
        prefix = prefix,
        key = key,
        defaultValue = defaultValue,
    ),
    checkValid = checkValid,
)

fun DataStore<Preferences>.cachedProperty(
    prefix: String,
    key: String,
    defaultValue: String,
    checkValid: ((String) -> Boolean)? = null,
) = cachedProperty(
    info = preferencePropertyInfo(
        prefix = prefix,
        key = key,
        defaultValue = defaultValue,
    ),
    checkValid = checkValid,
)
