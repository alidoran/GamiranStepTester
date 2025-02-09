package ir.dorantech.gamiransteptester.domain.usecase.impl

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.core.content.ContextCompat.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.dorantech.gamiransteptester.core.logging.LogManager
import ir.dorantech.gamiransteptester.domain.model.SensorManagerRegistrationModel
import ir.dorantech.gamiransteptester.domain.model.UseCaseResult
import ir.dorantech.gamiransteptester.domain.usecase.SensorStepCountUseCase
import kotlinx.coroutines.flow.MutableStateFlow

class SensorStepCountUseCaseImpl(
    @ApplicationContext private val context: Context,
    private val logManager: LogManager
) : SensorStepCountUseCase {
    private val stepsState = MutableStateFlow(0)
    private val accuracyState = MutableStateFlow(0)
    private lateinit var sensorListener: SensorEventListener
    private val sensorManager: SensorManager? = getSystemService(context, SensorManager::class.java)

    override operator fun invoke(): UseCaseResult<SensorManagerRegistrationModel> {
        val stepSensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepSensor == null) {
            logManager.addLog("Step sensor not available!")
            return UseCaseResult.Error("Step sensor not available!")
        }

        logManager.addLog("StepCounterManager: Listening for one step update...")

        sensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                stepsState.value = event?.values?.get(0)?.toInt() ?: 0
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                accuracyState.value = accuracy
            }
        }
        sensorManager!!.registerListener(sensorListener, stepSensor, SensorManager.SENSOR_DELAY_UI)

        return UseCaseResult.Success(
            SensorManagerRegistrationModel(stepsState, accuracyState)
        )
    }

    override fun unregisterStepListener() {
        if (::sensorListener.isInitialized)
            sensorManager?.unregisterListener(sensorListener)
    }
}