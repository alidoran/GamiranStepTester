package ir.dorantech.gamiransteptester.domain.usecase.impl

import ir.dorantech.gamiransteptester.core.logging.LogManager
import ir.dorantech.gamiransteptester.domain.model.StepResult
import ir.dorantech.gamiransteptester.domain.repository.StepCounterRepository
import ir.dorantech.gamiransteptester.domain.usecase.StepCountUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class StepCountUseCaseImpl(
    private val stepCounterRepository: StepCounterRepository
) : StepCountUseCase {
    override fun invoke(): Flow<String> {
        return callbackFlow {
            stepCounterRepository.stepFlow().collect {
                if (it is StepResult.Success) {
                    LogManager.addLog("StepCountUseCase: ${it.steps}")
                    trySend(it.steps.toString())
                }
                if (it is StepResult.Error) {
                    trySend(it.message)
                }
            }
        }
    }
}