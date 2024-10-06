package com.sbaygildin.pushwords.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import kotlinx.parcelize.Parcelize

@Entity(tableName = "word_translations")
data class WordTranslation(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val originalWord: String,
    val translatedWord: String,
    val isLearned: Boolean = false,
    val originalLanguage: String,
    val translationLanguage: String,
    val dateAdded: Date,
    val difficultyLevel: DifficultyLevel = DifficultyLevel.MEDIUM
)
