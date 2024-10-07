package com.sbaygildin.pushwords.progress

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
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
                val progressText = userName + """
            Ваш прогресс:
            Вы выучили $totalLearnedWords новых слов(а)!
            Угадали с первого раза $guessedRightAway слов(а)!
            И повторили $correctAnswer слов(а)!
        """.trimIndent()

                binding?.let { safeBinding ->
                    safeBinding.tvTotalLearnedWords.text = progressText
                }
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
        val wrongEntries = ArrayList<BarEntry>()
        val labels = arrayOf(
            getString(R.string.Mo),
            getString(R.string.Tu), getString(R.string.We), getString(R.string.Th),
            getString(R.string.Fr), getString(R.string.Sa), getString(R.string.Su)
        )

        val correctAnswersPerDay = mutableMapOf<String, Float>()
        val wrongAnswersPerDay = mutableMapOf<String, Float>()
        Log.d("ProgressData", "Daily Data: $dailyData")

        labels.forEach {
            correctAnswersPerDay[it] = 0f
            wrongAnswersPerDay[it] = 0f
        }
        dailyData.forEach { data ->
            val dayOfWeek = when (data.day.toInt()) {
                0 -> getString(R.string.Su)
                1 -> getString(R.string.Mo)
                2 -> getString(R.string.Tu)
                3 -> getString(R.string.We)
                4 -> getString(R.string.Th)
                5 -> getString(R.string.Fr)
                6 -> getString(R.string.Sa)
                else -> ""
            }
            correctAnswersPerDay[dayOfWeek]?.let {
                correctAnswersPerDay[dayOfWeek] = it + data.avgCorrect
            }
            wrongAnswersPerDay[dayOfWeek]?.let {
                wrongAnswersPerDay[dayOfWeek] = it + data.avgWrong
            }
        }

        labels.forEachIndexed { index, day ->
            correctEntries.add(BarEntry(index.toFloat(), correctAnswersPerDay[day] ?: 0f))
            wrongEntries.add(BarEntry(index.toFloat(), wrongAnswersPerDay[day] ?: 0f))
        }

        val correctDataSet = BarDataSet(correctEntries, "Правильные ответы")
        correctDataSet.color = ColorTemplate.COLORFUL_COLORS[1]
        correctDataSet.valueTextSize = 12f

        val wrongDataSet = BarDataSet(wrongEntries, "Неправильные ответы")
        wrongDataSet.color = ColorTemplate.COLORFUL_COLORS[0]
        wrongDataSet.valueTextSize = 14f

        val barData = BarData(correctDataSet, wrongDataSet)
        val groupSpace = 0.4f
        val barSpace = 0.05f
        val barWidth = 0.2f

        barData.barWidth = barWidth


        binding.weeklyProgressChart.description.isEnabled = false

        binding.weeklyProgressChart.data = barData
        binding.weeklyProgressChart.groupBars(0f, groupSpace, barSpace)
        binding.weeklyProgressChart.setExtraOffsets(0f, 20f, 0f, 0f)
        binding.weeklyProgressChart.legend.isEnabled = true
        binding.weeklyProgressChart.legend.textSize = 12f
        binding.weeklyProgressChart.legend.formToTextSpace = 8f
        binding.weeklyProgressChart.legend.isWordWrapEnabled = true
        binding.weeklyProgressChart.legend.xEntrySpace = 64f
        binding.weeklyProgressChart.legend.yEntrySpace = 10f
        binding.weeklyProgressChart.legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        binding.weeklyProgressChart.invalidate()

        val xAxis = binding.weeklyProgressChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.textSize = 14f  // Увеличиваем размер текста
        xAxis.granularity = 1f  // Шаг для оси X
        xAxis.setLabelCount(labels.size, true)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawLabels(true)
        xAxis.setCenterAxisLabels(true)

        Log.d("ProgressData", "Entries for chart: $correctEntries")

        // Настройка оси Y
        val yAxisLeft = binding.weeklyProgressChart.axisLeft
        yAxisLeft.axisMinimum = 0f
        yAxisLeft.textSize = 14f
        yAxisLeft.axisMaximum = maxOf(
            (correctAnswersPerDay.values.maxOrNull() ?: 0f),
            (wrongAnswersPerDay.values.maxOrNull() ?: 0f)
        ) + 5

        yAxisLeft.setDrawGridLines(false)
        binding.weeklyProgressChart.axisRight.isEnabled = false

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

