package com.fjrh.FabrikApp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.fjrh.FabrikApp.domain.model.SubscriptionInfo
import com.fjrh.FabrikApp.domain.model.SubscriptionStatus
import com.fjrh.FabrikApp.domain.usecase.SubscriptionManager

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
                featureName == "subscription_screen"
            }
            else -> true // Durante el trial, todo está disponible
        }
    } ?: false

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
        
        // Overlay de bloqueo
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
                    text = when (subscriptionInfo?.status) {
                        SubscriptionStatus.Expired -> "Funcionalidad Bloqueada"
                        SubscriptionStatus.Blocked -> "Acceso Denegado"
                        else -> "Funcionalidad Premium"
                    },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color(0xFFE65100)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = when (subscriptionInfo?.status) {
                        SubscriptionStatus.Expired -> "Tu prueba gratuita ha expirado. Suscríbete para continuar usando esta funcionalidad."
                        SubscriptionStatus.Blocked -> "Se detectó manipulación de datos. Contacta soporte técnico."
                        else -> "Esta funcionalidad está disponible solo para usuarios Premium."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF424242)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                when (subscriptionInfo?.status) {
                    SubscriptionStatus.Expired -> {
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
                    SubscriptionStatus.Blocked -> {
                        Text(
                            text = "Contacta soporte técnico",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFD32F2F),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    else -> {
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
                                text = "Actualizar a Premium",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
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
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE3F2FD)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = Color(0xFF1976D2),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Trial: ${info.daysRemaining} días restantes",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF1976D2),
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(onClick = onSubscribe) {
                            Text(
                                text = "Actualizar",
                                color = Color(0xFF1976D2),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            SubscriptionStatus.Expired -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEBEE)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
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
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE8F5E8)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
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
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEBEE)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
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
