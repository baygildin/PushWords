package com.sbaygildin.pushwords.editword

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.sbaygildin.pushwords.common.validateInput
import com.sbaygildin.pushwords.data.model.DifficultyLevel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditWordScreen(
    viewModel: EditwordViewModel,
    wordId: Long,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(wordId) {
        viewModel.loadWordData(wordId)
    }

    when (uiState) {
        is EditWordUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is EditWordUiState.Success -> {

            val word = (uiState as EditWordUiState.Success).word

            var originalWord by remember { mutableStateOf(word.originalWord) }
            var translatedWord by remember { mutableStateOf(word.translatedWord) }
            var isLearned by remember { mutableStateOf(word.isLearned) }
            var selectedDifficultyLevel by remember { mutableStateOf(word.difficultyLevel) }


            val difficultyLevels = DifficultyLevel.entries.map { it.name }

            Scaffold { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(paddingValues)
                                .padding(16.dp)
                        ) {
                            OutlinedTextField(
                                value = originalWord,
                                onValueChange = { originalWord = it },
                                label = { Text(context.getString(com.sbaygildin.pushwords.common.R.string.txt_original_word)) },
                                isError = originalWord.isEmpty(),
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                            )
                            if (originalWord.isEmpty()) {
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
                                isError = translatedWord.isEmpty(),
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                            )
                            if (translatedWord.isEmpty()) {
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
                                Text(text = context.getString(R.string.txt_is_learned))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            var expanded by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = !expanded }
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
                                        .menuAnchor(
                                            MenuAnchorType.PrimaryNotEditable,
                                            enabled = true
                                        )
                                        .fillMaxWidth()
                                )
                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    difficultyLevels.forEach { level ->
                                        DropdownMenuItem(
                                            text = { Text(text = level) },
                                            onClick = {
                                                selectedDifficultyLevel =
                                                    DifficultyLevel.valueOf(level)
                                                expanded = false
                                            }
                                        )
                                    }
                                }

                            }
                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Button(
                                    onClick = {
                                        if (validateInput(originalWord, translatedWord)) {
                                            viewModel.updateWordTranslation(
                                                originalWord = originalWord,
                                                translatedWord = translatedWord,
                                                isLearned = isLearned,
                                                difficultyLevel = selectedDifficultyLevel,

                                                onSuccess = {
                                                    Toast.makeText(
                                                        context,
                                                        context.getString(com.sbaygildin.pushwords.common.R.string.word_updated),
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    onBack()
                                                },
                                                onFailure = {
                                                    Toast.makeText(
                                                        context,
                                                        context.getString(com.sbaygildin.pushwords.common.R.string.error_updating_word),
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            )
                                        }
                                    }) {
                                    Text(text = context.getString(com.sbaygildin.pushwords.common.R.string.tv_btn_save))
                                }
                                Button(onClick = onBack) {
                                    Text(text = context.getString(com.sbaygildin.pushwords.common.R.string.btn_tv_back))
                                }
                            }
                        }
                    }
                }
            }
        }


        is EditWordUiState.Error -> {
            LaunchedEffect(Unit) {
                Toast.makeText(
                    context,
                    context.getString(com.sbaygildin.pushwords.common.R.string.word_not_found),
                    Toast.LENGTH_SHORT
                ).show()
                onBack()
            }
        }

    }
}
