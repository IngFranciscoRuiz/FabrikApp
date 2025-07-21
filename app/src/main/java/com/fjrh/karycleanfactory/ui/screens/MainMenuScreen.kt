package com.fjrh.karycleanfactory.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun MainMenuScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF4F2))
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "KARYCLEAN",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF944D2E)
        )

        Spacer(modifier = Modifier.height(40.dp))

        val botones = listOf(
            "Fórmulas" to "formulas",
            "Inventario" to "inventario",
            "Producción" to "produccion",
            "Historial" to "historial"
        )

        botones.forEach { (texto, ruta) ->
            Button(
                onClick = { navController.navigate(ruta) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF944D2E),
                    contentColor = Color.White
                )
            ) {
                Text(texto, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
