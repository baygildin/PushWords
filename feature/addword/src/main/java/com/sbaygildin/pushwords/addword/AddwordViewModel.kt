package com.sbaygildin.pushwords.addword

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sbaygildin.pushwords.data.di.WordTranslationDao
import com.sbaygildin.pushwords.data.model.DifficultyLevel
import com.sbaygildin.pushwords.data.model.WordTranslation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddwordViewModel @Inject constructor(
    private val wordTranslationDao: WordTranslationDao
) : ViewModel() {
    private val _importSuccess = MutableStateFlow(false)
    val importSuccess = _importSuccess.asStateFlow()

    fun saveWord(
        originalWord: String,
        translatedWord: String,
        isLearned: Boolean,
        difficultyLevel: DifficultyLevel
    ) {
        val newWord = WordTranslation(
            originalWord = originalWord,
            translatedWord = translatedWord,
            isLearned = isLearned,
            originalLanguage = "English",
            translationLanguage = "Russian",
            dateAdded = Date(System.currentTimeMillis()),
            difficultyLevel = difficultyLevel
        )

        viewModelScope.launch {
            wordTranslationDao.insertWordTranslation(newWord)
        }
    }

    fun readWordsFromFile(context: Context, uri: Uri) {
        viewModelScope.launch {
            val inputStream = context.contentResolver.openInputStream(uri)
            val content = inputStream?.bufferedReader().use { it?.readText() }
            content?.let { parseAndSaveWords(it) }
        }
    }
    fun importWordsFromRaw(context: Context, resourceId: Int, difficultyLevel: DifficultyLevel) {
        viewModelScope.launch {
            val inputStream = context.resources.openRawResource(resourceId)
            val content = inputStream.bufferedReader().use { it.readText() }
            parseAndSaveLevelWords(content, difficultyLevel)
        }
    }
    private fun parseAndSaveLevelWords(content: String, difficultyLevel: DifficultyLevel) {
        val lines = content.split("\n")
        lines.forEach { line ->
            val parts = line.split("=")
            if (parts.size == 2) {
                val originalWord = parts[0].trim()
                val translatedWord = parts[1].trim()

                val newWord = WordTranslation(
                    originalWord = originalWord,
                    translatedWord = translatedWord,
                    isLearned = false,
                    originalLanguage = "English",
                    translationLanguage = "Russian",
                    dateAdded = Date(System.currentTimeMillis()),
                    difficultyLevel = difficultyLevel
                )

                viewModelScope.launch {
                    wordTranslationDao.insertWordTranslation(newWord)
                }
            }
        }
        _importSuccess.value = true
    }
    fun resetImportSuccess() {
        _importSuccess.value = false
    }




    private fun parseAndSaveWords(content: String) {
        val lines = content.split("\n")
        lines.forEach { line ->
            val parts = line.split("=")
            if (parts.size == 2) {
                val originalWord = parts[0].trim()
                val translatedWord = parts[1].trim()

                val newWord = WordTranslation(
                    originalWord = originalWord,
                    translatedWord = translatedWord,
                    isLearned = false,
                    originalLanguage = "English",
                    translationLanguage = "Russian",
                    dateAdded = Date(System.currentTimeMillis()),
                    difficultyLevel = DifficultyLevel.MEDIUM
                )

                viewModelScope.launch {
                    wordTranslationDao.insertWordTranslation(newWord)
                }
            }
        }
        _importSuccess.value = true

    }
}
