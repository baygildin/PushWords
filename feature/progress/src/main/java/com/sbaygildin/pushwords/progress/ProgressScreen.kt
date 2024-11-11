package com.sbaygildin.pushwords.progress

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart


@Composable
fun ProgressScreen(
    viewModel: ProgressViewModel,
    onSwitchChange: (Boolean) -> Unit,
) {
    val context = LocalContext.current
    val isDailyProgress by viewModel.dailyToAllTimeSwitcher.collectAsStateWithLifecycle()
    val pieData by viewModel.pieData.collectAsStateWithLifecycle()
    val barData by viewModel.barData.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isDailyProgress) "Daily Progress" else "All-Time Progress",
                fontSize = 21.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = isDailyProgress,
                onCheckedChange = { onSwitchChange(!isDailyProgress) }
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = "Your Progress", fontSize = 21.sp)
        if (pieData.dataSetCount > 0) {
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                factory = { PieChart(context) },
                update = { pieChart ->
                    pieChart.data = pieData
                    pieChart.description.isEnabled = false
                    pieChart.isRotationEnabled = true
                    pieChart.setEntryLabelTextSize(12f)
                    pieChart.legend.isEnabled = false
                    pieChart.invalidate()
                    pieChart.animateY(1000)
                }
            )
        } else {
            Text(text = "No progress data available", fontSize = 16.sp, color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Weekly Progress", fontSize = 21.sp)
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()

                .height(300.dp),
            factory = { BarChart(context) },
            update = { barChart ->
                barChart.data = barData
                barChart.description.isEnabled = false
                barChart.legend.isEnabled = true
                barChart.invalidate()
            }
        )
    }
}