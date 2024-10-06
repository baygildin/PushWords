package com.sbaygildin.pushwords.data.di

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sbaygildin.pushwords.data.model.DailyAverage
import com.sbaygildin.pushwords.data.model.HourlyAverage
import com.sbaygildin.pushwords.data.model.ProgressData


@Dao
interface ProgressDataDao {

    // Агрегация по часам для правильных и неправильных ответов
    @Query("""
        SELECT strftime('%H', datetime(timestamp / 1000, 'unixepoch')) AS hour, 
               SUM(correctAnswers) AS avgCorrect, 
               SUM(wrongAnswers) AS avgWrong 
        FROM progress_data 
        WHERE timestamp BETWEEN :startTimestamp AND :endTimestamp
        GROUP BY hour 
        ORDER BY hour
    """)
    suspend fun getHourlyAverages(startTimestamp: Long, endTimestamp: Long): List<HourlyAverage>

    // Агрегация по дням для правильных и неправильных ответов за последние 7 дней
    @Query("""
        SELECT strftime('%d', datetime(timestamp / 1000, 'unixepoch')) AS day, 
               SUM(correctAnswers) AS avgCorrect, 
               SUM(wrongAnswers) AS avgWrong 
        FROM progress_data 
        WHERE timestamp BETWEEN :startTimestamp AND :endTimestamp
        GROUP BY day 
        ORDER BY day
    """)
    suspend fun getDailyAverages(startTimestamp: Long, endTimestamp: Long): List<DailyAverage>

    @Insert
    suspend fun insertProgressData(progressData: ProgressData)
}