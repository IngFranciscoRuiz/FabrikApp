package com.fjrh.FabrikApp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.fjrh.FabrikApp.ui.navigation.AppNavigation
import com.fjrh.FabrikApp.ui.theme.FabrikAppThemeWithConfig
import com.fjrh.FabrikApp.ui.viewmodel.ThemeViewModel
import com.fjrh.FabrikApp.ui.components.GlobalLayout
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel: ThemeViewModel = hiltViewModel()
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
            
            FabrikAppThemeWithConfig(isDarkTheme = isDarkTheme) {
                val navController = rememberNavController()
                GlobalLayout(navController = navController) {
                    AppNavigation(navController = navController)
                }
            }
        }
    }
}
