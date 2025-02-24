package ir.dorantech.gamiransteptester.core.broadcast.manager.impl

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.DetectedActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.dorantech.gamiransteptester.core.broadcast.manager.ActivityTypeBroadcastManager
import ir.dorantech.gamiransteptester.core.broadcast.model.DetectActivityResult
import ir.dorantech.gamiransteptester.core.broadcast.receiver.DetectActivityBroadcastReceiver
import ir.dorantech.gamiransteptester.core.broadcast.util.getDetectActivityModel
import ir.dorantech.gamiransteptester.core.logging.LogManager
import ir.dorantech.gamiransteptester.core.model.ResultCallback
import ir.dorantech.gamiransteptester.core.model.ResultModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class ActivityTypeBroadcastManagerImpl @Inject constructor(
    @ApplicationContext val context: Context,
    val logManager: LogManager
) : ActivityTypeBroadcastManager {
    private val _broadcastResult = MutableStateFlow<DetectActivityResult?>(null)
    override val broadcastResultState: StateFlow<DetectActivityResult?> =
        _broadcastResult.asStateFlow()
    private lateinit var pendingIntent: PendingIntent

    @SuppressLint("MissingPermission")
    override fun registerActivityTypeListener(callback: ResultCallback<String>) {
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
        val intentAction =
            "ir.dorantech.gamiransteptester.ACTION_PROCESS_ACTIVITY_TYPE_RECEIVER"
        val intent = Intent(context, DetectActivityBroadcastReceiver::class.java).apply {
            action = intentAction
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
    override fun unregisterActivityTypeListener(callback: ResultCallback<String>) {
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

    override suspend fun setBroadcastResult(broadcastResult: ResultModel<List<DetectedActivity>>) {
        when (broadcastResult) {
            is ResultModel.Success -> {
                val walkingResult =
                    broadcastResult.data.find { it.type == DetectedActivity.WALKING }
                walkingResult?.confidence?.let { confidence ->
                    if (confidence >= 70)
                        _broadcastResult.emit(getDetectActivityModel(walkingResult))
                }
            }

            is ResultModel.Error -> logManager.addLog("Broadcast Failed: ${broadcastResult.message}")
        }
    }
}