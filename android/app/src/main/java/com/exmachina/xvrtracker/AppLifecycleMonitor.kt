package com.exmachina.xvrtracker

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.exmachina.xvrtracker.data.source.NetworkInterfaceDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLifecycleMonitor @Inject constructor(
    private val networkStateDataSource: NetworkInterfaceDataSource,
) : DefaultLifecycleObserver {

    fun setup() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onStart(owner)
        networkStateDataSource.start()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onStop(owner)
        networkStateDataSource.stop()
    }
}
