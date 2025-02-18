package ir.dorantech.gamiransteptester.core.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent

import android.os.IBinder
import com.google.android.gms.location.DetectedActivity
import dagger.hilt.android.AndroidEntryPoint
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
    private var stopWalkingCounter = 3
    private var lastLocalDateTime = 0L

    private companion object {
        private const val CHANNEL_ID = "foreground_service_channel"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!initSensorManager()) stopSelf()
        registerWalkingBroadcast()
        logManager.addLog("onStartCommand is called")
        startForegroundService()
        return START_STICKY
    }

    private fun startForegroundService() {
        createNotificationChannel()
        val notification = Notification
            .Builder(this, CHANNEL_ID)
            .setContentText("StepCounter is walking")
            .setContentTitle("StepCounter service is enable")
            .setSmallIcon(android.R.drawable.ic_notification_overlay)
            .build()

        startForeground(584264, notification)
    }

    private fun createNotificationChannel() {
        val notificationChannel =
            NotificationChannel(
                CHANNEL_ID,
                "Foreground Service",
                NotificationManager.IMPORTANCE_DEFAULT
            )
        getSystemService(NotificationManager::class.java).createNotificationChannel(
            notificationChannel
        )
    }

    private fun initSensorManager(): Boolean {
        when (val result = sensorStepCountUseCase()) {
            is UseCaseResult.Success -> {
                serviceScope.launch {
                    result.data.steps.collect {
                        handleStepCount(it)
                    }
                }
                serviceScope.launch {
                    result.data.accuracy.collect {
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

    private fun handleStepCount(currentStepCount: Int) {
        if ((isWalking && accuracy >= 1)) {
            if (!walkingFlag) {
                walkingFlag = true
                startCounter = currentStepCount

                calculateMissedSteps()
                getTotalStepsCounter()
                logManager.addLog("StartCount = $currentStepCount & TotalCount = $totalCount")
            } else {
                saveTotalStepsCount(calculateTotalSteps(currentStepCount))
            }
        } else if (walkingFlag) stopWalking(currentStepCount)
    }

    private fun getTotalStepsCounter() {
        serviceScope.launch {
            totalCount = preferencesHelper.getTotalSteps().first()
        }
    }

    private fun saveTotalStepsCount(counter: Int) {
        serviceScope.launch {
            preferencesHelper.setTotalSteps(counter)
        }
    }

    private fun calculateTotalSteps(currentStepCount: Int): Int {
        return totalCount + (currentStepCount - startCounter)
    }

    private fun calculateMissedSteps(){
        val currentTime = System.currentTimeMillis()
        if (lastLocalDateTime > 0) {
            val deltaTime = (currentTime - lastLocalDateTime) / 1000.0
            val avgStepRate = 1.5
            val missedSteps = (deltaTime * avgStepRate).toInt()
            totalCount += missedSteps
        }
    }

    private fun stopWalking(currentStepCount: Int) {
        logManager.addLog("StepCounterManager stop: $currentStepCount")
        walkingFlag = false
        totalCount = 0
    }

    override fun onCreate() {
        super.onCreate()
        logManager.addLog("StepCounterManager: Service created!")
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
        lastLocalDateTime = System.currentTimeMillis()
        serviceScope.launch {
            activityTypeBroadcastManager.broadcastResultState.collect {
                if (it != null && it.detectedActivity == DetectedActivity.WALKING) {
                    isWalking = if (it.confidence >= 70) {
                        stopWalkingCounter = 3
                        true
                    } else if (stopWalkingCounter > 0) {
                        stopWalkingCounter--
                        true
                    } else {
                        stopWalkingCounter = 3
                        false
                    }

                    logManager.addLog("IS_WALKING = $isWalking & confidence: ${it.confidence}")
                }
            }
        }
    }
}