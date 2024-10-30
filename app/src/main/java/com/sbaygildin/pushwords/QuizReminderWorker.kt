package com.sbaygildin.pushwords

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sbaygildin.pushwords.data.di.WordTranslationDao
import com.sbaygildin.pushwords.data.model.WordTranslation
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class QuizReminderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    private val wordTranslationDao: WordTranslationDao,
    @Assisted params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val word = getRandomWordTranslation()
            val message = if (word == null) {
                applicationContext.getString(R.string.notification_add_words)
            } else {
                applicationContext.getString(
                    R.string.notification_word_translation,
                    word.originalWord,
                    word.translatedWord
                )
            }
            sendNotification(message)
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private suspend fun getRandomWordTranslation(): WordTranslation? {
        return try {
            val unlearnedWords = wordTranslationDao.getUnlearnedWords()
            if (!unlearnedWords.isNullOrEmpty()) {
                unlearnedWords.random()
            } else {
                val allWords = wordTranslationDao.getAllWordList()
                if (!allWords.isNullOrEmpty()) allWords.random() else null
            }
        } catch (e: Exception) {
            null
        }
    }


    private fun sendNotification(message: String) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = 1

        val notification = NotificationCompat.Builder(applicationContext, "quiz_channel")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(applicationContext.getString(R.string.word_for_day))
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        notificationManager.notify(notificationId, notification)
    }
}
