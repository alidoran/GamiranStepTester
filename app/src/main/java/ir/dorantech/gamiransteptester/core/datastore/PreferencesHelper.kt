package ir.dorantech.gamiransteptester.core.datastore

import kotlinx.coroutines.flow.Flow

interface PreferencesHelper {
    suspend fun setTotalSteps(steps: Int)
    fun getTotalSteps(): Flow<Int>
}