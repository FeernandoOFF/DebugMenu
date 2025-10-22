package com.tapado.debugmenuDemo.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val DATASTORE_NAME = "demo_prefs"

val Context.demoDataStore: DataStore<Preferences> by preferencesDataStore(name = DATASTORE_NAME)

class AppRepository(private val context: Context) {
    // Keys
    private val userNameKey = stringPreferencesKey("user_name")
    private val featureEnabledKey = booleanPreferencesKey("feature_enabled")

    val dataStore: DataStore<Preferences> get() = context.demoDataStore

    // Exposed flows
    val userName: Flow<String> = context.demoDataStore.data
        .catch { ex -> if (ex is IOException) emit(emptyPreferences()) else throw ex }
        .map { it[userNameKey] ?: "Guest" }

    val featureEnabled: Flow<Boolean> = context.demoDataStore.data
        .catch { ex -> if (ex is IOException) emit(emptyPreferences()) else throw ex }
        .map { it[featureEnabledKey] ?: false }

    suspend fun setUserName(name: String) {
        context.demoDataStore.edit { prefs ->
            prefs[userNameKey] = name
        }
    }

    suspend fun setFeatureEnabled(enabled: Boolean) {
        context.demoDataStore.edit { prefs ->
            prefs[featureEnabledKey] = enabled
        }
    }
}