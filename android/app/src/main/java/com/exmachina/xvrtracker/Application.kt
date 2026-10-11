package com.exmachina.xvrtracker

import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class Application : android.app.Application() {

    @Inject
    lateinit var appLifecycleMonitor: AppLifecycleMonitor

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        appLifecycleMonitor.setup()
    }
}
