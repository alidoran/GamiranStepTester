package ir.dorantech.gamiransteptester.core.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.dorantech.gamiransteptester.core.broadcast.manager.ActivityTypeBroadcastManager
import ir.dorantech.gamiransteptester.core.broadcast.manager.impl.ActivityTypeBroadcastManagerImpl
import ir.dorantech.gamiransteptester.core.datastore.PreferencesHelper
import ir.dorantech.gamiransteptester.core.datastore.impl.PreferencesHelperImpl
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
    fun provideRunningBroadcastManager(context: Context, logManager: LogManager): ActivityTypeBroadcastManager {
        return ActivityTypeBroadcastManagerImpl(context, logManager)
    }

    @Singleton
    @Provides
    fun providePreferencesHelper(context: Context): PreferencesHelper{
        return PreferencesHelperImpl(context)
    }
}