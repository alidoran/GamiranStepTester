package ir.dorantech.gamiransteptester.core.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent

import android.os.IBinder
import com.google.android.gms.location.DetectedActivity
import dagger.hilt.android.AndroidEntryPoint
import ir.dorantech.gamiransteptester.R
import ir.dorantech.gamiransteptester.core.broadcast.manager.ActivityTypeBroadcastManager
import ir.dorantech.gamiransteptester.core.datastore.PreferencesHelper
import ir.dorantech.gamiransteptester.core.logging.LogManager
import ir.dorantech.gamiransteptester.core.model.ResultCallback
import ir.dorantech.gamiransteptester.domain.model.UseCaseResult
import ir.dorantech.gamiransteptester.domain.usecase.SensorStepCountUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StepCountingService : Service() {
    @Inject
    lateinit var logManager: LogManager

    @Inject
    lateinit var sensorStepCountUseCase: SensorStepCountUseCase

    @Inject
    lateinit var activityTypeBroadcastManager: ActivityTypeBroadcastManager

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    private var isWalking = false
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var walkingFlag = false
    private var startCounter = 0
    private var accuracy = 0
    private var totalCount = 0

    override fun onCreate() {
        super.onCreate()
        logManager.addLog("StepCounterManager: Service created!")
    }

    private fun getTotalCounter() {
        serviceScope.launch {
            totalCount = preferencesHelper.getTotalSteps().first()
        }
    }

    private fun saveTotalCounter(counter: Int) {
        serviceScope.launch {
            preferencesHelper.setTotalSteps(counter)
        }
    }

    private fun initSensorManager(): Boolean {
        when (val result = sensorStepCountUseCase()) {
            is UseCaseResult.Success -> {
                serviceScope.launch {
                    result.data.steps.collect {
//                        logManager.addLog("isWalking: $isWalking & accuracy: $accuracy")
                        if ((isWalking && accuracy >= 1)) {
                            if (!walkingFlag) {
                                walkingFlag = true
                                startCounter = it
                                getTotalCounter()
                                logManager.addLog("StartCount = $it & TotalCount = $totalCount")
                            } else {
                                saveTotalCounter(totalCount + (it - startCounter))
                            }
                        } else if (walkingFlag) {
                            logManager.addLog("StepCounterManager stop: $it")
                            walkingFlag = false
                            totalCount = 0
                        }
                    }
                }
                serviceScope.launch {
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
        registerWalkingBroadcast()

        logManager.addLog("onStartCommand is called")
        val channelId = "Foreground channelId"
        val notificationChannel =
            NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_HIGH)
        getSystemService(NotificationManager::class.java).createNotificationChannel(
            notificationChannel
        )

        val notification = Notification
            .Builder(this, channelId)
            .setContentText("StepCounter is walking")
            .setContentTitle("StepCounter service is enable")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .build()

        startForeground(1001, notification)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        logManager.addLog("onDestroy is called")
        sensorStepCountUseCase.unregisterStepListener()
        activityTypeBroadcastManager.unregisterActivityTypeBroadcast(object :
            ResultCallback<String> {
            override fun onSuccess(data: String) {
                logManager.addLog(data)
            }

            override fun onError(error: String) {
                logManager.addLog(error)
            }

        })
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun registerWalkingBroadcast() {
        activityTypeBroadcastManager.registerActivityTypeBroadcast(object : ResultCallback<String> {
            override fun onSuccess(data: String) {
                logManager.addLog("registerWalkingBroadcast: $data")
                getBroadcastResult()
            }

            override fun onError(error: String) {
                logManager.addLog(error)
            }
        })
    }

    private fun getBroadcastResult() {
        serviceScope.launch {
            activityTypeBroadcastManager.broadcastResultState.collect {
                if (it != null && it.detectedActivity == DetectedActivity.WALKING) {
                    isWalking = it.confidence >= 70
                    logManager.addLog("IS_WALKING = $isWalking & confidence: ${it.confidence}")
                }
            }
        }
    }
}