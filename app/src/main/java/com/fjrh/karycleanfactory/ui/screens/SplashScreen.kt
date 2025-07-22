package com.fjrh.karycleanfactory.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fjrh.karycleanfactory.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    // Espera 2 segundos y navega al menú principal
    LaunchedEffect(Unit) {
        delay(2000)
        navController.navigate("menu") {
            popUpTo("splash") { inclusive = true } // elimina splash del backstack
        }
    }

    // UI de la splash
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF4F2)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.karyclean_logo_1),
                contentDescription = "Logo KaryClean",
                modifier = Modifier.size(180.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "FACTORY",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF944D2E)
            )
        }
    }
}