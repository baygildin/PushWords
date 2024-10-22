package com.sbaygildin.pushwords.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    isDarkModeEnabled: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    notificationsEnabled: Boolean,
    onNotificationsChange: (Boolean) -> Unit,
    notificationFrequencyIndex: Int,
    onNotificationFrequencyChange: (Int) -> Unit,
    volume: Float,
    onVolumeChange: (Float) -> Unit,
    userName: String,
    onUserNameChange: (String) -> Unit,
    languageForRiddles: String,
    onLanguageChange: (String) -> Unit,
    isQuietModeEnabled: Boolean,
    onQuietModeChange: (Boolean) -> Unit,

    ) {
    var menuExpanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(text = "Your name: $userName")
        OutlinedTextField(
            value = userName,
            onValueChange = onUserNameChange,
            label = { Text("Enter your name") }
        )
        Button(
            onClick = { onUserNameChange("") }
        )
        {
            Text("Save Name")
        }

        Text("Enable Notifications")
        Switch(checked = notificationsEnabled, onCheckedChange = onNotificationsChange)
        Text("Notification Frequency")
        Button(onClick = { menuExpanded = !menuExpanded }) {
            Text(text = "Select Frequency")
        }
        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false }
        ) {
            val notificationOptions =
                listOf("15 Minutes", "30 Minutes", "1 Hour", "6 Hours", "1 Day")
            notificationOptions.forEachIndexed { index, text ->
                DropdownMenuItem(
                    onClick = {
                        onNotificationFrequencyChange(index)
                        menuExpanded = false
                    },
                    text = { Text(text) }
                )
            }
        }


        Text("Volume")
        Slider(
            value = volume,
            onValueChange = onVolumeChange,
            valueRange = 0f..1f
        )
        Text("Quiet Mode")
        Checkbox(checked = isQuietModeEnabled, onCheckedChange = onQuietModeChange)
        Text("Dark Mode")
        Switch(checked = isDarkModeEnabled, onCheckedChange = onDarkModeChange)
        Text("Language for Riddles")
        LanguageSelectionScreen(
            selectedLanguage = languageForRiddles,
            onLanguageChange = onLanguageChange
        )

    }
}

@Composable
fun LanguageSelectionScreen(
    selectedLanguage: String,
    onLanguageChange: (String) -> Unit,
) {
    var selectedOption by remember { mutableStateOf(selectedLanguage) }

    Column {
        RadioButton(
            selected = (selectedOption == "originalLanguage"),
            onClick = {
                selectedOption = "originalLanguage"
                onLanguageChange("originalLanguage")
            }
        )
        Text("Original Language") // Подпись к RadioButton

        RadioButton(
            selected = (selectedOption == "translationLanguage"),
            onClick = {
                selectedOption = "translationLanguage"
                onLanguageChange("translationLanguage")
            }
        )
        Text("Translation Language") // Подпись к RadioButton

        RadioButton(
            selected = (selectedOption == "random"),
            onClick = {
                selectedOption = "random"
                onLanguageChange("random")
            }
        )
        Text("Random Language")
    }
}