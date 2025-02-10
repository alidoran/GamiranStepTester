package ir.dorantech.gamiransteptester.core.datastore.util.extension

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

internal val Context.dataStore by preferencesDataStore(name = "settings")

