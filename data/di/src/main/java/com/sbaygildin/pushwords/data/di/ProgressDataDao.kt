package com.sbaygildin.pushwords.data.di

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sbaygildin.pushwords.data.model.DailyAverage
import com.sbaygildin.pushwords.data.model.ProgressData


@Dao
interface ProgressDataDao {


    @Query("""
        SELECT (strftime('%s', date(timestamp / 1000, 'unixepoch')) * 1000) AS dateMillis,
               SUM(correctAnswers) AS avgCorrect,
               SUM(wrongAnswers) AS avgWrong
        FROM progress_data
        WHERE timestamp BETWEEN :startTimestamp AND :endTimestamp
        GROUP BY dateMillis
        ORDER BY dateMillis
    """
    )



    suspend fun getDailyAverages(startTimestamp: Long, endTimestamp: Long): List<DailyAverage>

    @Insert
    suspend fun insertProgressData(progressData: ProgressData)


    @Query("""
        SELECT * FROM progress_data
        WHERE timestamp BETWEEN :startTimestamp AND :endTimestamp
        ORDER BY timestamp
    """)
    suspend fun getProgressData(startTimestamp: Long, endTimestamp: Long): List<ProgressData>


}