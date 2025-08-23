package com.fjrh.FabrikApp.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fjrh.FabrikApp.R
import com.fjrh.FabrikApp.ui.theme.FabrikAppBlue
import com.fjrh.FabrikApp.ui.theme.FabrikAppBlueDark
import com.fjrh.FabrikApp.ui.viewmodel.OnboardingViewModel
import com.fjrh.FabrikApp.ui.navigation.Routes
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import com.google.firebase.auth.FirebaseAuth
import com.fjrh.FabrikApp.ui.viewmodel.SubscriptionViewModel

@Composable
fun SplashScreen(
    navController: NavController
) {
    val onboardingViewModel: OnboardingViewModel = hiltViewModel()
    val subscriptionViewModel: SubscriptionViewModel = hiltViewModel()
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 2000)
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(3000L)
        
        // Inicializar billing para verificar suscripción
        subscriptionViewModel.initializeBilling()
        
        // Esperar a que billing se conecte y verifique el estado real
        delay(1000L)
        
        // Verificar si ya se mostró el onboarding
        val hasSeenOnboarding = onboardingViewModel.hasSeenOnboarding.first()
        
        if (hasSeenOnboarding) {
            // Verificar estado real de Google Play
            val isPremiumActive = subscriptionViewModel.getBillingService().isPremiumActive.first()
            
            if (isPremiumActive) {
                // Si Google Play confirma que es premium, ir directo al menú principal
                navController.navigate("menu") {
                    popUpTo(Routes.Splash) { inclusive = true }
                }
            } else {
                // Si no es premium en Google Play, ir al paywall
                navController.navigate("paywall") {
                    popUpTo(Routes.Splash) { inclusive = true }
                }
            }
        } else {
            navController.navigate("onboarding") {
                popUpTo(Routes.Splash) { inclusive = true }
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
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Card(
                modifier = Modifier
                    .size(120.dp)
                    .alpha(alphaAnim.value),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f)),
                shape = MaterialTheme.shapes.medium
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.fabrikapp_logo),
                        contentDescription = "FabrikApp Logo",
                        modifier = Modifier.size(80.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App Name
            Text(
                text = "FABRIKAPP",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(alphaAnim.value)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = "Sistema de Gestión Industrial",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(alphaAnim.value)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Loading indicator
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier
                    .size(32.dp)
                    .alpha(alphaAnim.value)
            )
        }
    }
}
