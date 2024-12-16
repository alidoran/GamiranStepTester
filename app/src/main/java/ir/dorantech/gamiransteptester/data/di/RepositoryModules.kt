package ir.dorantech.gamiransteptester.data.di

import android.app.PendingIntent
import ir.dorantech.gamiransteptester.data.repository.StepCounterRepositoryImpl
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.dorantech.gamiransteptester.data.provider.ActivityRecognitionPendingIntentProvider
import ir.dorantech.gamiransteptester.data.repository.ActivityRecognitionRepositoryImpl
import ir.dorantech.gamiransteptester.data.repository.ActivityRecognitionRequestRepositoryImpl
import ir.dorantech.gamiransteptester.domain.repository.ActivityRecognitionRepository
import ir.dorantech.gamiransteptester.domain.repository.ActivityRecognitionRequestRepository
import ir.dorantech.gamiransteptester.domain.repository.StepCounterRepository

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModules {
    @Provides
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    fun provideStepCounterRepository(
        context: Context
    ): StepCounterRepository {
        return StepCounterRepositoryImpl(context)
    }

    @Provides
    fun provideActivityRecognitionRequestRepository(
        context: Context
    ): ActivityRecognitionRequestRepository {
        return ActivityRecognitionRequestRepositoryImpl(context)
    }

    @Provides
    fun provideActivityRecognitionPendingIntentProvider(
        context: Context
    ): ActivityRecognitionPendingIntentProvider {
        return ActivityRecognitionPendingIntentProvider(context)
    }

    @Provides
    fun provideActivityRecognitionPendingIntent(
        provider: ActivityRecognitionPendingIntentProvider
    ): PendingIntent {
        return provider.getPendingIntent()
    }

    @Provides
    fun provideActivityRecognitionRepository(
        repository: ActivityRecognitionRepositoryImpl
    ): ActivityRecognitionRepository {
        return repository
    }
}
