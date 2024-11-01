package com.sbaygildin.pushwords

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sbaygildin.pushwords.data.di.AppPreferencesManager
import com.sbaygildin.pushwords.home.HomeFragmentDirections
import com.sbaygildin.pushwords.navigation.Navigator
import com.sbaygildin.pushwords.settings.NotificationController
import com.sbaygildin.pushwords.wordlist.WordlistFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), Navigator, NotificationController {
    private lateinit var navController: NavController

    @Inject
    lateinit var preferencesManager: AppPreferencesManager
    private val REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "quiz_channel",
                getString(R.string.quiz_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
//        lifecycleScope.launch {
//            areNotificationsEnabled().collect { isEnabled ->
//                if (isEnabled) {
//                    scheduleQuizNotification()
//                } else {
//                    cancelQuizNotification()
//                }
//            }
//
//        }

        lifecycleScope.launch {
            areNotificationsEnabled().collect { isEnabled ->
                if (isEnabled) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (ContextCompat.checkSelfPermission(
                                this@MainActivity,
                                android.Manifest.permission.POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            scheduleQuizNotification()
                        } else {
                            requestPermissions(
                                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                                REQUEST_CODE
                            )
                        }
                    } else {

                        scheduleQuizNotification()
                        Log.d("Notification456", "Quiz notification scheduled in MainActivity")

                    }
                } else {
                    cancelQuizNotification()
                    Log.d("Notification456", "Quiz notification cancelled in MainActivity")
                }
            }

        }

        lifecycleScope.launch {
            preferencesManager.darkModeFlow.collect { isDarkMode ->
                updateTheme(isDarkMode)
            }
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setupBottomNavMenu(navController)

        findViewById<BottomNavigationView>(R.id.bottom_nav).setOnItemSelectedListener { menuItem ->
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
        lifecycleScope.launch {
            Log.d("Notification456", "Scheduling quiz notification")
            val interval = preferencesManager.notificationIntervalFlow.first()
            val isQuietModeEnabled = preferencesManager.isQuietModeEnabledFlow.first()
            val currentTime = Calendar.getInstance()
            Log.d("Notification456", "Interval: $interval, Quiet Mode: $isQuietModeEnabled")

            val quietStart = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 21)
                set(Calendar.MINUTE, 0)
            }
            val quietEnd = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 9)
                set(Calendar.MINUTE, 0)
            }
            Log.d("Notification456", "Current time: ${currentTime.time}")
            Log.d("Notification456", "Quiet start: ${quietStart.time}, Quiet end: ${quietEnd.time}")

            if (!(isQuietModeEnabled && currentTime.after(quietStart) || currentTime.before(quietEnd))) {
//            if (!(isQuietModeEnabled && (currentTime.after(quietStart) && currentTime.before(quietEnd)))) {
                val workRequest =
                    PeriodicWorkRequestBuilder<QuizReminderWorker>(interval, TimeUnit.MILLISECONDS)
                        .build()
                WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
                    "quiz_reminder",
                    ExistingPeriodicWorkPolicy.REPLACE,
                    workRequest
                )
                Log.d("Notification456", "Quiz notification scheduled")
            } else {
                Log.d("Notification456", "Quiet mode is enabled, not scheduling notification")
            }
        }
    }

    private fun areNotificationsEnabled(): Flow<Boolean> {
        val preferencesManager = AppPreferencesManager(applicationContext)
        return preferencesManager.notificationsFlow
    }


    override fun cancelQuizNotification() {
        WorkManager.getInstance(applicationContext).cancelUniqueWork("quiz_reminder")
        Log.d("Notification456", "Cancelling quiz notification")
    }

    override fun updateTheme(isDarkMode: Boolean) {
        val currentMode = AppCompatDelegate.getDefaultNightMode()
        if (currentMode != (if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)) {
            AppCompatDelegate.setDefaultNightMode(if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
        }
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
