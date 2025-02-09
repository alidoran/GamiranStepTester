package ir.dorantech.gamiransteptester.core.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder

import android.os.IBinder
import com.google.android.gms.location.DetectedActivity
import dagger.hilt.android.AndroidEntryPoint
import ir.dorantech.gamiransteptester.R
import ir.dorantech.gamiransteptester.core.broadcast.manager.RunningBroadcastManager
import ir.dorantech.gamiransteptester.core.broadcast.manager.impl.RunningBroadcastManagerImpl
import ir.dorantech.gamiransteptester.core.logging.LogManager
import ir.dorantech.gamiransteptester.core.model.ResultCallback
import ir.dorantech.gamiransteptester.domain.model.UseCaseResult
import ir.dorantech.gamiransteptester.domain.usecase.SensorStepCountUseCase
import ir.dorantech.gamiransteptester.util.ActivityType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StepCountingService : Service() {
    @Inject
    lateinit var logManager: LogManager

    @Inject
    lateinit var sensorStepCountUseCase: SensorStepCountUseCase

    @Inject
    lateinit var runningBroadcastManager: RunningBroadcastManager

    private val binder = StepCountingBinder()

    private var isRunning = false
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val _counterState = MutableStateFlow(0)
    val counterState = _counterState.asStateFlow()
    private var runningFlag = false
    private var startCounter = 0
    private var accuracy = 0


    override fun onCreate() {
        super.onCreate()
        logManager.addLog("StepCounterManager: Service created!")
    }

    private fun initSensorManager(): Boolean {
        when (val result = sensorStepCountUseCase()) {
            is UseCaseResult.Success -> {
                CoroutineScope(Dispatchers.IO).launch {
                    result.data.steps.collect {
//                        logManager.addLog("StepCounterManager From Sensor manager: $it")
                        val shouldStart = isRunning && accuracy > 1
                        logManager.addLog("isRunning: $isRunning")
                        if ((shouldStart)) {
                            logManager.addLog("TOTAL COUNT: ${counterState.value}")
                            if (!runningFlag) {
                                startCounter = it
                                runningFlag = true
                            } else {
                                _counterState.value += (it - startCounter)
                                startCounter = it
                            }
                        } else runningFlag = false

                    }
                }
                CoroutineScope(Dispatchers.IO).launch {
                    result.data.accuracy.collect {
//                        logManager.addLog("StepCounterManager From Sensor manager: $it")
                        accuracy = it
                    }
                }
                return true
            }

            is UseCaseResult.Error -> {
                logManager.addLog(result.message)
                return false
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (!initSensorManager()) stopSelf()
        registerRunningBroadcast()

        logManager.addLog("onStartCommand is called")
        val channelId = "Foreground channelId"
        val notificationChannel =
            NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_HIGH)
        getSystemService(NotificationManager::class.java).createNotificationChannel(
            notificationChannel
        )

        val notification = Notification
            .Builder(this, channelId)
            .setContentText("StepCounter is running")
            .setContentTitle("StepCounter service is enable")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .build()

        startForeground(1001, notification)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        logManager.addLog("onDestroy is called")
        sensorStepCountUseCase.unregisterStepListener()
        runningBroadcastManager.unregisterRunningBroadcast(object : ResultCallback<String> {
            override fun onSuccess(data: String) {
                logManager.addLog(data)
            }

            override fun onError(error: String) {
                logManager.addLog(error)
            }

        })
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    private fun registerRunningBroadcast() {
        runningBroadcastManager.registerRunningBroadcast(object : ResultCallback<String> {
            override fun onSuccess(data: String) {
                logManager.addLog("registerRunningBroadcast: $data")
                getBroadcastResult()
            }

            override fun onError(error: String) {
                logManager.addLog(error)
            }
        })
    }

    private fun getBroadcastResult() {
        serviceScope.launch {
            runningBroadcastManager.getBroadcastResult().collect {
                isRunning = (it?.detectedActivity == DetectedActivity.RUNNING && it.confidence >= 9)
                logManager.addLog("isRunning: $isRunning with confidence: ${it?.confidence} and activity: ${it?.let { ActivityType.fromInt(it.detectedActivity) }}")
            }
        }
    }

    inner class StepCountingBinder : Binder() {
        fun getService(): StepCountingService {
            return this@StepCountingService
        }
    }
}