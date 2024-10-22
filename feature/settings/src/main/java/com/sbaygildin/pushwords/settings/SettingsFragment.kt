package com.sbaygildin.pushwords.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val darkMode = viewModel.darkMode.collectAsState(initial = false).value
                val notificationsEnabled =
                    viewModel.notifications.collectAsState(initial = false).value
                val volume = viewModel.volume.collectAsState(initial = 0.5f).value
                val notificationFrequencyIndex = 0
                val userName = viewModel.userName.collectAsState(initial = "").value ?: ""
                val languageForRiddles =
                    viewModel.languageForRiddles.collectAsState(initial = "originalLanguage").value
                val isQuietModeEnabled =
                    viewModel.isQuietModeEnabled.collectAsState(initial = false).value

                SettingsScreen(
                    isDarkModeEnabled = darkMode,
                    onDarkModeChange = { viewModel.setDarkMode(it) },
                    notificationsEnabled = notificationsEnabled,
                    onNotificationsChange = { viewModel.setNotifications(it) },
                    notificationFrequencyIndex = notificationFrequencyIndex,
                    onNotificationFrequencyChange = { /* handle it */ },
                    volume = volume,
                    onVolumeChange = { viewModel.setVolume(it) },
                    userName = userName,
                    onUserNameChange = { viewModel.setUserName(it) },
                    languageForRiddles = languageForRiddles,
                    onLanguageChange = { viewModel.setLanguageForRiddles(it) },
                    isQuietModeEnabled = isQuietModeEnabled,
                    onQuietModeChange = { viewModel.setQuietModeEnabled(it) }
                )
            }
        }
    }
}

