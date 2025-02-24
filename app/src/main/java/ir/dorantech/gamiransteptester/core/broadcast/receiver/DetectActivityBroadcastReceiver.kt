package ir.dorantech.gamiransteptester.core.broadcast.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.ActivityRecognitionResult
import dagger.hilt.android.EntryPointAccessors
import ir.dorantech.gamiransteptester.core.broadcast.manager.ActivityTypeBroadcastManager
import ir.dorantech.gamiransteptester.core.logging.LogManager
import ir.dorantech.gamiransteptester.core.model.ResultModel
import ir.dorantech.gamiransteptester.domain.di.HiltBroadcastReceiverEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetectActivityBroadcastReceiver : BroadcastReceiver() {
    lateinit var logManager: LogManager
    private lateinit var runningBroadcastManager: ActivityTypeBroadcastManager

    override fun onReceive(context: Context, intent: Intent) {
        val appContext = context.applicationContext
        val hiltEntryPoint = EntryPointAccessors.fromApplication(
            appContext, HiltBroadcastReceiverEntryPoint::class.java
        )
        logManager = hiltEntryPoint.logManager()
        runningBroadcastManager = hiltEntryPoint.runningBroadcastManager()

        if (ActivityRecognitionResult.hasResult(intent)) {
            val result = ActivityRecognitionResult.extractResult(intent)
            val detectedActivities = result?.probableActivities ?: emptyList()

            repeat(detectedActivities.size) {
                processRecognitionIntent(intent)
            }
        }
    }

    private fun processRecognitionIntent(intent: Intent) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            val result = ActivityRecognitionResult.extractResult(intent)
            val detectedActivities = result?.probableActivities ?: emptyList()
            val broadcastSuccessResult =
                ResultModel.Success(detectedActivities)
            CoroutineScope(Dispatchers.Main).launch {
                runningBroadcastManager.setBroadcastResult(broadcastSuccessResult)
            }
        } else {
            logManager.addLog("No recognition result found in intent")
            val broadcastFailedResult = ResultModel.Error(
                "No recognition result found in intent"
            )
            CoroutineScope(Dispatchers.Main).launch {
                runningBroadcastManager.setBroadcastResult(broadcastFailedResult)
            }
        }
    }
}