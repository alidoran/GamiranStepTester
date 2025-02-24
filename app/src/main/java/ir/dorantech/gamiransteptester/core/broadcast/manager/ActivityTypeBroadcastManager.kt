package ir.dorantech.gamiransteptester.core.broadcast.manager

import com.google.android.gms.location.DetectedActivity
import ir.dorantech.gamiransteptester.core.broadcast.model.DetectActivityResult
import ir.dorantech.gamiransteptester.core.model.ResultCallback
import ir.dorantech.gamiransteptester.core.model.ResultModel
import kotlinx.coroutines.flow.StateFlow

interface ActivityTypeBroadcastManager {
    val broadcastResultState: StateFlow<DetectActivityResult?>

    suspend fun setBroadcastResult(broadcastResult: ResultModel<List<DetectedActivity>>)

    fun unregisterActivityTypeListener(callback: ResultCallback<String>)

    fun registerActivityTypeListener(callback: ResultCallback<String>)
}