package com.exmachina.xvrtracker.common.util

import kotlinx.coroutines.sync.Mutex

inline fun <T> Mutex.withTryLock(
    owner: Any? = null,
    action: () -> T,
): TryLockResult<T> = tryLock(owner).let {
    return if (it) {
        try {
            return TryLockResult.Success(action())
        } finally {
            unlock()
        }
    } else {
        TryLockResult.Failure()
    }
}

sealed interface TryLockResult<T> {
    data class Success<T>(val result: T) : TryLockResult<T>

    class Failure<Never>() : TryLockResult<Never>
}
