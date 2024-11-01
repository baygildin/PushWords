package com.sbaygildin.pushwords.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit


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
                val notificationInterval = viewModel.notificationsInterval.collectAsState(
                    initial = TimeUnit.DAYS.toMillis(1)
                ).value
                val notificationFrequencyIndex = when (notificationInterval) {
                    TimeUnit.MINUTES.toMillis(15) -> 0
                    TimeUnit.MINUTES.toMillis(30) -> 1
                    TimeUnit.HOURS.toMillis(1) -> 2
                    TimeUnit.HOURS.toMillis(6) -> 3
                    TimeUnit.DAYS.toMillis(1) -> 4
                    else -> 4
                }

                val userName = viewModel.userName.collectAsState(initial = "").value ?: ""
                val languageForRiddles =
                    viewModel.languageForRiddles.collectAsState(initial = "originalLanguage").value
                val isQuietModeEnabled =
                    viewModel.isQuietModeEnabled.collectAsState(initial = false).value

                SettingsScreen(
                    isDarkModeEnabled = darkMode,
                    onDarkModeChange = { viewModel.setDarkMode(it) },
                    notificationsEnabled = notificationsEnabled,
                    onNotificationsChange = {
                        Log.d("Notification456", "Notifications enabled: SettingsScreen")
                        viewModel.setNotifications(it)
                    },
                    notificationFrequencyIndex = notificationFrequencyIndex,
                    onNotificationFrequencyChange = { selectedIndex ->
                        val selectedInterval = when (selectedIndex) {
                            0 -> TimeUnit.MINUTES.toMillis(15)
                            1 -> TimeUnit.MINUTES.toMillis(30)
                            2 -> TimeUnit.HOURS.toMillis(1)
                            3 -> TimeUnit.HOURS.toMillis(6)
                            4 -> TimeUnit.DAYS.toMillis(1)
                            else -> TimeUnit.HOURS.toMillis(6)
                        }
                        viewModel.setNotificationInterval(selectedInterval)
                        Log.d("Notification456", "selectedInterval $selectedInterval")
                    },
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

