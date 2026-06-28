package com.asfine.mythought.core

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "app_settings")

class AppSettingsStore(
    private val context: Context
) {
    private object Keys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val FINGERPRINT = booleanPreferencesKey("fingerprint_enabled")
    }

    val darkModeEnabled: Flow<Boolean> = context.settingsDataStore.data
        .map { prefs: Preferences -> prefs[Keys.DARK_MODE] ?: false }

    val fingerprintEnabled: Flow<Boolean> = context.settingsDataStore.data
        .map { prefs: Preferences -> prefs[Keys.FINGERPRINT] ?: false }

    suspend fun setDarkModeEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[Keys.DARK_MODE] = enabled
        }
    }

    suspend fun setFingerprintEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[Keys.FINGERPRINT] = enabled
        }
    }
}
