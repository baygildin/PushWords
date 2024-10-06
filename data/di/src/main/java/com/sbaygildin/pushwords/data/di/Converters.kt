package com.sbaygildin.pushwords.data.di

import androidx.room.TypeConverter
import com.sbaygildin.pushwords.data.model.DifficultyLevel
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromDifficultyLevel(value: DifficultyLevel?): String? {
        return value?.name
    }

    @TypeConverter
    fun toDifficultyLevel(value: String?): DifficultyLevel? {
        return value?.let {DifficultyLevel.valueOf(it)}
    }

}
