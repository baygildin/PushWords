package com.sbaygildin.pushwords.data.di


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.Preferences

import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")


@Singleton
class AppPreferencesManager @Inject constructor(@ApplicationContext context: Context) {

    private val dataStore = context.dataStore


    companion object {
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        val LANGUAGE_KEY = stringPreferencesKey("language")
        val NOTIFICATIONS_KEY = booleanPreferencesKey("notifications")
        val VOLUME_KEY = floatPreferencesKey("volume_level")
        val USERNAME_KEY = stringPreferencesKey("user_name")

    }

    val darkModeFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[DARK_MODE_KEY] ?: false
    }

    val languageFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[LANGUAGE_KEY] ?: "English"
    }

    val notificationsFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[NOTIFICATIONS_KEY] ?: true
    }

    val volumeFlow: Flow<Float> = dataStore.data.map { preferences ->
        preferences[VOLUME_KEY] ?: 0.5f
    }
    val userNameFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[USERNAME_KEY] ?: ""
    }

    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = enabled
        }
    }

    suspend fun setLanguage(language: String) {
        dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language
        }
    }

    suspend fun setNotifications(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_KEY] = enabled
        }
    }

    suspend fun setVolume(volume: Float) {
        dataStore.edit {preferences ->
            preferences[VOLUME_KEY] = volume
        }
    }
    suspend fun setUserName(name: String) {
        dataStore.edit {preferences ->
            preferences[USERNAME_KEY] = name
        }
    }
}
