package ir.dorantech.gamiransteptester.domain.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.dorantech.gamiransteptester.domain.repository.ActivityRecognitionRequestRepository
import ir.dorantech.gamiransteptester.domain.repository.StepCounterRepository
import ir.dorantech.gamiransteptester.domain.usecase.ActivityRecognitionRequestUseCase
import ir.dorantech.gamiransteptester.domain.usecase.PermissionUseCase
import ir.dorantech.gamiransteptester.domain.usecase.StepCountUseCase
import ir.dorantech.gamiransteptester.domain.usecase.impl.ActivityRecognitionRequestUseCaseImpl
import ir.dorantech.gamiransteptester.domain.usecase.impl.PermissionUseCaseImpl
import ir.dorantech.gamiransteptester.domain.usecase.impl.StepCountUseCaseImpl

@Module
@InstallIn(SingletonComponent::class)
object DomainModules {
    @Provides
    fun provideStepCountUseCase(stepCounterRepository: StepCounterRepository): StepCountUseCase {
        return StepCountUseCaseImpl(stepCounterRepository)
    }

    @Provides
    fun provideRequestActivityTransitionUpdatesUseCase(
        activityRecognitionRequestRepository: ActivityRecognitionRequestRepository
    ): ActivityRecognitionRequestUseCase {
        return ActivityRecognitionRequestUseCaseImpl(activityRecognitionRequestRepository)
    }

    @Provides
    fun providePermissionUseCase(
        context: Context
    ): PermissionUseCase {
        return PermissionUseCaseImpl(context)
    }
}