package com.app.currents.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences(private val dataStore: DataStore<Preferences>) {

    companion object {
        val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
        val SELECTED_CATEGORIES = stringSetPreferencesKey("selected_categories")
    }

    val isOnboardingComplete: Flow<Boolean> = dataStore.data
        .map { prefs -> prefs[ONBOARDING_COMPLETE] ?: false }

    val selectedCategories: Flow<Set<String>> = dataStore.data
        .map { prefs -> prefs[SELECTED_CATEGORIES] ?: emptySet() }


    suspend fun saveSelectedCategories(categories: Set<String>) {
        dataStore.edit { prefs ->
            prefs[SELECTED_CATEGORIES] = categories
        }
    }

    suspend fun setOnboardingComplete() {
        dataStore.edit { prefs ->
            prefs[ONBOARDING_COMPLETE] = true
        }
    }
}