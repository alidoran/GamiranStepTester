package ir.dorantech.gamiransteptester.core.datastore.impl

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.dorantech.gamiransteptester.core.datastore.PreferencesHelper
import ir.dorantech.gamiransteptester.core.datastore.util.extension.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class PreferencesHelperImpl(
    @ApplicationContext val context: Context
): PreferencesHelper {
    private companion object {
        private val TOTAL_STEPS = intPreferencesKey("sample_text")
    }

    override suspend fun setTotalSteps(steps: Int) {
        context.dataStore.edit { preferences ->
            preferences[TOTAL_STEPS] = steps
        }
    }

    override fun getTotalSteps(): Flow<Int> {
        return context.dataStore.data.map { preferences ->
            preferences[TOTAL_STEPS] ?: 0
        }
    }
}