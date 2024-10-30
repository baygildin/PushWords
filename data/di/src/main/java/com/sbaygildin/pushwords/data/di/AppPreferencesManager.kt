package com.sbaygildin.pushwords.data.di


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")


@Singleton
class AppPreferencesManager @Inject constructor(@ApplicationContext context: Context) {

    private val dataStore = context.dataStore


    companion object {
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        val LANGUAGE_FOR_RIDDLE_KEY = stringPreferencesKey("language_for_riddle")
        val NOTIFICATIONS_KEY = booleanPreferencesKey("notifications")
        val VOLUME_KEY = floatPreferencesKey("volume_level")
        val USERNAME_KEY = stringPreferencesKey("user_name")
        val NOTIFICATION_INTERVAL_KEY = longPreferencesKey("notification_interval")
        val QUIET_MODE_KEY = booleanPreferencesKey("quiet_mode")

    }

    val darkModeFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[DARK_MODE_KEY] ?: false
    }

    val notificationsFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[NOTIFICATIONS_KEY] ?: true
    }
    val notificationIntervalFlow: Flow<Long> = dataStore.data.map { preferences ->
        preferences[NOTIFICATION_INTERVAL_KEY] ?: TimeUnit.MINUTES.toMillis(60 * 24)
    }

    val volumeFlow: Flow<Float> = dataStore.data.map { preferences ->
        preferences[VOLUME_KEY] ?: 0.5f
    }
    val userNameFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[USERNAME_KEY] ?: ""
    }
    val languageForRiddlesFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[LANGUAGE_FOR_RIDDLE_KEY] ?: "random"
    }
    val isQuietModeEnabledFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[QUIET_MODE_KEY] ?: false
    }


    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = enabled
        }
    }


    suspend fun saveLanguageForRiddles(language: String) {
        dataStore.edit { preferences ->
            preferences[LANGUAGE_FOR_RIDDLE_KEY] = language
        }
    }

    suspend fun setNotificationInterval(interval: Long) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATION_INTERVAL_KEY] = interval
        }
    }

    suspend fun setNotifications(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_KEY] = enabled
        }
    }

    suspend fun setVolume(volume: Float) {
        dataStore.edit { preferences ->
            preferences[VOLUME_KEY] = volume
        }
    }

    suspend fun setUserName(name: String) {
        dataStore.edit { preferences ->
            preferences[USERNAME_KEY] = name
        }
    }

    suspend fun setQuietModeEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[QUIET_MODE_KEY] = enabled
        }
    }
}
