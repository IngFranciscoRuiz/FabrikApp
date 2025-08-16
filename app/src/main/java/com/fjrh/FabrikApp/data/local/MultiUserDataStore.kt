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

private val Context.multiUserDataStore: DataStore<Preferences> by preferencesDataStore(name = "multi_user_prefs")

@Singleton
class MultiUserDataStore @Inject constructor(
    private val context: Context
) {
    
    private val isMultiUserActiveKey = booleanPreferencesKey("is_multi_user_active")
    
    val isMultiUserActive: Flow<Boolean> = context.multiUserDataStore.data
        .map { preferences ->
            preferences[isMultiUserActiveKey] ?: false
        }
    
    suspend fun setMultiUserActive(active: Boolean) {
        context.multiUserDataStore.edit { preferences ->
            preferences[isMultiUserActiveKey] = active
        }
        println("MultiUserDataStore: Multiusuario establecido a: $active")
    }
    
    suspend fun clearMultiUserState() {
        context.multiUserDataStore.edit { preferences ->
            preferences.remove(isMultiUserActiveKey)
        }
        println("MultiUserDataStore: Estado de multiusuario limpiado")
    }
}
