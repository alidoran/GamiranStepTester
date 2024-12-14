package ir.dorantech.gamiransteptester.domain.usecase

import kotlinx.coroutines.flow.Flow

interface StepCountUseCase {
    fun invoke(): Flow<String>
}