package com.sbaygildin.pushwords.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sbaygildin.pushwords.data.di.AppPreferencesManager
import com.sbaygildin.pushwords.data.di.ProgressRepository
import com.sbaygildin.pushwords.data.di.WordTranslationDao
import com.sbaygildin.pushwords.data.model.ProgressData
import com.sbaygildin.pushwords.data.model.WordTranslation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val wordTranslationDao: WordTranslationDao,
    private val progressRepository: ProgressRepository,
    preferencesManager: AppPreferencesManager
) : ViewModel() {

    val volume: Flow<Float> = preferencesManager.volumeFlow

    var correctAnswer = 0  // Количество правильных ответов
    var wrongAnswer = 0 //Количество неправильных ответов
    var guessedRightAway = 0 //Количество угаданных ответов с первого раза
    var learnedWords = 0 //Количество новых изученных слов. isLearned должен быть false  у них
    var  wordCache: List<WordTranslation> = emptyList()
    //Сделай Массив, чтоб быстрее работало. Все равно знаешь размер
    val languageForRiddles: Flow<String> = preferencesManager.languageForRiddlesFlow



    init {
        viewModelScope.launch {
            wordTranslationDao.getAllWordTranslations().collect{ wordList ->
                wordCache = if (wordList.size > 1000) {
                    wordList.shuffled().take(1000)
                } else {
                    wordList
                }
            }
        }
    }
    fun getCachedWords(): List<WordTranslation> {
        return wordCache
    }
    fun recordProgressData(correct: Int, wrong: Int, guessedRightAway: Int, learnedWords: Int) {
        viewModelScope.launch {
            val progressData = ProgressData(
                timestamp = System.currentTimeMillis(),
                correctAnswers = correct,
                wrongAnswers = wrong,
                guessedRightAway = guessedRightAway,
                learnedWords = learnedWords
            )
            progressRepository.insertProgressData(progressData)
        }
    }

    fun resetCounters() {
        correctAnswer = 0
        wrongAnswer = 0
        guessedRightAway = 0
        learnedWords = 0
    }

    fun updateWordAsLearned(wordId: Long) {
        viewModelScope.launch {
            wordTranslationDao.updateWordAsLearned(wordId)
        }
    }
}
