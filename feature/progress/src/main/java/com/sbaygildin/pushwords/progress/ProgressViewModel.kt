package com.sbaygildin.pushwords.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.sbaygildin.pushwords.data.di.AppPreferencesManager
import com.sbaygildin.pushwords.data.di.ProgressRepository
import com.sbaygildin.pushwords.data.model.ProgressData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val preferencesManager: AppPreferencesManager,
    private val progressRepository: ProgressRepository,
) : ViewModel() {

    val userName: Flow<String> = preferencesManager.userNameFlow
    var dailyToAllTimeSwitcher = MutableStateFlow(true)

    private val _pieData = MutableStateFlow(PieData())
    val pieData: StateFlow<PieData> = _pieData

    private val _barData = MutableStateFlow(BarData())
    val barData: StateFlow<BarData> = _barData

    init {
        updateProgressData()
    }

    fun updateProgressData() {
        viewModelScope.launch {
            val currentTime = System.currentTimeMillis()
            val startTime =
                if (dailyToAllTimeSwitcher.value) currentTime - 24 * 60 * 60 * 1000L else 0L
            val progressDataList = progressRepository.getProgressData(startTime, currentTime)
            calculateProgressData(progressDataList)
            updatePieChartData()
            updateBarDataData()
        }
    }

    var totalLearnedWords: Int = 0
    var guessedRightAway: Int = 0
    var correctAnswer: Int = 0
    var wrongAnswer = 0


    fun calculateProgressData(progressDataList: List<ProgressData>) {

        totalLearnedWords = progressDataList.sumOf { it.learnedWords }
        guessedRightAway = progressDataList.sumOf { it.guessedRightAway }
        correctAnswer = progressDataList.sumOf { it.correctAnswers }
        wrongAnswer = progressDataList.sumOf { it.wrongAnswers }

    }

    private fun updatePieChartData() {
        val pieEntries = listOf(
            PieEntry(totalLearnedWords.toFloat(), "Learned Words"),
            PieEntry(guessedRightAway.toFloat(), "Guessed Right Away"),
            PieEntry(correctAnswer.toFloat(), "Correct Answers"),
            PieEntry(wrongAnswer.toFloat(), "Wrong Answers")
        )
        val pieDataSet = PieDataSet(pieEntries, "Your Progress").apply {
            colors = ColorTemplate.COLORFUL_COLORS.toList()
            valueTextSize = 14f
        }
        _pieData.update { PieData(pieDataSet) }
    }


    fun updateBarDataData() {
        val labels = listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")
        val correctEntries = listOf(
            BarEntry(0f, 3f), BarEntry(1f, 5f), BarEntry(2f, 2f), // sample data
            BarEntry(3f, 1f), BarEntry(4f, 4f), BarEntry(5f, 7f), BarEntry(6f, 3f)
        )
        val wrongEntries = listOf(
            BarEntry(0f, 1f), BarEntry(1f, 2f), BarEntry(2f, 0f),
            BarEntry(3f, 1f), BarEntry(4f, 3f), BarEntry(5f, 2f), BarEntry(6f, 1f)
        )

        val correctDataSet = BarDataSet(correctEntries, "Correct Answers").apply {
            color = ColorTemplate.COLORFUL_COLORS[1]
            valueTextSize = 12f
        }
        val wrongDataSet = BarDataSet(wrongEntries, "Wrong Answers").apply {
            color = ColorTemplate.COLORFUL_COLORS[0]
            valueTextSize = 12f
        }

        _barData.update { BarData(correctDataSet, wrongDataSet) }
    }

}