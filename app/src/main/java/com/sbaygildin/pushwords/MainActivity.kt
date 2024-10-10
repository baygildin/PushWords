package com.sbaygildin.pushwords

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sbaygildin.pushwords.data.di.AppPreferencesManager
import com.sbaygildin.pushwords.data.model.WordTranslation
import com.sbaygildin.pushwords.home.HomeFragmentDirections
import com.sbaygildin.pushwords.navigation.Navigator
import com.sbaygildin.pushwords.settings.NotificationController
import com.sbaygildin.pushwords.wordlist.WordlistFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), Navigator, NotificationController {
    private lateinit var navController: NavController
    @Inject
    lateinit var preferencesManager: AppPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "quiz_channel",
                "Канал для напоминаний о квизах",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
            Log.d("QuizWorkManager", "Notification channel created")
        }

        lifecycleScope.launch {
            areNotificationsEnabled().collect { isEnabled ->
                if (isEnabled) {
                    scheduleQuizNotification()
                } else {
                    cancelQuizNotification()
                }
            }

        }

        lifecycleScope.launch {
            preferencesManager.darkModeFlow.collect {isDarkMode ->
                updateTheme(isDarkMode)
                Log.d("DarkThemeproblem", "preferencesManager.darkModeFlow.collect {isDarkMode ->")
            }
        }


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setupBottomNavMenu(navController)

        findViewById<BottomNavigationView>(R.id.bottom_nav).setOnItemSelectedListener { menuItem ->
            Log.d("MainActivity", "BottomNav Item selected: ${menuItem.title}")
            when (menuItem.itemId) {
                R.id.home_navigation -> {
                    if (navController.currentDestination?.id != R.id.home_navigation) {
                        navController.navigate(R.id.home_navigation)
                    }
                    true
                }

                R.id.settings_navigation -> {
                    if (navController.currentDestination?.id != R.id.home_navigation) {
                        navController.navigate(R.id.settings_navigation)
                    }
                    true
                }

                R.id.wordlist_navigation -> {
                    if (navController.currentDestination?.id != R.id.home_navigation) {
                        navController.navigate(R.id.wordlist_navigation)
                    }
                    true
                }

                R.id.progress_navigation -> {
                    if (navController.currentDestination?.id != R.id.home_navigation) {
                        navController.navigate(R.id.progress_navigation)
                    }
                    true
                }

                else -> false
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun scheduleQuizNotification() {

        val workRequest = PeriodicWorkRequestBuilder<QuizReminderWorker>(15, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "quiz_reminder",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        ).also {
            Log.d("QuizWorkManager", "WorkManager registered from settings")

        }
    }
    private fun areNotificationsEnabled(): Flow<Boolean> {
        val preferencesManager = AppPreferencesManager(applicationContext)
        return preferencesManager.notificationsFlow
    }


    override fun cancelQuizNotification() {
        WorkManager.getInstance(applicationContext).cancelUniqueWork("quiz_reminder")
        Log.d("QuizWorkManager", "WorkManager cancelled")
    }
    override fun updateTheme(isDarkMode: Boolean) {
        val currentMode = AppCompatDelegate.getDefaultNightMode()
        if (currentMode != (if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)) {
            AppCompatDelegate.setDefaultNightMode(if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
        }

//        if (isDarkMode) {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//            Log.d("ThemeMode", "Current mode after setting: ${AppCompatDelegate.getDefaultNightMode()}")
//        } else {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//            Log.d("ThemeMode", "Current mode after setting: ${AppCompatDelegate.getDefaultNightMode()}")
//        }
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_UNSPECIFIED)
        Log.d("ThemeMode", "Current mode after setting: ${AppCompatDelegate.getDefaultNightMode()}")
    }

    private fun setupBottomNavMenu(navController: NavController) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav?.setupWithNavController(navController)
    }

    override fun navigateHomeFragmentToSettingsFragment(s: String) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav.selectedItemId = R.id.settings_navigation
        val action = HomeFragmentDirections.actionHomeFragmenttoSettingsFragment(s)
        navController.navigate(action)
    }

    override fun navigateWordlistToEdit(id: String) {
        val action = WordlistFragmentDirections.navigateWordlistToEdit(id)
        navController.navigate(action)
    }

    override fun navigateWordlistToAddword() {
        val action = WordlistFragmentDirections.navigateWordlistToAddword()
        navController.navigate(action)
    }

    override fun navigateHomeToAddword() {
        val action = HomeFragmentDirections.navigateHomeToAddword()
        navController.navigate(action)
    }
}
