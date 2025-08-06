package com.fjrh.laxcleanfactory.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush

val fondoLaxCleanGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF0066CC), // azul principal de Lax Clean
        Color(0xFF4A90E2)  // azul claro
    )
)

// Colores de Lax Clean basados en su sitio web
val LaxCleanBlue = Color(0xFF0066CC)        // Azul principal
val LaxCleanBlueDark = Color(0xFF004499)    // Azul oscuro
val LaxCleanBlueLight = Color(0xFF4A90E2)   // Azul claro
val LaxCleanWhite = Color(0xFFFFFFFF)       // Blanco
val LaxCleanBlack = Color(0xFF000000)       // Negro
val LaxCleanGray = Color(0xFF666666)        // Gris para texto

// Colores Material por defecto (mantenidos para compatibilidad)
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
