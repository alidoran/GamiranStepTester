package ir.dorantech.gamiransteptester.domain.model

import kotlinx.coroutines.flow.StateFlow

data class SensorManagerRegistrationModel(
    val steps: StateFlow<Int>,
    val accuracy: StateFlow<Int>,
)
