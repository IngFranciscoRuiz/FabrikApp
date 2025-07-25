package com.fjrh.karycleanfactory.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fjrh.karycleanfactory.R
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color


@Composable
fun MainMenuScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF000000), // Azul más brillante
                        Color(0xFF0D1A2F)  // Azul profundo
                    )
                )
            )
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Logo centrado
        Image(
            painter = painterResource(id = R.drawable.karyclean_logo), // Asegúrate que exista
            contentDescription = "Logo KaryClean",
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Botones con íconos y texto
        MenuButton("Fórmulas", Icons.Default.Science) {
            navController.navigate("formulas")
        }

        MenuButton("Nueva fórmula", Icons.Default.Add) {
            navController.navigate("nueva_formula")
        }


        MenuButton("Inventario", Icons.Default.Inventory) {
            navController.navigate("inventario")
        }

        MenuButton("Stock productos terminados", Icons.Default.Inventory) {
            navController.navigate("stock_productos")
        }

        MenuButton("Producción", Icons.Default.Factory) {
            navController.navigate("produccion")
        }

        MenuButton("Historial", Icons.Default.History) {
            navController.navigate("historial")
        }
    }
}

@Composable
fun MenuButton(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(60.dp),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, fontSize = 18.sp)
    }
}
