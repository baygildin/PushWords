package com.sbaygildin.pushwords.progress

import android.graphics.Color
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
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.sbaygildin.pushwords.data.model.DailyAverage
import com.sbaygildin.pushwords.data.di.ProgressRepository
import com.sbaygildin.pushwords.data.model.ProgressData
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
            setupBarChart(dailyData)
            viewModel.fetchProgressData(currentTime)
            updateProgressPieChart()


            val progressDataList = progressRepository.getProgressData(startTime, currentTime)
            viewModel.calculateProgressData(progressDataList)
            val switchProgressType = binding.switchProgressType
            switchProgressType.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    viewModel.dailyToAllTimeSwitcher = false
                    switchProgressType.text = "Прогресс за день"
                } else {
                    viewModel.dailyToAllTimeSwitcher = true
                    switchProgressType.text = "Прогресс за все время"
                }
                lifecycleScope.launch {
                    viewModel.fetchProgressData(currentTime)
                    updateProgressPieChart()
                }
            }

        }
    }


    private fun updateProgressPieChart() {
        val pieEntries = ArrayList<PieEntry>()

        pieEntries.add(PieEntry(viewModel.totalLearnedWords.toFloat(), "Выученные слова"))
        pieEntries.add(PieEntry(viewModel.guessedRightAway.toFloat(), "Угаданные с первого раза"))
        pieEntries.add(PieEntry(viewModel.correctAnswer.toFloat(), "Повторённые слова"))
        pieEntries.add(PieEntry(viewModel.wrongAnswer.toFloat(), "Неправильные ответы"))

        val pieDataSet = PieDataSet(pieEntries, "Ваш прогресс")
        pieDataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
        pieDataSet.valueTextSize = 14f

        val pieData = PieData(pieDataSet)

        binding.pieChartProgress.run {
            data = pieData
            description.isEnabled = false
            isRotationEnabled = true
            setEntryLabelTextSize(12f)
            setUsePercentValues(false)
            legend.isEnabled = false
            invalidate()
        }
        binding.pieChartProgress.animateY(1000)
    }

    private fun setupBarChart(dailyData: List<DailyAverage>) {
        val correctEntries = ArrayList<BarEntry>()
        val wrongEntries = ArrayList<BarEntry>()

        val correctAnswersPerDay = mutableMapOf<String, Float>()
        val wrongAnswersPerDay = mutableMapOf<String, Float>()
        Log.d("ProgressData", "Daily Data: $dailyData")
        val calendar = Calendar.getInstance()
        val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val labels = arrayOf(
            getString(R.string.Su),
            getString(R.string.Mo),
            getString(R.string.Tu),
            getString(R.string.We),
            getString(R.string.Th),
            getString(R.string.Fr),
            getString(R.string.Sa)
        )
        Collections.rotate(labels.asList(), -currentDayOfWeek)

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
        val groupSpace = 0.18f
        val barSpace = 0.03f
        val barWidth = 0.35f


        //delete 0.0 value
        correctDataSet.setDrawValues(true)
        correctDataSet.valueFormatter = object : ValueFormatter() {
            override fun getBarLabel(barEntry: BarEntry): String {
                return if (barEntry.y == 0f) "" else barEntry.y.toString()
            }
        }
        wrongDataSet.setDrawValues(true)
        wrongDataSet.valueFormatter = object : ValueFormatter() {
            override fun getBarLabel(barEntry: BarEntry): String {
                return if (barEntry.y == 0f) "" else barEntry.y.toString()
            }
        }


        barData.barWidth = barWidth

        binding.weeklyProgressChart.run {
            description.isEnabled = false
            data = barData
            groupBars(0f, groupSpace, barSpace)
            setExtraOffsets(0f, 20f, 0f, 0f)
            legend.isEnabled = true
            legend.textSize = 12f
            legend.formToTextSpace = 8f
            legend.isWordWrapEnabled = true
            legend.xEntrySpace = 24f
            legend.yEntrySpace = -24f
            setVisibleXRangeMaximum(7f)
            moveViewToX(0f)
            legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            setFitBars(true)
            invalidate()
        }


        val xAxis = binding.weeklyProgressChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.run {
            textSize = 12f
            granularity = 1f
            setLabelCount(labels.size, false)
            setDrawGridLines(false)
            position = XAxis.XAxisPosition.BOTTOM
            setDrawLabels(true)
            setCenterAxisLabels(true)
            setAvoidFirstLastClipping(true)
        }

        val yAxisLeft = binding.weeklyProgressChart.axisLeft
        yAxisLeft.run {
            axisMinimum = 0f

            textSize = 14f
            axisMaximum = maxOf(
                (correctAnswersPerDay.values.maxOrNull() ?: 0f),
                (wrongAnswersPerDay.values.maxOrNull() ?: 0f)
            ) + 5
            setDrawGridLines(false)
        }
        binding.weeklyProgressChart.axisRight.isEnabled = false

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

