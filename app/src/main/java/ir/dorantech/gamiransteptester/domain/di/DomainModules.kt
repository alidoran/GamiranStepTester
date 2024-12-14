package ir.dorantech.gamiransteptester.domain.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.dorantech.gamiransteptester.domain.repository.StepCounterRepository
import ir.dorantech.gamiransteptester.domain.usecase.StepCountUseCase
import ir.dorantech.gamiransteptester.domain.usecase.impl.StepCountUseCaseImpl

@Module
@InstallIn(SingletonComponent::class)
object DomainModules {
    @Provides
    fun provideStepCountUseCase(stepCounterRepository: StepCounterRepository): StepCountUseCase {
        return StepCountUseCaseImpl(stepCounterRepository)
    }
}