package com.sbaygildin.pushwords.progress

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.sbaygildin.pushwords.data.model.DailyAverage
import com.sbaygildin.pushwords.data.di.ProgressRepository
import com.sbaygildin.pushwords.progress.databinding.FragmentProgressBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.*

@AndroidEntryPoint
class ProgressFragment : Fragment() {
    private val viewModel: ProgressViewModel by viewModels()
    private var _binding: FragmentProgressBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var progressRepository: ProgressRepository

    private var totalLearnedWords: Int = 0
    private var guessedRightAway: Int = 0
    private var correctAnswer: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProgressBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            val currentTime = System.currentTimeMillis()
            val startTime = currentTime - 7 * 24 * 60 * 60 * 1000L // 7 дней назад
            val dailyData = progressRepository.getDailyAverages(startTime, currentTime)

            totalLearnedWords = calculateTotalLearnedWords(dailyData)
            guessedRightAway = calculateGuessedRightAway(dailyData)
            correctAnswer = calculateCorrectAnswers(dailyData)

            updateProgressText()
            setupBarChart(dailyData)
        }
    }

    private fun updateProgressText() {
        var userName = ""
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userName.collectLatest { name ->
                userName = if (name == "") "" else name + "!\n"
                Log.d("sdsdads", "$name dfsdfsd")
                val progressText = userName + """
            Ваш прогресс:
            Вы выучили $totalLearnedWords новых слов(а)!
            Угадали с первого раза $guessedRightAway слов(а)!
            И повторили $correctAnswer слов(а)!
        """.trimIndent()

                binding?.let { safeBinding ->
                    safeBinding.tvTotalLearnedWords.text = progressText }
            }
        }

    }

    private fun calculateTotalLearnedWords(dailyData: List<DailyAverage>): Int {
        return dailyData.sumOf { it.avgCorrect.toInt() }
    }

    private fun calculateGuessedRightAway(dailyData: List<DailyAverage>): Int {
        return (dailyData.sumOf { it.avgCorrect.toInt() * 0.7 }).toInt()
    }


    private fun calculateCorrectAnswers(dailyData: List<DailyAverage>): Int {
        return dailyData.sumOf { it.avgCorrect.toInt() + it.avgWrong.toInt() }
    }

    private fun setupBarChart(dailyData: List<DailyAverage>) {
        val correctEntries = ArrayList<BarEntry>()
        val labels = arrayOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
        val dayIndexMap = mutableMapOf<String, Int>()

        // Инициализация массива для всех дней недели нулями
        val defaultData = Array(labels.size) { 0f }

        labels.forEachIndexed { index, label ->
            dayIndexMap[label] = index
        }

        dailyData.forEach { data ->
            val dayOfWeek = getDayOfWeek(data.day.toLong())
            Log.d("BarChartData", "Day: $dayOfWeek, AvgCorrect: ${data.avgCorrect}")
            val index = dayIndexMap[dayOfWeek] ?: 0
            defaultData[index] = data.avgCorrect
        }

        // Заполнение столбцов для каждого дня недели от библиотеки
        defaultData.forEachIndexed { index, value ->
            correctEntries.add(BarEntry(index.toFloat(), value))
        }

        //Просто настройка Графика
        val correctDataSet = BarDataSet(correctEntries, "Правильные ответы")
        correctDataSet.color = ColorTemplate.COLORFUL_COLORS[1]

        val barData = BarData(correctDataSet)
        binding.weeklyProgressChart.description.isEnabled = false
        binding.weeklyProgressChart.data = barData
        binding.weeklyProgressChart.setExtraOffsets(0f, 20f, 0f, 0f)
        binding.weeklyProgressChart.legend.isEnabled = false

        // Ось X
        val xAxis = binding.weeklyProgressChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.granularity = 1f
        xAxis.setLabelCount(labels.size, true)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawLabels(true)
        xAxis.labelRotationAngle = 0f

        // Ось Y
        val yAxisLeft = binding.weeklyProgressChart.axisLeft
        yAxisLeft.axisMinimum = 0f
        yAxisLeft.setDrawGridLines(false)

        val yAxisRight = binding.weeklyProgressChart.axisRight
        yAxisRight.isEnabled = false

        binding.weeklyProgressChart.invalidate()
    }


    private fun getDayOfWeek(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp * 1000
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "Пн"
            Calendar.TUESDAY -> "Вт"
            Calendar.WEDNESDAY -> "Ср"
            Calendar.THURSDAY -> "Чт"
            Calendar.FRIDAY -> "Пт"
            Calendar.SATURDAY -> "Сб"
            Calendar.SUNDAY -> "Вс"
            else -> ""
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

