package com.fjrh.FabrikApp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "onboarding_preferences")

@Singleton
class OnboardingDataStore @Inject constructor(
    private val context: Context
) {
    private val hasSeenOnboardingKey = booleanPreferencesKey("has_seen_onboarding")

    val hasSeenOnboarding: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[hasSeenOnboardingKey] ?: false
        }

    suspend fun markOnboardingAsSeen() {
        context.dataStore.edit { preferences ->
            preferences[hasSeenOnboardingKey] = true
        }
    }
}
