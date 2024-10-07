package com.sbaygildin.pushwords.settings

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
    val language: Flow<String> = preferencesManager.languageFlow
    val notifications: Flow<Boolean> = preferencesManager.notificationsFlow
    val volume: Flow<Float> = preferencesManager.volumeFlow
    val languageForRiddles: Flow<String> = preferencesManager.languageForRiddlesFlow

    //    val userName: Flow<String> = preferencesManager.userNameFlow
    val userName: StateFlow<String?> = preferencesManager.userNameFlow.stateIn(
        viewModelScope, SharingStarted.Lazily, null
    )

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setDarkMode(enabled)
        }
    }

    fun setLanguage(language: String) {
        viewModelScope.launch {
            preferencesManager.setLanguage(language)
        }
    }
    fun setLanguageForRiddles(language: String) {
        viewModelScope.launch {
            preferencesManager.saveLanguageForRiddles(language)
        }
    }

    fun setNotifications(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setNotifications(enabled)
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
