package ir.dorantech.gamiransteptester.ui.viewmodel

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.dorantech.gamiransteptester.core.logging.LogManager
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class SensorEventViewModel @Inject constructor(
    private val logManager: LogManager,
    @ApplicationContext private val context: Context,
) : ViewModel(){
    private val stepCount = MutableStateFlow<String>("")
    val stepCounterState: MutableStateFlow<String> get() = stepCount
    init {
        val sensorManager = context.getSystemService(SensorManager::class.java)
        val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        val stepListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                val steps = event?.values?.get(0) ?: 0f
                stepCount.value = steps.toString()
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                addLogToList("SensorName: ${sensor?.name}, Accuracy: $accuracy")
            }
        }
        sensorManager.registerListener(stepListener, stepSensor, SensorManager.SENSOR_DELAY_UI)
    }

    fun addLogToList(log: String) {
        logManager.addLog(log)
    }
}