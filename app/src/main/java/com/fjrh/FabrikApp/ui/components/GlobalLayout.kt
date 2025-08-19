package com.fjrh.FabrikApp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun GlobalLayout(
    navController: NavController,
    content: @Composable () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Rutas donde NO mostrar el bottom navigation
    val routesWithoutBottomNav = listOf(
        "splash",
        "onboarding",
        "login",
        "workspace_gate",
        "subscription",
        "app_selector",
        "mockup_demo"
    )
    
    val showBottomNav = currentRoute !in routesWithoutBottomNav
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Contenido principal (sin restricciones de layout)
        content()
        
        // Bottom Navigation flotante (solo si debe mostrarse)
        if (showBottomNav) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                FabrikBottomNavigation(navController)
            }
        }
    }
}
