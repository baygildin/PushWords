package com.sbaygildin.pushwords.progress

import androidx.lifecycle.ViewModel
import com.sbaygildin.pushwords.data.di.AppPreferencesManager
import com.sbaygildin.pushwords.data.di.ProgressRepository
import com.sbaygildin.pushwords.data.model.ProgressData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class ProgressViewModel @Inject constructor(
    preferencesManager: AppPreferencesManager,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    val userName: Flow<String> = preferencesManager.userNameFlow
    var dailyToAllTimeSwitcher: Boolean = true

    var totalLearnedWords: Int = 0
    var guessedRightAway: Int = 0
    var correctAnswer: Int = 0
    var wrongAnswer = 0





    fun calculateProgressData(progressDataList: List<ProgressData>){

        totalLearnedWords = progressDataList.sumOf { it.learnedWords }
        guessedRightAway = progressDataList.sumOf { it.guessedRightAway }
        correctAnswer = progressDataList.sumOf { it.correctAnswers}
        wrongAnswer = progressDataList.sumOf{ it.wrongAnswers}

    }
    suspend fun fetchProgressData(currentTime: Long) {
        val progressDataList: List<ProgressData> = if (dailyToAllTimeSwitcher) {
            progressRepository.getProgressData(0L, currentTime)
        } else {
            val dayAgo = currentTime - 24 * 60 * 60 * 1000L // 1 день назад
            progressRepository.getProgressData(dayAgo, currentTime)
        }
        calculateProgressData(progressDataList)
    }

}