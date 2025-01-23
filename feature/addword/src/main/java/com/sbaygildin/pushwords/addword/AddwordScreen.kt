package com.sbaygildin.pushwords.addword

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sbaygildin.pushwords.common.validateInput
import com.sbaygildin.pushwords.data.model.DifficultyLevel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWordScreen(
    viewModel: AddwordViewModel,
    onBack: () -> Unit,
    openFilePicker: () -> Unit,
) {
    val context = LocalContext.current
    val importSuccess by viewModel.importSuccess.collectAsState()
    var showHelpDialog by remember { mutableStateOf(false) }

    // Text fields
    var originalWord by remember { mutableStateOf("") }
    var translatedWord by remember { mutableStateOf("") }
    var isLearned by remember { mutableStateOf(false) }
    var selectedDifficultyLevel by remember { mutableStateOf(DifficultyLevel.MEDIUM) }
    val difficultyLevels = DifficultyLevel.entries.map { it.name }
    var showErrors by remember { mutableStateOf(false) }





    LaunchedEffect(importSuccess) {
        if (importSuccess == true) {
            Toast.makeText(context, context.getString(R.string.words_imported), Toast.LENGTH_SHORT)
                .show()
            viewModel.resetImportSuccess()
        }
    }


    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        )
        {
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = context.getString(R.string.add_the_word_to_your_vocabulary),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = originalWord,
                        onValueChange = { originalWord = it },
                        label = { Text(context.getString(com.sbaygildin.pushwords.common.R.string.txt_original_word)) },
                        isError = originalWord.isBlank() && showErrors,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (showErrors && originalWord.isBlank()) {
                        Text(
                            text = context.getString(com.sbaygildin.pushwords.common.R.string.tv_this_field_is_required),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = translatedWord,
                        onValueChange = { translatedWord = it },
                        label = { Text(context.getString(com.sbaygildin.pushwords.common.R.string.txt_translated_word)) },
                        isError = translatedWord.isBlank() && showErrors,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (showErrors && translatedWord.isBlank()) {
                        Text(
                            text = context.getString(com.sbaygildin.pushwords.common.R.string.tv_this_field_is_required),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isLearned,
                            onCheckedChange = { isLearned = it }

                        )
                        Text(text = context.getString(R.string.ch_box_is_learned))

                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    //Dif.level Radiobutton
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = {
                            expanded = !expanded
                            Log.d("DropdownMenu", "Clicked!")
                        }
                    ) {

                        OutlinedTextField(
                            value = selectedDifficultyLevel.name,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(context.getString(com.sbaygildin.pushwords.common.R.string.difficulty_level)) },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = {
                                expanded = false
                                Log.d("DropdownMenu", "Dismissed")
                            }
                        ) {
                            difficultyLevels.forEach { level ->
                                DropdownMenuItem(
                                    text = { Text(text = level) },
                                    onClick = {
                                        selectedDifficultyLevel = DifficultyLevel.valueOf(level)
                                        expanded = false
                                        Log.d("DropdownMenu", "Selected: $level")
                                    }
                                )

                            }

                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {
                        if (validateInput(originalWord, translatedWord)) {
                            viewModel.saveWord(
                                originalWord,
                                translatedWord,
                                isLearned,
                                selectedDifficultyLevel
                            )
                            Toast.makeText(
                                context,
                                context.getString(R.string.word_added),
                                Toast.LENGTH_SHORT
                            ).show()
                            onBack()
                        } else {
                            showErrors = true
                        }
                    }) {
                        Text(text = context.getString(com.sbaygildin.pushwords.common.R.string.tv_btn_save))
                    }
                    Button(onClick = onBack) {
                        Text(text = context.getString(com.sbaygildin.pushwords.common.R.string.btn_tv_back))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            //English Levels
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = context.getString(R.string.tv_select_english_level_to_load_words),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    val levels = listOf(
                        Triple("A1", R.raw.a1level, DifficultyLevel.EASY),
                        Triple("A2", R.raw.a2level, DifficultyLevel.EASY),
                        Triple("B1", R.raw.b1level, DifficultyLevel.MEDIUM),
                        Triple("B2", R.raw.b2level, DifficultyLevel.MEDIUM),
                        Triple("C1", R.raw.c1level, DifficultyLevel.HARD),
                        Triple("C2", R.raw.c2level, DifficultyLevel.HARD)
                    )

                    levels.chunked(2).forEach { rowLevels ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            rowLevels.forEach { (levelName, resourceId, difficulty) ->
                                Button(onClick = {
                                    viewModel.importWordsFromRaw(context, resourceId, difficulty)
                                }) {
                                    Text(text = levelName)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            //Import
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = context.getString(R.string.txt_upload_your_words_from_a_txt_file),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = openFilePicker,
                        modifier = Modifier.fillMaxWidth()
                    )
                    {
                        Icon(Icons.Filled.Search, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = context.getString(R.string.txt_import_your_text_files))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    IconButton(onClick = { showHelpDialog = true }) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = context.getString(R.string.txt_help)
                        )

                    }
                }
            }
        }
    }

    if (showHelpDialog) {
        HelpDialog(onDismiss = { showHelpDialog = false })

    }
}

