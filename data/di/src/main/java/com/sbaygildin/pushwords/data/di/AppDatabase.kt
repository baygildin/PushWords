package com.sbaygildin.pushwords.data.di

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sbaygildin.pushwords.data.model.ProgressData
import com.sbaygildin.pushwords.data.model.WordTranslation

@Database(entities = [WordTranslation::class, ProgressData::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)  // Include if you have type converters
abstract class AppDatabase : RoomDatabase() {

    abstract fun wordTranslationDao(): WordTranslationDao
    abstract fun progressDataDao(): ProgressDataDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"  // Use the same database name
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
