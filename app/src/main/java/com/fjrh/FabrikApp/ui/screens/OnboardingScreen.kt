package com.fjrh.FabrikApp.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fjrh.FabrikApp.ui.theme.FabrikAppBlue
import com.fjrh.FabrikApp.ui.theme.FabrikAppBlueDark
import com.fjrh.FabrikApp.ui.viewmodel.OnboardingViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnboardingScreen(
    navController: NavController
) {
    val onboardingViewModel: OnboardingViewModel = hiltViewModel()
    var currentPage by remember { mutableStateOf(0) }
    val totalPages = 4
    val scope = rememberCoroutineScope()
    
    // Animación de entrada
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(300)
        isVisible = true
    }

    fun navigateToMenu() {
        scope.launch {
            onboardingViewModel.markOnboardingAsSeen()
            navController.navigate("paywall") {
                popUpTo("onboarding") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        FabrikAppBlue,
                        FabrikAppBlueDark
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header con skip button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo placeholder
                Card(
                    modifier = Modifier.size(40.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "F",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                TextButton(
                    onClick = { navigateToMenu() },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.White.copy(alpha = 0.8f)
                    )
                ) {
                    Text("Saltar")
                }
            }

            // Content area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = currentPage,
                    transitionSpec = {
                        slideInHorizontally(
                            initialOffsetX = { if (targetState > initialState) it else -it },
                            animationSpec = tween(500, easing = EaseOutCubic)
                        ) + fadeIn(animationSpec = tween(500)) with
                        slideOutHorizontally(
                            targetOffsetX = { if (targetState > initialState) -it else it },
                            animationSpec = tween(500, easing = EaseInCubic)
                        ) + fadeOut(animationSpec = tween(500))
                    }
                ) { page ->
                    OnboardingPage(page = page)
                }
            }

            // Bottom section with indicators and buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Page indicators
                Row(
                    modifier = Modifier.padding(bottom = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(totalPages) { index ->
                        PageIndicator(
                            isActive = index == currentPage,
                            isCompleted = index < currentPage
                        )
                    }
                }

                // Navigation buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button
                    if (currentPage > 0) {
                        OutlinedButton(
                            onClick = { currentPage-- },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.White
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.3f),
                                        Color.White.copy(alpha = 0.3f)
                                    )
                                )
                            )
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Anterior")
                        }
                    } else {
                        Spacer(modifier = Modifier.width(100.dp))
                    }

                    // Next/Start button
                    Button(
                        onClick = {
                            if (currentPage < totalPages - 1) {
                                currentPage++
                            } else {
                                navigateToMenu()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = FabrikAppBlue
                        ),
                        modifier = Modifier.height(48.dp)
                    ) {
                        if (currentPage < totalPages - 1) {
                            Text("Siguiente")
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null)
                        } else {
                            Text("¡Comenzar!")
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingPage(page: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Animated icon
        var iconScale by remember { mutableStateOf(0f) }
        LaunchedEffect(page) {
            iconScale = 0f
            delay(200)
            iconScale = 1f
        }

        Card(
            modifier = Modifier
                .size(120.dp)
                .animateContentSize(),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (page) {
                        0 -> Icons.Default.Rocket
                        1 -> Icons.Default.Inventory
                        2 -> Icons.Default.Science
                        3 -> Icons.Default.Factory
                        else -> Icons.Default.CheckCircle
                    },
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Title
        Text(
            text = when (page) {
                0 -> "¡Bienvenido a FabrikApp!"
                1 -> "Gestión de Inventario"
                2 -> "Fórmulas de Producción"
                3 -> "Control Total"
                else -> ""
            },
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Text(
            text = when (page) {
                0 -> "Tu sistema completo de gestión industrial. Optimiza procesos, controla costos y maximiza la productividad de tu negocio."
                1 -> "Gestiona insumos, controla stock en tiempo real y mantén un inventario preciso con alertas automáticas."
                2 -> "Crea fórmulas de producción, calcula costos automáticamente y optimiza tus recetas para máxima eficiencia."
                3 -> "Registra producción, controla ventas y mantén el balance financiero de tu empresa en una sola aplicación."
                else -> ""
            },
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.9f),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}

@Composable
fun PageIndicator(
    isActive: Boolean,
    isCompleted: Boolean
) {
    val animatedProgress by animateFloatAsState(
        targetValue = if (isActive || isCompleted) 1f else 0f,
        animationSpec = tween(300)
    )

    Card(
        modifier = Modifier.size(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) Color.White else Color.White.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(6.dp)
    ) {
        // Empty for now, could add animation here
    }
}
