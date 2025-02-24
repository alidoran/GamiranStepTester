package ir.dorantech.gamiransteptester.core.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint
import ir.dorantech.gamiransteptester.core.broadcast.manager.ActivityTypeBroadcastManager
import ir.dorantech.gamiransteptester.core.broadcast.model.DetectActivityResult
import ir.dorantech.gamiransteptester.core.datastore.PreferencesHelper
import ir.dorantech.gamiransteptester.core.logging.LogManager
import ir.dorantech.gamiransteptester.core.model.ResultCallback
import ir.dorantech.gamiransteptester.domain.model.UseCaseResult
import ir.dorantech.gamiransteptester.domain.usecase.SensorStepCountUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
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
    private var saveJob: Job? = null
    private var isWalkingRecently = false
    private var startCounter = 0
    private var accuracy = 0
    private var totalCount = 0
    private var stopWalkingCounter = 3
    private var lastLocalDateTime = 0L

    private companion object {
        private const val CHANNEL_ID = "foreground_service_channel"
        private const val STOP_WALKING_TRY_TO_PROOF = 3
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        logAliveMessage()
        registerWalkingBroadcast()
        logManager.addLog("onStartCommand is called")
        startForegroundService()
        if (!initSensorManager()) stopSelf()
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
                logManager.addLog("initSensorManager is failure: ${result.message}")
                return false
            }
        }
    }

    private fun handleStepCount(currentStepCount: Int) = serviceScope.launch {
        if (totalCount == 0) {
            totalCount = getTotalStepsCounterSync()
        }
        if ((isWalking && accuracy >= 1)) {
            if (!isWalkingRecently) startWalking(currentStepCount)
            else saveTotalStepsCount(calculateTotalSteps(currentStepCount))
        } else if (isWalkingRecently) stopWalking(currentStepCount)
    }

    private suspend fun getTotalStepsCounterSync(): Int {
        return preferencesHelper.getTotalSteps().first()
    }

    private fun saveTotalStepsCount(counter: Int) {
        saveJob?.cancel()
        saveJob = serviceScope.launch {
            preferencesHelper.setTotalSteps(counter)
            delay(TimeUnit.SECONDS.toMillis(2))
        }
    }

    private fun calculateTotalSteps(currentStepCount: Int): Int {
        return totalCount + (currentStepCount - startCounter)
    }

    private fun calculateMissedSteps() {
        val currentTime = System.currentTimeMillis()
        if (lastLocalDateTime > 0) {
            val deltaTime = (currentTime - lastLocalDateTime) / 1000.0
            val avgStepRate = 1.5
            val missedSteps = (deltaTime * avgStepRate).toInt()
            totalCount += missedSteps
        }
    }

    private fun startWalking(currentStepCount: Int) {
        if (!isWalkingRecently) {
            isWalkingRecently = true
            startCounter = currentStepCount

            calculateMissedSteps()
            logManager.addLog("StartCount = $currentStepCount & TotalCount = $totalCount")
        }
    }

    private fun stopWalking(currentStepCount: Int) {
        logManager.addLog("StepCounterManager stop: $currentStepCount")
        isWalkingRecently = false
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
        activityTypeBroadcastManager.unregisterActivityTypeListener(object :
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
        activityTypeBroadcastManager.registerActivityTypeListener(object : ResultCallback<String> {
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
            activityTypeBroadcastManager.broadcastResultState.collect { detectActivityResult ->
                detectActivityResult?.let {
                    handleWalkingState(it)
                }
            }
        }
    }

    private fun handleWalkingState(result: DetectActivityResult) {
        if (result is DetectActivityResult.Walking) {
            isWalking = result.confidence >= 70 || stopWalkingCounter-- > 0
            if (!isWalking) stopWalkingCounter = STOP_WALKING_TRY_TO_PROOF
            logManager.addLog("IS_WALKING = $isWalking & confidence: ${result.confidence}")
        }
    }

    private fun logAliveMessage() {
        serviceScope.launch {
            while (isActive) {
                LogManager.addLog("I'm still alive")
                delay(TimeUnit.MINUTES.toMillis(5))
            }
        }
    }
}