package ir.dorantech.gamiransteptester.core.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.dorantech.gamiransteptester.core.broadcast.manager.RunningBroadcastManager
import ir.dorantech.gamiransteptester.core.broadcast.manager.impl.RunningBroadcastManagerImpl
import ir.dorantech.gamiransteptester.core.logging.LogManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreModules {
    @Provides
    fun provideLogManager(): LogManager {
        return LogManager
    }

    @Singleton
    @Provides
    fun provideRunningBroadcastManager(context: Context, logManager: LogManager): RunningBroadcastManager {
        return RunningBroadcastManagerImpl(context, logManager)
    }
}