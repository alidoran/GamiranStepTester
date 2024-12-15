package ir.dorantech.gamiransteptester.core.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.ActivityTransitionEvent
import com.google.android.gms.location.ActivityTransitionResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

class ActivityRecognitionReceiver @Inject constructor() : BroadcastReceiver() {
    private val _eventsFlow = MutableSharedFlow<ActivityTransitionEvent>()
    val eventsFlow: SharedFlow<ActivityTransitionEvent> = _eventsFlow

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("ActivityRecognitionReceiver", "onReceive called")
        val result = ActivityTransitionResult.extractResult(intent)
        if (result != null)
            result.transitionEvents.forEach { event ->
                _eventsFlow.tryEmit(event)
            }
        else Log.d("ActivityRecognitionReceiver", "ActivityTransitionResult is null")
    }
}