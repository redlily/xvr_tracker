package com.exmachina.xvrtracker.common.util

import android.content.Context
import android.hardware.SensorManager
import android.net.ConnectivityManager

val Context.connectivityManager
    get() = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

val Context.sensorManager
    get() = getSystemService(Context.SENSOR_SERVICE) as SensorManager
