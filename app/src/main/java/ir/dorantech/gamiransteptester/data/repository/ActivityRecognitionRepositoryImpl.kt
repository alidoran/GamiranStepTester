package ir.dorantech.gamiransteptester.data.repository

import android.content.Intent
import android.util.Log
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity
import ir.dorantech.gamiransteptester.core.logging.LogManager
import ir.dorantech.gamiransteptester.domain.model.RecognitionEvent
import ir.dorantech.gamiransteptester.domain.repository.ActivityRecognitionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

import javax.inject.Inject

class ActivityRecognitionRepositoryImpl @Inject constructor(
    private val logManager: LogManager,
) : ActivityRecognitionRepository {

    private val _recognitionStateFlow = MutableStateFlow<RecognitionEvent?>(null)

    override fun processRecognitionIntent(intent: Intent): StateFlow<RecognitionEvent?> {
        if (ActivityRecognitionResult.hasResult(intent)) {
            logManager.addLog("processRecognitionIntent called with intent: $intent")
            val result = ActivityRecognitionResult.extractResult(intent)
            val detectedActivities = result?.probableActivities ?: emptyList()

            detectedActivities.forEach { activity ->
                val event = RecognitionEvent(
                    activityType = activity.toReadableActivity(),
                    confidence = activity.confidence
                )
                _recognitionStateFlow.value = event
            }
        } else {
            logManager.addLog("No recognition result found in intent")
            Log.w("ActivityRecognition", "No recognition result found in intent")
        }
        return _recognitionStateFlow
    }

    private fun DetectedActivity.toReadableActivity(): String {
        return when (this.type) {
            DetectedActivity.IN_VEHICLE -> "In Vehicle"
            DetectedActivity.ON_BICYCLE -> "On Bicycle"
            DetectedActivity.ON_FOOT -> "On Foot"
            DetectedActivity.RUNNING -> "Running"
            DetectedActivity.STILL -> "Still"
            DetectedActivity.TILTING -> "Tilting"
            DetectedActivity.WALKING -> "Walking"
            DetectedActivity.UNKNOWN -> "Unknown"
            else -> "Unidentified"
        }
    }
}
