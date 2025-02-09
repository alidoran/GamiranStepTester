package ir.dorantech.gamiransteptester.core.broadcast.manager.impl

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.DetectedActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.dorantech.gamiransteptester.core.broadcast.manager.RunningBroadcastManager
import ir.dorantech.gamiransteptester.core.broadcast.model.RunningBroadcastResult
import ir.dorantech.gamiransteptester.core.broadcast.receiver.ActivityTypeReceiver
import ir.dorantech.gamiransteptester.core.logging.LogManager
import ir.dorantech.gamiransteptester.core.model.ResultCallback
import ir.dorantech.gamiransteptester.core.model.ResultModel
import ir.dorantech.gamiransteptester.util.ActivityType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class RunningBroadcastManagerImpl @Inject constructor(
    @ApplicationContext val context: Context,
    val logManager: LogManager
): RunningBroadcastManager {
    private val broadcastResultState = MutableStateFlow<RunningBroadcastResult?>(null)
    private lateinit var pendingIntent: PendingIntent

    @SuppressLint("MissingPermission")
    override fun registerRunningBroadcast(callback: ResultCallback<String>) {
        pendingIntent = getPendingIntent()
        val task = ActivityRecognition.getClient(context)
            .requestActivityUpdates(10000L, pendingIntent)

        task.addOnSuccessListener {
            logManager.addLog("Broadcast initial is successful")
            callback.onSuccess("Broadcast has been registered Successfully")
        }

        task.addOnFailureListener { e ->
            logManager.addLog("Broadcast initial is failed: $e")
            callback.onError("Broadcast has been registered Failed")
        }
    }

    private fun getPendingIntent(): PendingIntent {
        val intentAction2 =
            "ir.dorantech.gamiransteptester.ACTION_PROCESS_ACTIVITY_TRANSITIONS2"
        val intent = Intent(context, ActivityTypeReceiver::class.java).apply {
            action = intentAction2
        }
        val flags =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
                PendingIntent.FLAG_ALLOW_UNSAFE_IMPLICIT_INTENT or PendingIntent.FLAG_MUTABLE
            else PendingIntent.FLAG_MUTABLE


        return PendingIntent.getBroadcast(
            context,
            0,
            intent,
            flags
        )
    }

    @SuppressLint("MissingPermission")
    override fun unregisterRunningBroadcast(callback: ResultCallback<String>) {
        val client = ActivityRecognition.getClient(context)
        val task = client.removeActivityTransitionUpdates(pendingIntent)
        task.addOnSuccessListener {
            logManager.addLog("unregisterBroadcastSuccess")
            pendingIntent.cancel()
            callback.onSuccess("Broadcast has been unregistered Successfully")
        }
        task.addOnFailureListener { e ->
            logManager.addLog("unregisterBroadcastFailed by ${e.message}")
            callback.onError("Broadcast has been unregistered Failed")
        }
    }

    override fun getBroadcastResponse(broadcastResult: ResultModel<RunningBroadcastResult>) {
        when (broadcastResult) {
            is ResultModel.Success -> {
                val detectActivity = ActivityType.fromInt(broadcastResult.data.detectedActivity)
                if (broadcastResultState.value != broadcastResult.data){
                if (broadcastResult.data.detectedActivity == DetectedActivity.RUNNING) {
                    logManager.addLog("DetectActivity: $detectActivity")
                    logManager.addLog("confidence: ${broadcastResult.data.confidence}")
                }
                broadcastResultState.value = broadcastResult.data
            }else logManager.addLog("The same values")
            }

            is ResultModel.Error -> {
                logManager.addLog("Broadcast Failed: ${broadcastResult.message}")
                broadcastResultState.value = null
            }
        }
    }

    override suspend fun getBroadcastResult(): StateFlow<RunningBroadcastResult?> = broadcastResultState
}