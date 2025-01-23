package com.sbaygildin.pushwords.progress

import androidx.lifecycle.ViewModel
import com.sbaygildin.pushwords.data.di.AppPreferencesManager
import com.sbaygildin.pushwords.data.di.ProgressRepository
import com.sbaygildin.pushwords.data.model.DailyAverage
import com.sbaygildin.pushwords.data.model.ProgressData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ProgressViewModel @Inject constructor(
    preferencesManager: AppPreferencesManager,
    private val progressRepository: ProgressRepository,
) : ViewModel() {

    val userName: Flow<String> = preferencesManager.userNameFlow

    private val _dailyToAllTimeSwitcher = MutableStateFlow(true)
    val dailyToAllTimeSwitcher: StateFlow<Boolean> = _dailyToAllTimeSwitcher

    private val _totalLearnedWords = MutableStateFlow(0)
    val totalLearnedWords: StateFlow<Int> = _totalLearnedWords

    private val _guessedRightAway = MutableStateFlow(0)
    val guessedRightAway: StateFlow<Int> = _guessedRightAway

    private val _correctAnswer = MutableStateFlow(0)
    val correctAnswer: StateFlow<Int> = _correctAnswer

    private val _wrongAnswer = MutableStateFlow(0)
    val wrongAnswer: StateFlow<Int> = _wrongAnswer

    private val _dailyData = MutableStateFlow<List<DailyAverage>>(emptyList())
    val dailyData: StateFlow<List<DailyAverage>> = _dailyData

    fun toggleDailyToAllTimeSwitcher() {
        _dailyToAllTimeSwitcher.value = !_dailyToAllTimeSwitcher.value
    }

    suspend fun fetchProgressData(currentTime: Long) {
        val progressDataList: List<ProgressData> = if (_dailyToAllTimeSwitcher.value) {
            progressRepository.getProgressData(0L, currentTime)
        } else {
            val dayAgo = currentTime - 24 * 60 * 60 * 1000L
            progressRepository.getProgressData(dayAgo, currentTime)
        }
        calculateProgressData(progressDataList)

        val startTime = currentTime - 7 * 24 * 60 * 60 * 1000L
        val dailyDataList = progressRepository.getDailyAverages(startTime, currentTime)
        _dailyData.value = dailyDataList
    }

    private fun calculateProgressData(progressDataList: List<ProgressData>) {
        _totalLearnedWords.value = progressDataList.sumOf { it.learnedWords }
        _guessedRightAway.value = progressDataList.sumOf { it.guessedRightAway }
        _correctAnswer.value = progressDataList.sumOf { it.correctAnswers }
        _wrongAnswer.value = progressDataList.sumOf { it.wrongAnswers }
    }
}