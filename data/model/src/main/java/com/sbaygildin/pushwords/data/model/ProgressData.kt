package com.sbaygildin.pushwords.data.model


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "progress_data")
data class ProgressData(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val correctAnswers: Int,
    val wrongAnswers: Int,
    val guessedRightAway: Int,
    val learnedWords: Int
)

