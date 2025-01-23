package com.sbaygildin.pushwords.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sbaygildin.pushwords.data.di.AppPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: AppPreferencesManager
) : ViewModel() {

    val darkMode: Flow<Boolean> = preferencesManager.darkModeFlow
    val notifications: Flow<Boolean> = preferencesManager.notificationsFlow
    val volume: Flow<Float> = preferencesManager.volumeFlow
    val notificationsInterval: Flow<Long> = preferencesManager.notificationIntervalFlow
    val languageForRiddles: Flow<String> = preferencesManager.languageForRiddlesFlow
    val isQuietModeEnabled: Flow<Boolean> = preferencesManager.isQuietModeEnabledFlow
    val userName: StateFlow<String?> = preferencesManager.userNameFlow.stateIn(
        viewModelScope, SharingStarted.Lazily, null
    )


    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setDarkMode(enabled)
        }
    }

    fun setLanguageForRiddles(language: String) {
        viewModelScope.launch {
            Log.d("LanguageSelection", "in SettingsViewModel Language saved: $language")
            preferencesManager.saveLanguageForRiddles(language)
        }
    }

    fun setNotifications(enabled: Boolean) {
        Log.d("Notification456", "Notifications set to: $enabled")
        viewModelScope.launch {
            preferencesManager.setNotifications(enabled)
        }
    }
    fun setNotificationInterval(interval: Long) {
        viewModelScope.launch {
            preferencesManager.setNotificationInterval(interval)
        }
    }

    fun setQuietModeEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setQuietModeEnabled(enabled)
        }
    }

    fun setVolume(volume: Float) {
        viewModelScope.launch {
            preferencesManager.setVolume(volume)
        }
    }

    fun setUserName(name: String) {
        viewModelScope.launch {
            preferencesManager.setUserName(name)
        }
    }

}
