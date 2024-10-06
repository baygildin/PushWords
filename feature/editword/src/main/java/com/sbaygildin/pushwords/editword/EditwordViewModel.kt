package com.sbaygildin.pushwords.editword

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sbaygildin.pushwords.data.di.WordTranslationDao
import com.sbaygildin.pushwords.data.model.DifficultyLevel
import com.sbaygildin.pushwords.data.model.WordTranslation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditwordViewModel @Inject constructor(
    private val wordTranslationDao: WordTranslationDao
) : ViewModel() {

    fun getWordTranslationById(id: Long, onWordFound: (WordTranslation?) -> Unit) {
        viewModelScope.launch {
            val wordTranslation = wordTranslationDao.getWordTranslationById(id)
            onWordFound(wordTranslation)
        }
    }

    fun updateWordTranslation(
        context: Context,
        wordTranslation: WordTranslation,
        originalWord: String,
        translatedWord: String,
        isLearned: Boolean,
        difficultyLevel: DifficultyLevel,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val updatedWord = wordTranslation.copy(
            originalWord = originalWord,
            translatedWord = translatedWord,
            isLearned = isLearned,
            difficultyLevel = difficultyLevel
        )

        viewModelScope.launch {
            try {
                wordTranslationDao.update(updatedWord)
                Toast.makeText(context, "Word updated!", Toast.LENGTH_SHORT).show()
                onSuccess()
            } catch (e: Exception) {
                Toast.makeText(context, "Error updating word", Toast.LENGTH_SHORT).show()
                onFailure()
            }
        }
    }
}
