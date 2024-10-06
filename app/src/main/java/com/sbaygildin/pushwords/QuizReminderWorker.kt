package com.sbaygildin.pushwords

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.sbaygildin.pushwords.data.di.WordTranslationDao
import com.sbaygildin.pushwords.data.model.WordTranslation
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


@HiltWorker
class QuizReminderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    private val wordTranslationDao: WordTranslationDao,
    @Assisted params: WorkerParameters
) :  CoroutineWorker(context, params) {
    init {
        Log.d("QuizWorkManager", "Dependencies injected correctly")
    }

    override suspend fun doWork(): Result {
        return try {
            val word = getRandomWordTranslation()
            Log.d("QuizWorkManager", "doWork started")

            val message = if (word == null) {
                "Пора добавить слова в словарь"
            } else {"Слово: ${word.originalWord} — Перевод: ${word.translatedWord}"}
            Log.d("QuizWorkManager", "Sending notification: $message")
                sendNotification(message)



            Result.success()
        } catch (e: Exception) {
            Log.e("QuizWorkManager", "Error in doWork: ${e.message}")
            Result.failure()
        }
    }
    private suspend fun getRandomWordTranslation(): WordTranslation? {
        return try {
            val unlearnedWords = wordTranslationDao.getUnlearnedWords()
            Log.d("QuizWorkManager", "Fetched unlearned words: $unlearnedWords")
            if (!unlearnedWords.isNullOrEmpty()) {
                unlearnedWords.random()
            } else {
                val allWords = wordTranslationDao.getAllWordList()
                Log.d("QuizWorkManager", "Fetched all words: $allWords")
                if (!allWords.isNullOrEmpty()) allWords.random() else null
            }
        } catch (e: Exception) {
            Log.e("QuizWorkManager", "Error fetching words: ${e.message}")
            null
        }
    }


    private fun sendNotification(message: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = 1

        val notification = NotificationCompat.Builder(applicationContext, "quiz_channel")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Слово дня")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(notificationId, notification)
    }
}
