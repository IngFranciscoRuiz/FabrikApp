package com.fjrh.FabrikApp.ui.components

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fjrh.FabrikApp.data.billing.BillingService
import com.fjrh.FabrikApp.domain.model.SubscriptionInfo

@Composable
fun PaywallGate(
    subscriptionInfo: SubscriptionInfo?,
    billingService: BillingService,
    onNavigateToPaywall: (Boolean) -> Unit,
    onRestoreSubscription: () -> Unit,
    onBypassToApp: () -> Unit,
    content: @Composable () -> Unit
) {
    val isPremiumActive by billingService.isPremiumActive.collectAsState()
    val isConnected by billingService.isConnected.collectAsState()
    var showAppContent by remember { mutableStateOf(false) }
    
    if (!isConnected) {
        PaywallLoadingScreen()
    } else if (isPremiumActive || showAppContent) {
        content()
    } else {
        PaywallScreen(
            onSubscribeMonthly = { onNavigateToPaywall(true) },
            onSubscribeYearly = { onNavigateToPaywall(false) },
            onRestoreSubscription = onRestoreSubscription,
            onBypassToApp = { showAppContent = true }
        )
    }
}

@Composable
private fun PaywallLoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = Color(0xFF1976D2),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Verificando suscripción...",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF666666)
            )
        }
    }
}

@Composable
private fun PaywallScreen(
    onSubscribeMonthly: () -> Unit,
    onSubscribeYearly: () -> Unit,
    onRestoreSubscription: () -> Unit,
    onBypassToApp: () -> Unit
) {
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(40.dp))
                
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(60.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Desbloquea FabrikApp Premium",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF1A1A1A)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Accede a todas las funcionalidades avanzadas",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF666666)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
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
                    features = listOf(
                        "✓ Acceso completo a todas las funciones",
                        "✓ Backup automático",
                        "✓ Soporte prioritario",
                        "✓ Actualizaciones gratuitas"
                    ),
                    isPopular = false,
                    onClick = onSubscribeMonthly
                )
            }
            
            // Tarjeta de suscripción anual
            item {
                PaywallSubscriptionCard(
                    title = "Plan Anual",
                    price = "$749 MXN",
                    period = "por año",
                    features = listOf(
                        "✓ Todo del plan mensual",
                        "✓ 2 meses gratis",
                        "✓ Ahorro del 21%",
                        "✓ Acceso anticipado a nuevas funciones"
                    ),
                    isPopular = true,
                    onClick = onSubscribeYearly
                )
            }
            
            // Botón Restaurar Suscripción
            item {
                OutlinedButton(
                    onClick = onRestoreSubscription,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Restore,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Restaurar Suscripción")
                }
            }
            
            // 🔧 BOTÓN TEMPORAL PARA TESTING
            item {
                Button(
                    onClick = onBypassToApp,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF9C27B0)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeveloperMode,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "FJDVS IN",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun PaywallSubscriptionCard(
    title: String,
    price: String,
    period: String,
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
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                    
                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = price,
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
                
                if (isPopular) {
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
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                features.forEach { feature ->
                    Text(
                        text = feature,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF666666)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
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
