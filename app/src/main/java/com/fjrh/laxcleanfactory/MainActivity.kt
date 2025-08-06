package com.fjrh.laxcleanfactory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.fjrh.laxcleanfactory.ui.navigation.AppNavigation
import com.fjrh.laxcleanfactory.ui.theme.LaxCleanTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LaxCleanTheme {
                val navController = rememberNavController()
                AppNavigation(navController = navController)
            }
        }
    }
}
