package com.fjrh.FabrikApp.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.expandVertically
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fjrh.FabrikApp.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    var logoScale by remember { mutableStateOf(0f) }
    var textVisible by remember { mutableStateOf(false) }
    var currentText by remember { mutableStateOf("") }
    var spinnerVisible by remember { mutableStateOf(false) }
    
    val fullText = "FABRIKAPP FACTORY"
    val typingDelay = 150L
    
    // Animación del logo
    val logoAnimation by animateFloatAsState(
        targetValue = logoScale,
        animationSpec = tween(
            durationMillis = 1000,
            easing = EaseOutBack
        ),
        label = "logo_scale"
    )
    
    // Animación del spinner
    val spinnerRotation by rememberInfiniteTransition(label = "spinner").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spinner_rotation"
    )

    LaunchedEffect(Unit) {
        // Secuencia de animaciones
        delay(500)
        logoScale = 1f
        
        delay(800)
        textVisible = true
        
        // Efecto typing
        for (i in fullText.indices) {
            currentText = fullText.substring(0, i + 1)
            delay(typingDelay)
        }
        
        delay(500)
        spinnerVisible = true
        
        // Esperar un poco más antes de navegar
        delay(2000)
        
        // Navegar al menú principal
        navController.navigate("menu") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1E3A8A), // Azul industrial oscuro
                        Color(0xFF3B82F6), // Azul industrial medio
                        Color(0xFF60A5FA)  // Azul industrial claro
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo animado
            Image(
                painter = painterResource(id = R.drawable.fabrikapp_logo),
                contentDescription = "Logo FabrikApp",
                modifier = Modifier
                    .size(200.dp)
                    .scale(logoAnimation)
                    .clip(CircleShape)
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Texto con efecto typing
            AnimatedVisibility(
                visible = textVisible,
                enter = fadeIn(animationSpec = tween(500)) + expandVertically()
            ) {
                Text(
                    text = currentText,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontSize = 28.sp,
                    letterSpacing = 2.sp
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Subtítulo
            AnimatedVisibility(
                visible = textVisible,
                enter = fadeIn(animationSpec = tween(500, delayMillis = 1000))
            ) {
                Text(
                    text = "Sistema de Gestión Industrial",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp
                )
            }
            
            Spacer(modifier = Modifier.height(60.dp))
            
            // Loading spinner
            AnimatedVisibility(
                visible = spinnerVisible,
                enter = fadeIn(animationSpec = tween(500)) + expandVertically()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(40.dp)
                        .rotate(spinnerRotation),
                    color = Color.White,
                    strokeWidth = 3.dp
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Texto de carga
            AnimatedVisibility(
                visible = spinnerVisible,
                enter = fadeIn(animationSpec = tween(500, delayMillis = 200))
            ) {
                Text(
                    text = "Iniciando sistema...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            }
        }
        
        // Versión en la esquina inferior
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
        ) {
            Text(
                text = "v1.0.0",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp
            )
        }
    }
}
