package ir.dorantech.gamiransteptester.domain.usecase

import ir.dorantech.gamiransteptester.domain.model.SensorManagerRegistrationModel
import ir.dorantech.gamiransteptester.domain.model.UseCaseResult

interface SensorStepCountUseCase {
    operator fun invoke(): UseCaseResult<SensorManagerRegistrationModel>
    fun unregisterStepListener()
}