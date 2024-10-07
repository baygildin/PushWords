package com.sbaygildin.pushwords.data.di

import com.sbaygildin.pushwords.data.model.DailyAverage
import com.sbaygildin.pushwords.data.model.ProgressData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProgressRepository @Inject constructor(private val progressDataDao: ProgressDataDao) {

    suspend fun insertProgressData(progressData: ProgressData) = withContext(Dispatchers.IO) {
        progressDataDao.insertProgressData(progressData)
    }

    suspend fun getDailyAverages(startTimestamp: Long, endTimestamp: Long): List<DailyAverage> = withContext(Dispatchers.IO) {
        progressDataDao.getDailyAverages(startTimestamp, endTimestamp)
    }
}

