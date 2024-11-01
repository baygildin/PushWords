package com.sbaygildin.pushwords.settings

import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
    var isDropDownExpanded by remember { mutableStateOf(false) }
    var tempUserName by remember { mutableStateOf(userName) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .padding(bottom = (84.dp))
    ) {
        Text(
            text = "Settings",
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .align(Alignment.End),
            style = TextStyle(
                fontSize = 24.sp,
                shadow = Shadow(
                    color = Color.Gray, blurRadius = 1f
                )
            )
        )
        Text(
            text = "Your name: $userName", fontWeight = FontWeight.SemiBold, modifier = Modifier
                .padding(top = 8.dp)
                .align(Alignment.Start)
        )
        OutlinedTextField(
            value = tempUserName,
            onValueChange = { tempUserName = it },
            label = { Text("Enter your name") }
        )
        Button(
            onClick = {
                onUserNameChange(tempUserName)
                tempUserName = ""
            }
        )
        { Text("Save Name") }

        Text(
            "Enable Notifications", fontWeight = FontWeight.SemiBold, modifier = Modifier
                .padding(top = 8.dp)
                .align(Alignment.Start)
        )
        Switch(checked = notificationsEnabled, onCheckedChange = onNotificationsChange)

        Text("Notification Frequency", fontWeight = FontWeight.SemiBold)
        Button(onClick = { isDropDownExpanded = !isDropDownExpanded }) {
            Text(text = "Select Frequency")
        }
        Box() {
            DropdownMenu(
                expanded = isDropDownExpanded,
                onDismissRequest = { isDropDownExpanded = false }
            ) {
                val notificationOptions =
                    listOf("15 Minutes", "30 Minutes", "1 Hour", "6 Hours", "1 Day")
                notificationOptions.forEachIndexed { index, period ->
                    DropdownMenuItem(
                        onClick = {
                            onNotificationFrequencyChange(index)
                            isDropDownExpanded = false
                        },
                        text = {
                            Text(
                                text = period,
                                color = if (notificationFrequencyIndex == index) Color.Blue else Color.Black
                            )
                        }
                    )
                }
            }
        }

        Text(
            "Volume", fontWeight = FontWeight.SemiBold, modifier = Modifier
                .padding(top = 8.dp)
                .align(Alignment.Start)
        )
        Slider(
            value = volume,
            onValueChange = onVolumeChange,
            valueRange = 0f..1f
        )
        Text(
            "Quiet Mode", fontWeight = FontWeight.SemiBold, modifier = Modifier
                .padding(top = 8.dp)
                .align(Alignment.Start)
        )
        Checkbox(checked = isQuietModeEnabled, onCheckedChange = onQuietModeChange)
        Text(
            "Dark Mode", fontWeight = FontWeight.SemiBold, modifier = Modifier
                .padding(top = 8.dp)
                .align(Alignment.Start)
        )
        Switch(checked = isDarkModeEnabled, onCheckedChange = onDarkModeChange)

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
        Text(
            "Language for Riddles",
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
        RadioButton(
            selected = (selectedOption == "originalLanguage"),
            onClick = {
                selectedOption = "originalLanguage"
                onLanguageChange("originalLanguage")
            }
        )
        Text("Original Language")

        RadioButton(
            selected = (selectedOption == "translationLanguage"),
            onClick = {
                selectedOption = "translationLanguage"
                onLanguageChange("translationLanguage")
            }
        )
        Text("Translation Language")

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
