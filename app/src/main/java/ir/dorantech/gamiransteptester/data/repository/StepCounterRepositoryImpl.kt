package ir.dorantech.gamiransteptester.data.repository

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import ir.dorantech.gamiransteptester.domain.model.StepResult
import ir.dorantech.gamiransteptester.domain.repository.StepCounterRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class StepCounterRepositoryImpl @Inject constructor(
    context: Context,
): StepCounterRepository {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    override suspend fun stepFlow() = callbackFlow {
        if (sensor == null) {
            trySend(StepResult.Error("Step Counter sensor not available"))
        }

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event != null) {
                    val stepsSinceLastReboot = event.values[0].toLong()
                    trySend(StepResult.Success(stepsSinceLastReboot))
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        val supportedAndEnabled = sensorManager.registerListener(
            listener, sensor, SensorManager.SENSOR_DELAY_UI
        )

        if (!supportedAndEnabled) {
            trySend(StepResult.Error("Failed to register sensor listener"))
        }

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }
}