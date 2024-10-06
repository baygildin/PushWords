package com.sbaygildin.pushwords.data.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "app_database"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideProgressDataDao(database: AppDatabase): ProgressDataDao {
        return database.progressDataDao()
    }

    @Provides
    @Singleton
    fun provideWordTranslationDao(database: AppDatabase): WordTranslationDao {
        return database.wordTranslationDao()
    }

    @Provides
    @Singleton
    fun provideProgressRepository(progressDataDao: ProgressDataDao): ProgressRepository {
        return ProgressRepository(progressDataDao)
    }
    @Provides
    @Singleton
    fun provideAppPreferencesManager(@ApplicationContext context: Context): AppPreferencesManager {
        return AppPreferencesManager(context)
    }
}
