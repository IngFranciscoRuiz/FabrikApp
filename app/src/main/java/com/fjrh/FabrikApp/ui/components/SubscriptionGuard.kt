package com.fjrh.FabrikApp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fjrh.FabrikApp.domain.model.SubscriptionInfo
import com.fjrh.FabrikApp.domain.model.SubscriptionStatus
import com.fjrh.FabrikApp.domain.usecase.SubscriptionManager
import com.fjrh.FabrikApp.data.billing.BillingService

/**
 * Componente que protege funcionalidades según el estado de suscripción
 */
@Composable
fun SubscriptionGuard(
    subscriptionInfo: SubscriptionInfo?,
    featureName: String,
    onSubscribe: () -> Unit,
    content: @Composable () -> Unit
) {
    val isFeatureAvailable = subscriptionInfo?.let { info ->
        when {
            info.isPremium -> true
            info.isBlocked -> false
            info.isTrialExpired() -> {
                // Si el trial expiró, bloquear TODO excepto la pantalla de suscripción
                featureName == "subscription_screen" || featureName == "paywall"
            }
            else -> {
                // Durante el trial, todo está disponible EXCEPTO backup
                featureName != "backup"
            }
        }
    } ?: true // Si no hay info de suscripción, permitir acceso

    if (isFeatureAvailable) {
        content()
    } else {
        FeatureLockedOverlay(
            subscriptionInfo = subscriptionInfo,
            featureName = featureName,
            onSubscribe = onSubscribe
        )
    }
}

/**
 * Componente que protege funcionalidades usando verificación real de Google Play
 */
@Composable
fun SubscriptionGuardWithBilling(
    subscriptionInfo: SubscriptionInfo?,
    billingService: BillingService,
    featureName: String,
    onSubscribe: () -> Unit,
    content: @Composable () -> Unit
) {
    val isPremiumActive by billingService.isPremiumActive.collectAsState()
    
    val isFeatureAvailable = when {
        isPremiumActive -> true // Google Play confirma que es premium
        subscriptionInfo?.isBlocked == true -> false
        subscriptionInfo?.isTrialExpired() == true -> {
            // Si el trial expiró, bloquear TODO excepto la pantalla de suscripción
            featureName == "subscription_screen"
        }
        else -> {
            // Durante el trial, todo está disponible EXCEPTO backup
            featureName != "backup"
        }
    }

    if (isFeatureAvailable) {
        content()
    } else {
        FeatureLockedOverlay(
            subscriptionInfo = subscriptionInfo,
            featureName = featureName,
            onSubscribe = onSubscribe
        )
    }
}

@Composable
private fun FeatureLockedOverlay(
    subscriptionInfo: SubscriptionInfo?,
    featureName: String,
    onSubscribe: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Contenido original (bloqueado)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            // Aquí iría el contenido original pero deshabilitado
        }
        
        // Overlay simple con una sola tarjeta
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.Center),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFF3E0)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = Color(0xFFFF9800)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Funcionalidad Premium",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color(0xFFE65100)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Suscríbete para obtener todas las funciones.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF424242)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = onSubscribe,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
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

/**
 * Componente para mostrar el estado de suscripción en la UI
 */
@Composable
fun SubscriptionStatusIndicator(
    subscriptionInfo: SubscriptionInfo?,
    onSubscribe: () -> Unit
) {
            subscriptionInfo?.let { info ->
            when (info.status) {
                SubscriptionStatus.Trial -> {
                    // En el sistema simplificado, no mostramos trial local
                    // Google Play maneja todo el trial automáticamente
                }
 
                SubscriptionStatus.Expired -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEBEE)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Trial expirado",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFD32F2F),
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Button(
                            onClick = onSubscribe,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50)
                            )
                        ) {
                            Text(
                                text = "Suscribirse",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            SubscriptionStatus.Active -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE8F5E8)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Premium Activo",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            SubscriptionStatus.Blocked -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEBEE)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Acceso bloqueado",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFD32F2F),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
