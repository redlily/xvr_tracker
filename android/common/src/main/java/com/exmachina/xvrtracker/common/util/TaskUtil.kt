package com.exmachina.xvrtracker.common.util

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

suspend fun checkCancellation() {
    currentCoroutineContext().ensureActive()
}
