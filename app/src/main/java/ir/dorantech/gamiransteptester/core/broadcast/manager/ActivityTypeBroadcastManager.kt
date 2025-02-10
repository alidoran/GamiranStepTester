package ir.dorantech.gamiransteptester.core.broadcast.manager

import ir.dorantech.gamiransteptester.core.broadcast.model.ActivityTypeBroadcastResult
import ir.dorantech.gamiransteptester.core.model.ResultCallback
import ir.dorantech.gamiransteptester.core.model.ResultModel
import kotlinx.coroutines.flow.StateFlow

interface ActivityTypeBroadcastManager {
    val broadcastResultState: StateFlow<ActivityTypeBroadcastResult?>

    suspend fun setBroadcastResult(broadcastResult: ResultModel<ActivityTypeBroadcastResult>)

    fun unregisterActivityTypeBroadcast(callback: ResultCallback<String>)

    fun registerActivityTypeBroadcast(callback: ResultCallback<String>)
}