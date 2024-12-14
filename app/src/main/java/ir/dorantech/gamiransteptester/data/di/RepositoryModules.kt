package ir.dorantech.gamiransteptester.data.di

import ir.dorantech.gamiransteptester.data.repository.StepCounterRepositoryImpl
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.dorantech.gamiransteptester.domain.repository.StepCounterRepository

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModules {

    @Provides
    fun provideStepCounterRepository(
        @ApplicationContext context: Context
    ): StepCounterRepository {
        return StepCounterRepositoryImpl(context)
    }

    @Provides
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }
}
