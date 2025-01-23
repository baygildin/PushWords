package com.sbaygildin.pushwords.progress

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@Composable
fun ProgressScreen(
    viewModel: ProgressViewModel,
) {
    val context = LocalContext.current


    val totalLearnedWords by viewModel.totalLearnedWords.collectAsState()
    val guessedRightAway by viewModel.guessedRightAway.collectAsState()
    val correctAnswer by viewModel.correctAnswer.collectAsState()
    val wrongAnswer by viewModel.wrongAnswer.collectAsState()
    val dailyData by viewModel.dailyData.collectAsState()
    val dailyToAllTimeSwitcher by viewModel.dailyToAllTimeSwitcher.collectAsState()

    val currentTime = System.currentTimeMillis()

    LaunchedEffect(dailyToAllTimeSwitcher) {
        viewModel.fetchProgressData(currentTime)
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 64.dp)
    ) {
        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text(
                text = if (!dailyToAllTimeSwitcher) context.getString(R.string.progress_for_day) else context.getString(
                    R.string.txt_all_time_progress
                ),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 2.dp)
            )

            Switch(
                checked = !dailyToAllTimeSwitcher,
                onCheckedChange = {
                    viewModel.toggleDailyToAllTimeSwitcher()
                },
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }

        AndroidView(
            factory = { ctx ->
                PieChart(ctx).apply {
                    description.isEnabled = false
                    isRotationEnabled = true
                    setEntryLabelTextSize(12f)
                    setUsePercentValues(false)
                    legend.isEnabled = false
                }
            },
            update = { pieChart ->
                val entries = ArrayList<PieEntry>()
                entries.add(
                    PieEntry(
                        totalLearnedWords.toFloat(),
                        context.getString(R.string.learned_words)
                    )
                )
                entries.add(
                    PieEntry(
                        guessedRightAway.toFloat(),
                        context.getString(R.string.guessed_right_away)
                    )
                )
                entries.add(
                    PieEntry(
                        correctAnswer.toFloat(),
                        context.getString(R.string.repeated_answers)
                    )
                )
                entries.add(
                    PieEntry(
                        wrongAnswer.toFloat(),
                        context.getString(R.string.wrong_answers)
                    )
                )

                val dataSet = PieDataSet(entries, context.getString(R.string.your_progress))
                dataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
                dataSet.valueTextSize = 14f

                pieChart.data = PieData(dataSet)
                pieChart.invalidate()
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        Text(
            text = context.getString(R.string.txt_weekly_progress),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
        )

        AndroidView(
            factory = { ctx ->
                BarChart(ctx).apply {
                    description.isEnabled = false
                    setExtraOffsets(0f, 20f, 0f, 0f)
                    legend.isEnabled = true
                    legend.textSize = 12f
                    legend.formToTextSpace = 8f
                    legend.isWordWrapEnabled = true
                    legend.xEntrySpace = 24f
                    legend.yEntrySpace = -24f
                    legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                    setFitBars(true)
                }
            },
            update = { barChart ->
                val totalDays = 7
                val labels = MutableList(totalDays) { "" }
                val dateFormat = SimpleDateFormat("EEE", Locale.getDefault())
                val dateList = mutableListOf<Long>()

                for (i in 6 downTo 0) {
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = currentTime - i * 24 * 60 * 60 * 1000L
                    calendar.set(Calendar.HOUR_OF_DAY, 0)
                    calendar.set(Calendar.MINUTE, 0)
                    calendar.set(Calendar.SECOND, 0)
                    calendar.set(Calendar.MILLISECOND, 0)
                    val dayLabel = dateFormat.format(calendar.time)
                    labels[6 - i] = dayLabel
                    dateList.add(calendar.timeInMillis)
                }

                val dataMap = dailyData.associateBy { data ->
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = data.dateMillis
                    calendar.set(Calendar.HOUR_OF_DAY, 0)
                    calendar.set(Calendar.MINUTE, 0)
                    calendar.set(Calendar.SECOND, 0)
                    calendar.set(Calendar.MILLISECOND, 0)
                    calendar.timeInMillis
                }

                val correctEntries = ArrayList<BarEntry>()
                val wrongEntries = ArrayList<BarEntry>()

                for ((index, dateMillis) in dateList.withIndex()) {
                    val x = index.toFloat()
                    val data = dataMap[dateMillis]
                    val correctValue = data?.avgCorrect ?: 0f
                    val wrongValue = data?.avgWrong ?: 0f
                    correctEntries.add(BarEntry(x, correctValue))
                    wrongEntries.add(BarEntry(x, wrongValue))
                }

                val correctDataSet =
                    BarDataSet(correctEntries, context.getString(R.string.correct_answers)).apply {
                        color = ColorTemplate.COLORFUL_COLORS[1]
                        valueTextSize = 12f
                        setDrawValues(true)
                        valueFormatter = object : ValueFormatter() {
                            override fun getBarLabel(barEntry: BarEntry): String {
                                return if (barEntry.y == 0f) "" else barEntry.y.toInt().toString()
                            }
                        }
                    }

                val wrongDataSet =
                    BarDataSet(wrongEntries, context.getString(R.string.wrong_answers)).apply {
                        color = ColorTemplate.COLORFUL_COLORS[0]
                        valueTextSize = 14f
                        setDrawValues(true)
                        valueFormatter = object : ValueFormatter() {
                            override fun getBarLabel(barEntry: BarEntry): String {
                                return if (barEntry.y == 0f) "" else barEntry.y.toInt().toString()
                            }
                        }
                    }

                val barData = BarData(correctDataSet, wrongDataSet).apply {
                    barWidth = 0.3f
                }

                val groupSpace = 0.4f
                val barSpace = 0f

                barChart.apply {
                    data = barData
                    xAxis.apply {
                        valueFormatter = IndexAxisValueFormatter(labels)
                        granularity = 1f
                        labelCount = labels.size
                        setDrawGridLines(false)
                        position = XAxis.XAxisPosition.BOTTOM
                        setCenterAxisLabels(true)
                        axisMinimum = 0f
                        axisMaximum = labels.size.toFloat()
                        textSize = 12f
                    }
                    axisLeft.apply {
                        axisMinimum = 0f
                        textSize = 14f
                        setDrawGridLines(false)
                        axisMaximum = maxOf(
                            correctEntries.maxOfOrNull { it.y } ?: 0f,
                            wrongEntries.maxOfOrNull { it.y } ?: 0f
                        ) + 5
                    }
                    axisRight.isEnabled = false

                    barData.barWidth = 0.3f
                    groupBars(0f, groupSpace, barSpace)
                    invalidate()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )


    }
}