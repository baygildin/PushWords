package com.sbaygildin.pushwords.data.model

data class TenMinuteAverage(
    val minute: Int,
    val avgCorrect: Float,
    val avgWrong: Float
)