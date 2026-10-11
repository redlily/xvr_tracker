package com.exmachina.xvrtracker.data.source

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.exmachina.xvrtracker.common.math.Vector3
import com.exmachina.xvrtracker.common.util.sensorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccelerationDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {

    val accelerationFlow: Flow<Vector3> = callbackFlow {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                trySend(
                    Vector3(
                        event.values[0],
                        event.values[1],
                        event.values[2],
                    ),
                )
            }

            override fun onAccuracyChanged(
                sensor: Sensor,
                accuracy: Int,
            ) {
            }
        }

        val sensorManager = context.sensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        sensorManager.registerListener(
            listener,
            accelerometer,
            SensorManager.SENSOR_DELAY_NORMAL,
        )

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }
}
