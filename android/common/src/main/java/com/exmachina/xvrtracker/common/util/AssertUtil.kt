package com.exmachina.xvrtracker.common.util

import android.os.Looper
import com.exmachina.xvrtracker.common.BuildConfig

fun assertNotMainThread() {
    if (BuildConfig.DEBUG) {
        check(Looper.myLooper() != Looper.getMainLooper()) {
            "Blocking operation invoked on the main thread."
        }
    }
}
