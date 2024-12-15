package ir.dorantech.gamiransteptester.domain.usecase

import kotlinx.coroutines.flow.Flow

interface StepCountUseCase {
    operator fun invoke(): Flow<String>
}