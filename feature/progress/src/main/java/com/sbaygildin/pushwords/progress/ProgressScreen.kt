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
                val correctEntries = ArrayList<BarEntry>()
                val wrongEntries = ArrayList<BarEntry>()
                val correctAnswersPerDay = mutableMapOf<String, Float>()
                val wrongAnswersPerDay = mutableMapOf<String, Float>()
                val labels = listOf(
                    context.getString(R.string.Su),
                    context.getString(R.string.Mo),
                    context.getString(R.string.Tu),
                    context.getString(R.string.We),
                    context.getString(R.string.Th),
                    context.getString(R.string.Fr),
                    context.getString(R.string.Sa)
                )

                labels.forEach {
                    correctAnswersPerDay[it] = 0f
                    wrongAnswersPerDay[it] = 0f
                }

                dailyData.forEach { data ->
                    val dayOfWeek = when (data.day.toInt()) {
                        0 -> context.getString(R.string.Su)
                        1 -> context.getString(R.string.Mo)
                        2 -> context.getString(R.string.Tu)
                        3 -> context.getString(R.string.We)
                        4 -> context.getString(R.string.Th)
                        5 -> context.getString(R.string.Fr)
                        6 -> context.getString(R.string.Sa)
                        else -> ""
                    }
                    correctAnswersPerDay[dayOfWeek] =
                        correctAnswersPerDay.getOrDefault(dayOfWeek, 0f) + data.avgCorrect
                    wrongAnswersPerDay[dayOfWeek] =
                        wrongAnswersPerDay.getOrDefault(dayOfWeek, 0f) + data.avgWrong
                }

                labels.forEachIndexed { index, day ->
                    correctEntries.add(BarEntry(index.toFloat(), correctAnswersPerDay[day] ?: 0f))
                    wrongEntries.add(BarEntry(index.toFloat(), wrongAnswersPerDay[day] ?: 0f))
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
                    barWidth = 0.35f
                }

                barChart.apply {
                    data = barData
                    groupBars(0f, 0.18f, 0.03f)
                    setVisibleXRangeMaximum(7f)
                    moveViewToX(0f)
                    xAxis.apply {
                        valueFormatter = IndexAxisValueFormatter(labels)
                        textSize = 12f
                        granularity = 1f
                        labelCount = labels.size
                        setDrawGridLines(false)
                        position = XAxis.XAxisPosition.BOTTOM
                        setCenterAxisLabels(true)
                    }
                    axisLeft.apply {
                        axisMinimum = 0f
                        textSize = 14f
                        axisMaximum = maxOf(
                            correctAnswersPerDay.values.maxOrNull() ?: 0f,
                            wrongAnswersPerDay.values.maxOrNull() ?: 0f
                        ) + 5
                        setDrawGridLines(false)
                    }
                    axisRight.isEnabled = false
                    invalidate()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}