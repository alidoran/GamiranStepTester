package ir.dorantech.gamiransteptester.core.broadcast.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.ActivityRecognitionResult
import dagger.hilt.android.AndroidEntryPoint
import ir.dorantech.gamiransteptester.core.broadcast.manager.ActivityTypeBroadcastManager
import ir.dorantech.gamiransteptester.core.broadcast.model.ActivityTypeBroadcastResult
import ir.dorantech.gamiransteptester.core.logging.LogManager
import ir.dorantech.gamiransteptester.core.model.ResultModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ActivityTypeBroadcastReceiver : BroadcastReceiver() {
    @Inject
    lateinit var logManager: LogManager

    @Inject
    lateinit var runningBroadcastManager: ActivityTypeBroadcastManager

    override fun onReceive(context: Context, intent: Intent) {
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

            detectedActivities.forEach { activity ->
                val broadcastSuccessResult: ResultModel<ActivityTypeBroadcastResult> =
                    ResultModel.Success(
                        ActivityTypeBroadcastResult(activity.type, activity.confidence)
                    )
                CoroutineScope(Dispatchers.IO).launch {
                    runningBroadcastManager.setBroadcastResult(broadcastSuccessResult)
                }

            }
        } else {
            logManager.addLog("No recognition result found in intent")
            val broadcastFailedResult: ResultModel<ActivityTypeBroadcastResult> = ResultModel.Error(
                "No recognition result found in intent"
            )
            CoroutineScope(Dispatchers.IO).launch {
                runningBroadcastManager.setBroadcastResult(broadcastFailedResult)
            }
        }
    }
}