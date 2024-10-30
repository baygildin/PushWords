package com.sbaygildin.pushwords

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            installSplashScreen()
            Log.e("SplashActivitdsy", "успешно запущена установка")
        } catch (e: Exception) {
            Log.e("SplashActivitdsy", "Ошибка отображения сплэш-экрана", e)
        }
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()

    }
}