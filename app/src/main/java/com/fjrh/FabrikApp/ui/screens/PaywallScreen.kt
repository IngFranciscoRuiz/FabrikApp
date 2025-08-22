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
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.fjrh.FabrikApp.ui.viewmodel.PaywallViewModel

@Composable
fun PaywallScreen(
    navController: NavController,
    viewModel: PaywallViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val uiState by viewModel.uiState.collectAsState()
    
    // Limpiar estado cuando se sale de la pantalla
    LaunchedEffect(Unit) {
        viewModel.clearState()
        // Inicializar billing si no está conectado
        viewModel.initializeBilling()
    }
    
    // Manejar estados de éxito
    LaunchedEffect(uiState) {
        when (uiState) {
            is PaywallViewModel.PaywallUiState.Success -> {
                navController.navigate("menu") {
                    popUpTo("paywall") { inclusive = true }
                }
            }
            else -> {}
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
                Spacer(modifier = Modifier.height(40.dp))
                
                // Icono principal
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(60.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Título
                Text(
                    text = "Desbloquea FabrikApp Premium",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF1A1A1A)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Descripción
                Text(
                    text = "Accede a todas las funcionalidades avanzadas",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF666666)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Información de prueba gratuita
                Text(
                    text = "7 días de prueba gratuita • Cancela cuando quieras",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Medium
                )
            }
            
            // Tarjeta de suscripción mensual
            item {
                PaywallSubscriptionCard(
                    title = "Plan Mensual",
                    price = "$79 MXN",
                    period = "por mes",
                    trialText = "7 días de prueba gratuita",
                    features = listOf(
                        "✓ Acceso completo a todas las funciones",
                        "✓ Backup automático",
                        "✓ Soporte prioritario",
                        "✓ Actualizaciones gratuitas"
                    ),
                    isPopular = false,
                    onClick = {
                        activity?.let { act ->
                            viewModel.purchaseSubscription(act, true)
                        }
                    }
                )
            }
            
            // Tarjeta de suscripción anual
            item {
                PaywallSubscriptionCard(
                    title = "Plan Anual",
                    price = "$749 MXN",
                    period = "por año",
                    trialText = "7 días de prueba gratuita",
                    features = listOf(
                        "✓ Todo del plan mensual",
                        "✓ 2 meses gratis",
                        "✓ Ahorro del 21%",
                        "✓ Acceso anticipado a nuevas funciones"
                    ),
                    isPopular = true,
                    onClick = {
                        activity?.let { act ->
                            viewModel.purchaseSubscription(act, false)
                        }
                    }
                )
            }
            
            // Botón YA ERES PREMIUM
            item {
                Spacer(modifier = Modifier.height(4.dp))
                
                OutlinedButton(
                    onClick = {
                        viewModel.restoreSubscription()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Restore,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("YA ERES PREMIUM")
                }
            }
            
            // Botón SIMULAR PREMIUM (para testing)
            item {
                Button(
                    onClick = {
                        viewModel.simulatePremium()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF9C27B0)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "SIMULAR PREMIUM",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            

            
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
            
            // Estados de loading y error
            when (uiState) {
                is PaywallViewModel.PaywallUiState.Loading -> {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color(0xFF4CAF50),
                                    strokeWidth = 2.dp
                                )
                                
                                Spacer(modifier = Modifier.width(12.dp))
                                
                                Text(
                                    text = "Procesando...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }
                    }
                }
                
                is PaywallViewModel.PaywallUiState.Error -> {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = null,
                                    tint = Color(0xFFF44336),
                                    modifier = Modifier.size(20.dp)
                                )
                                
                                Spacer(modifier = Modifier.width(12.dp))
                                
                                Text(
                                    text = (uiState as PaywallViewModel.PaywallUiState.Error).message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFFC62828)
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
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isPopular) Color(0xFFE3F2FD) else Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
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
                
                Spacer(modifier = Modifier.height(8.dp))
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
                        style = MaterialTheme.typography.titleLarge,
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
                
                Spacer(modifier = Modifier.height(4.dp))
                
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
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Features
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                features.forEach { feature ->
                    Text(
                        text = feature,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF666666)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Botón de suscripción
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPopular) Color(0xFF1976D2) else Color(0xFF4CAF50)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Comenzar Prueba Gratuita",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
