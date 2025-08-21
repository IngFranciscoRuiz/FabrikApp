package com.fjrh.FabrikApp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.rememberNavController
import com.fjrh.FabrikApp.ui.navigation.AppNavigation
import com.fjrh.FabrikApp.ui.theme.FabrikAppThemeWithConfig
import com.fjrh.FabrikApp.ui.viewmodel.ThemeViewModel
import com.fjrh.FabrikApp.ui.viewmodel.SubscriptionViewModel

import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel: ThemeViewModel = hiltViewModel()
            val subscriptionViewModel: SubscriptionViewModel = hiltViewModel()
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
            
            // Inicializar billing y verificar suscripción al abrir la app
            LaunchedEffect(Unit) {
                subscriptionViewModel.initializeBilling()
            }
            
            FabrikAppThemeWithConfig(isDarkTheme = isDarkTheme) {
                val navController = rememberNavController()
                AppNavigation(navController = navController)
            }
        }
    }
}
