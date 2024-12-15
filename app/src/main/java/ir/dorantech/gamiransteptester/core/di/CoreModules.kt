package ir.dorantech.gamiransteptester.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.dorantech.gamiransteptester.core.logging.LogManager

@Module
@InstallIn(SingletonComponent::class)
object CoreModules {
    @Provides
    fun provideLogManager(): LogManager {
        return LogManager
    }
}