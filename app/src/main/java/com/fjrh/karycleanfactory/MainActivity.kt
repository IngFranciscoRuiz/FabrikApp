package com.fjrh.karycleanfactory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.fjrh.karycleanfactory.ui.navigation.AppNavigation
import com.fjrh.karycleanfactory.ui.theme.KaryCleanTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            KaryCleanTheme {
                val navController = rememberNavController()
                AppNavigation(navController = navController)
            }
        }
    }
}
