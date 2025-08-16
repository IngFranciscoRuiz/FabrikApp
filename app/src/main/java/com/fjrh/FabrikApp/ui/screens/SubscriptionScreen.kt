package com.fjrh.FabrikApp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.hilt.navigation.compose.hiltViewModel
import com.fjrh.FabrikApp.domain.model.SubscriptionInfo
import com.fjrh.FabrikApp.domain.model.SubscriptionStatus
import com.fjrh.FabrikApp.ui.viewmodel.SubscriptionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(
    onNavigateBack: () -> Unit,
    onSubscribe: () -> Unit,
    viewModel: SubscriptionViewModel = hiltViewModel()
) {
    val subscriptionInfo by viewModel.subscriptionInfo.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.initializeTrial()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Suscripción Premium") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Header con información del trial
            TrialStatusCard(subscriptionInfo)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Beneficios Premium
            PremiumBenefitsCard()
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Opciones de suscripción
            SubscriptionOptionsCard(
                onSubscribe = onSubscribe,
                isLoading = isLoading
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Información adicional
            AdditionalInfoCard()
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun TrialStatusCard(subscriptionInfo: SubscriptionInfo?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (subscriptionInfo?.status) {
                SubscriptionStatus.Trial -> Color(0xFFE3F2FD)
                SubscriptionStatus.Expired -> Color(0xFFFFEBEE)
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = when (subscriptionInfo?.status) {
                    SubscriptionStatus.Trial -> Icons.Default.Schedule
                    SubscriptionStatus.Expired -> Icons.Default.Warning
                    SubscriptionStatus.Active -> Icons.Default.Star
                    else -> Icons.Default.Info
                },
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = when (subscriptionInfo?.status) {
                    SubscriptionStatus.Trial -> Color(0xFF1976D2)
                    SubscriptionStatus.Expired -> Color(0xFFD32F2F)
                    SubscriptionStatus.Active -> Color(0xFFFFD700)
                    else -> MaterialTheme.colorScheme.primary
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = when (subscriptionInfo?.status) {
                    SubscriptionStatus.Trial -> "Prueba Gratuita"
                    SubscriptionStatus.Expired -> "Trial Expirado"
                    SubscriptionStatus.Active -> "Premium Activo"
                    else -> "Estado Desconocido"
                },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            when (subscriptionInfo?.status) {
                SubscriptionStatus.Trial -> {
                    Text(
                        text = "Te quedan ${subscriptionInfo.daysRemaining} días de prueba",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF1976D2)
                    )
                }
                SubscriptionStatus.Expired -> {
                    Text(
                        text = "Tu prueba gratuita ha expirado",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = Color(0xFFD32F2F)
                    )
                }
                SubscriptionStatus.Active -> {
                    Text(
                        text = "¡Disfruta de todas las funcionalidades!",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF2E7D32)
                    )
                }
                else -> {
                    Text(
                        text = "Inicializando...",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun PremiumBenefitsCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF3E5F5)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Beneficios Premium",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF7B1FA2)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            val benefits = listOf(
                "✨ Agregar, editar y eliminar ingredientes",
                "📊 Gestión completa de fórmulas",
                "💰 Registro de ventas ilimitado",
                "📈 Análisis avanzados y reportes",
                "💾 Exportar e importar datos",
                "🔄 Backup automático en la nube",
                "👥 Soporte para múltiples usuarios",
                "🎯 Funcionalidades exclusivas"
            )
            
            benefits.forEach { benefit ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = benefit,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF424242)
                    )
                }
            }
        }
    }
}

@Composable
private fun SubscriptionOptionsCard(
    onSubscribe: () -> Unit,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE8F5E8)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Suscripción Premium",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Acceso completo a todas las funcionalidades",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = Color(0xFF424242)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Opciones de suscripción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    SubscriptionOption(
                        title = "Mensual",
                        price = "$9.99",
                        period = "mes",
                        isPopular = false,
                        onClick = onSubscribe
                    )
                }
                
                Box(modifier = Modifier.weight(1f)) {
                    SubscriptionOption(
                        title = "Anual",
                        price = "$99.99",
                        period = "año",
                        isPopular = true,
                        onClick = onSubscribe
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onSubscribe,
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Suscribirse Ahora",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun SubscriptionOption(
    title: String,
    price: String,
    period: String,
    isPopular: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPopular) Color(0xFFFFD700) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isPopular) {
                Text(
                    text = "MÁS POPULAR",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE65100)
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = price,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50)
            )
            
            Text(
                text = "por $period",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF757575)
            )
            
            if (isPopular) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ahorra 17%",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
            }
        }
    }
}

@Composable
private fun AdditionalInfoCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Información Importante",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "• Cancelación en cualquier momento\n" +
                       "• Acceso inmediato a todas las funciones\n" +
                       "• Soporte técnico prioritario\n" +
                       "• Actualizaciones gratuitas",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF424242)
            )
        }
    }
}
