package ir.dorantech.gamiransteptester.core.broadcast.model

import kotlinx.coroutines.flow.StateFlow

data class BroadcastRegisterModel (
    val success: StateFlow<String>,
    val failure: StateFlow<String>,
)