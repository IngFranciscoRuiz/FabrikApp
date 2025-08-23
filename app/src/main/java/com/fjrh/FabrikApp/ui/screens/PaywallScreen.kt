package com.fjrh.FabrikApp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.fjrh.FabrikApp.ui.viewmodel.PaywallViewModel
import android.content.ContextWrapper

@Composable
fun PaywallScreen(
    navController: NavController,
    viewModel: PaywallViewModel = hiltViewModel()
) {
    // Obtener estado de billing para mostrar texto correcto
    val billingService = viewModel.getBillingService()
    val monthlyOfferToken by billingService.monthlyOfferToken.collectAsState()
    val yearlyOfferToken by billingService.yearlyOfferToken.collectAsState()
    val monthlyStandardProduct by billingService.monthlyStandardProduct.collectAsState()
    val yearlyStandardProduct by billingService.yearlyStandardProduct.collectAsState()
    
    // Determinar si hay ofertas de prueba gratuita disponibles
    val hasTrialOffers = monthlyOfferToken != null || yearlyOfferToken != null
    val hasStandardProducts = monthlyStandardProduct != null || yearlyStandardProduct != null
    
    val trialText = when {
        hasTrialOffers -> "7 días de prueba gratuita • Cancela cuando quieras"
        hasStandardProducts -> "Suscripción directa • Cancela cuando quieras"
        else -> "Productos no disponibles"
    }
    val context = LocalContext.current
    
    // Obtener activity de forma más confiable usando el contexto del NavController
    val activity = remember {
        try {
            val navContext = navController.context
            when {
                navContext is FragmentActivity -> navContext
                navContext is ContextWrapper -> {
                    var current = navContext.baseContext
                    while (current is ContextWrapper) {
                        if (current is FragmentActivity) {
                            current
                            break
                        }
                        current = current.baseContext
                    }
                    null
                }
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    } as? FragmentActivity
    
    println("PaywallScreen: Activity found: ${activity != null}")
    
    val uiState by viewModel.uiState.collectAsState()
    
    // Estado local para mostrar error cerca del botón restore
    var showRestoreError by remember { mutableStateOf<String?>(null) }
    
    // Limpiar estado cuando se sale de la pantalla
    LaunchedEffect(Unit) {
        viewModel.clearState()
        // Inicializar billing si no está conectado
        viewModel.initializeBilling()
    }
    
    // Manejar estados de éxito y error
    LaunchedEffect(uiState) {
        when (uiState) {
            is PaywallViewModel.PaywallUiState.Success -> {
                showRestoreError = null
                navController.navigate("menu") {
                    popUpTo("paywall") { inclusive = true }
                }
            }
            is PaywallViewModel.PaywallUiState.Error -> {
                showRestoreError = (uiState as PaywallViewModel.PaywallUiState.Error).message
            }
            else -> {
                showRestoreError = null
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Icono principal
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(40.dp)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Título
                Text(
                    text = "Desbloquea FabrikApp Premium",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF1A1A1A)
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                // Información de prueba gratuita
                Text(
                    text = trialText,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Medium
                )
            }
            
            // Botón YA ERES PREMIUM (ultra compacto)
            item {
                Spacer(modifier = Modifier.height(8.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE8F5E8)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "¿Ya tienes una suscripción?",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32),
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(6.dp))
                        
                        Button(
                            onClick = {
                                viewModel.restoreSubscription()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50)
                            ),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Restore,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "YA ERES PREMIUM",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
            
            // Mensaje de error para restore (cerca del botón)
            showRestoreError?.let { errorMessage ->
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = Color(0xFFF44336),
                                modifier = Modifier.size(16.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Text(
                                text = errorMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFC62828)
                            )
                        }
                    }
                }
            }
            
            // Tarjeta de suscripción mensual
            item {
                Spacer(modifier = Modifier.height(8.dp))
                
                PaywallSubscriptionCard(
                    title = "Plan Mensual",
                    price = "$79 MXN",
                    period = "por mes",
                    trialText = when {
                        hasTrialOffers -> "7 días de prueba gratuita"
                        hasStandardProducts -> "Suscripción directa"
                        else -> "No disponible"
                    },
                    features = listOf(
                        "✓ Acceso completo a todas las funciones",
                        "✓ Exportación e importación de datos",
                        "✓ Soporte prioritario",
                        "✓ Actualizaciones gratuitas"
                    ),
                    isPopular = false,
                    onClick = {
                        if (hasTrialOffers || hasStandardProducts) {
                            println("PaywallScreen: Monthly card clicked!")
                            activity?.let { act ->
                                println("PaywallScreen: Activity found, calling purchaseSubscription(true)")
                                viewModel.purchaseSubscription(act, true)
                            } ?: run {
                                println("PaywallScreen: Activity is null!")
                            }
                        }
                    },
                    isEnabled = hasTrialOffers || hasStandardProducts
                )
            }
            
            // Tarjeta de suscripción anual
            item {
                PaywallSubscriptionCard(
                    title = "Plan Anual",
                    price = "$749 MXN",
                    period = "por año",
                    trialText = when {
                        hasTrialOffers -> "7 días de prueba gratuita"
                        hasStandardProducts -> "Suscripción directa"
                        else -> "No disponible"
                    },
                    features = listOf(
                        "✓ Todo del plan mensual",
                        "✓ 2 meses gratis",
                        "✓ Ahorro del 21%",
                        "✓ Acceso anticipado a nuevas funciones"
                    ),
                    isPopular = true,
                    onClick = {
                        if (hasTrialOffers || hasStandardProducts) {
                            println("PaywallScreen: Yearly card clicked!")
                            activity?.let { act ->
                                println("PaywallScreen: Yearly card clicked!")
                                viewModel.purchaseSubscription(act, false)
                            } ?: run {
                                println("PaywallScreen: Activity is null!")
                            }
                        }
                    },
                    isEnabled = hasTrialOffers || hasStandardProducts
                )
            }
            

            

            
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
            
            // Estado de loading
            when (uiState) {
                is PaywallViewModel.PaywallUiState.Loading -> {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color(0xFF4CAF50),
                                    strokeWidth = 2.dp
                                )
                                
                                Spacer(modifier = Modifier.width(8.dp))
                                
                                Text(
                                    text = "Procesando...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun PaywallSubscriptionCard(
    title: String,
    price: String,
    period: String,
    trialText: String,
    features: List<String>,
    isPopular: Boolean,
    onClick: () -> Unit,
    isEnabled: Boolean = true
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isPopular) Color(0xFFE3F2FD) else Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Badge popular en la parte superior
            if (isPopular) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1976D2)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "MÁS POPULAR",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(6.dp))
            }
            
            // Header sin badge
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                    
                    Text(
                        text = trialText,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Spacer(modifier = Modifier.height(2.dp))
                
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "Después $price",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1976D2)
                    )
                    Text(
                        text = " $period",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF666666)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(6.dp))
            
            // Features
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                features.forEach { feature ->
                    Text(
                        text = feature,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF666666)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Botón de suscripción
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPopular) Color(0xFF1976D2) else Color(0xFF4CAF50)
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = isEnabled
            ) {
                Text(
                    text = if (trialText.contains("prueba gratuita")) "Comenzar Prueba Gratuita" else "Suscribirse Ahora",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
