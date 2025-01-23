package com.sbaygildin.pushwords.editword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sbaygildin.pushwords.data.di.WordTranslationDao
import com.sbaygildin.pushwords.data.model.DifficultyLevel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditwordViewModel @Inject constructor(
    private val wordTranslationDao: WordTranslationDao,
) : ViewModel() {

    private val _uiState = MutableStateFlow<EditWordUiState>(EditWordUiState.Loading)
    val uiState: StateFlow<EditWordUiState> = _uiState

    fun loadWordData(wordId: Long) {
        viewModelScope.launch {
            val wordTranslation = wordTranslationDao.getWordTranslationById(wordId)
            if (wordTranslation != null) {
                _uiState.value = EditWordUiState.Success(wordTranslation)
            } else {
                _uiState.value = EditWordUiState.Error
            }
        }
    }

    fun updateWordTranslation(
        originalWord: String,
        translatedWord: String,
        isLearned: Boolean,
        difficultyLevel: DifficultyLevel,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {
        val currentState = _uiState.value
        if (currentState is EditWordUiState.Success) {

            val word = currentState.word

            val updatedWord = word.copy(
                originalWord = originalWord,
                translatedWord = translatedWord,
                isLearned = isLearned,
                difficultyLevel = difficultyLevel
            )

            viewModelScope.launch {
                try {
                    wordTranslationDao.update(updatedWord)
                    onSuccess()
                } catch (e: Exception) {
                    onFailure()

                }
            }
        } else {
            onFailure()
        }

    }
}
