package com.sbaygildin.pushwords.data.di

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.sbaygildin.pushwords.data.model.WordTranslation
import kotlinx.coroutines.flow.Flow

@Dao
interface WordTranslationDao {
    @Insert
    suspend fun insert(wordTranslation: WordTranslation): Long

    @Update
    suspend fun update(wordTranslation: WordTranslation)

    @Query("SELECT * FROM word_translations WHERE id = :id")
    suspend fun getWordTranslationById(id: Long): WordTranslation?

    @Query("SELECT * FROM word_translations WHERE isLearned = 0 ORDER BY dateAdded DESC")
    suspend fun getUnlearnedWords(): List<WordTranslation>

    @Query("DELETE FROM word_translations WHERE id = :id")
    suspend fun deleteWordTranslationById(id: Long) : Int

    @Query("SELECT * FROM word_translations ORDER BY dateAdded DESC")
    fun getAllWordList(): List<WordTranslation>

    @Query("SELECT * FROM word_translations ORDER BY dateAdded DESC")
    fun getAllWordTranslations(): Flow<List<WordTranslation>>

    @Insert
    suspend fun insertWordTranslation(wordTranslation: WordTranslation): Long

    @Query("UPDATE word_translations SET isLearned = 1 WHERE id = :id")
    suspend fun updateWordAsLearned(id: Long)
}
