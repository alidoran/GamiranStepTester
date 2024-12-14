package ir.dorantech.gamiransteptester.domain.repository

import ir.dorantech.gamiransteptester.domain.model.StepResult
import kotlinx.coroutines.flow.Flow

interface StepCounterRepository {
    suspend fun stepFlow(): Flow<StepResult>
}