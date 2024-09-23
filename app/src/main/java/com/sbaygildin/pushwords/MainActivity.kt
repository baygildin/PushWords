package com.sbaygildin.pushwords

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

import com.sbaygildin.pushwords.home.HomeFragmentDirections
import com.sbaygildin.pushwords.navigation.Navigator

class MainActivity : AppCompatActivity(), Navigator {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        findViewById<BottomNavigationView>(R.id.bottom_nav)
            .setupWithNavController(navController)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun navigateHomeToSettingsArgs(x: String) {
        val action = HomeFragmentDirections.navigateHomeToSettingsArgs(x)
        findNavController(R.id.nav_host_fragment).navigate(action)
    }
    override fun navigateHomeToSettingsWoArgs() {
        val action = HomeFragmentDirections.navigateHomeToSettingsWoArgs()
        findNavController(R.id.nav_host_fragment).navigate(action)
    }

}