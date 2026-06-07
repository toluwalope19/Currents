package com.app.currents.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

expect class DataStoreFactory {
    fun create(): DataStore<Preferences>
}

internal const val DATA_STORE_FILE_NAME = "currents_prefs.preferences_pb"