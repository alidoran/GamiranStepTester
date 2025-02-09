package ir.dorantech.gamiransteptester.core.broadcast.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.ActivityRecognitionResult
import dagger.hilt.android.AndroidEntryPoint
import ir.dorantech.gamiransteptester.core.broadcast.manager.RunningBroadcastManager
import ir.dorantech.gamiransteptester.core.broadcast.manager.impl.RunningBroadcastManagerImpl
import ir.dorantech.gamiransteptester.core.broadcast.model.RunningBroadcastResult
import ir.dorantech.gamiransteptester.core.logging.LogManager
import ir.dorantech.gamiransteptester.core.model.ResultModel
import javax.inject.Inject

@AndroidEntryPoint
class ActivityTypeReceiver : BroadcastReceiver() {
    @Inject
    lateinit var logManager: LogManager

    @Inject
    lateinit var runningBroadcastManager: RunningBroadcastManager

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
                val broadcastSuccessResult: ResultModel<RunningBroadcastResult> =
                    ResultModel.Success(
                        RunningBroadcastResult(activity.type, activity.confidence)
                    )
                runningBroadcastManager.getBroadcastResponse(broadcastSuccessResult)
            }
        } else {
            logManager.addLog("No recognition result found in intent")
            val broadcastFailedResult: ResultModel<RunningBroadcastResult> = ResultModel.Error(
                "No recognition result found in intent"
            )
            runningBroadcastManager.getBroadcastResponse(broadcastFailedResult)
        }
    }
}