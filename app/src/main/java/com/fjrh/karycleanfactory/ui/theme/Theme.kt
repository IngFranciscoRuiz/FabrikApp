package com.fjrh.karycleanfactory.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.Typography


private val LightColorScheme = lightColorScheme(
    primary = AzulKary,
    secondary = AzulBoton,
    background = BlancoSuave,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = GrisTexto,
    onSurface = GrisTexto
)

private val DarkColorScheme = darkColorScheme(
    primary = AzulOscuro,
    secondary = AzulBoton,
    background = Color.Black,
    surface = Color.DarkGray,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun KaryCleanTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (useDarkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(), // Usa la default de Material3
        content = content
    )
}
