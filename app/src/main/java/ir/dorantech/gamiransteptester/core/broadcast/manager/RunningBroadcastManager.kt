package ir.dorantech.gamiransteptester.core.broadcast.manager

import ir.dorantech.gamiransteptester.core.broadcast.model.RunningBroadcastResult
import ir.dorantech.gamiransteptester.core.model.ResultCallback
import ir.dorantech.gamiransteptester.core.model.ResultModel
import kotlinx.coroutines.flow.StateFlow

interface RunningBroadcastManager {
    fun getBroadcastResponse(broadcastResult: ResultModel<RunningBroadcastResult>)

    fun unregisterRunningBroadcast(callback: ResultCallback<String>)

    fun registerRunningBroadcast(callback: ResultCallback<String>)

    suspend fun getBroadcastResult(): StateFlow<RunningBroadcastResult?>
}