package ir.dorantech.gamiransteptester.core.datastore

import kotlinx.coroutines.flow.Flow

interface PreferencesHelper {
    suspend fun setTotalSteps(steps: Int)
    fun getTotalSteps(): Flow<Int>

    suspend fun setAutoStartOpened(steps: Boolean)
    fun getAutoStartOpened(): Flow<Boolean>
}