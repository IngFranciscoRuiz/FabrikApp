package com.fjrh.FabrikApp.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * Componente para mostrar errores de manera consistente
 */
@Composable
fun ErrorDisplay(
    error: String?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    type: ErrorType = ErrorType.ERROR
) {
    if (error != null) {
        var visible by remember { mutableStateOf(true) }
        
        LaunchedEffect(error) {
            visible = true
            // Auto-dismiss después de 5 segundos
            delay(5000)
            visible = false
            onDismiss()
        }
        
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (type) {
                        ErrorType.ERROR -> Color(0xFFFFEBEE)
                        ErrorType.WARNING -> Color(0xFFFFF3E0)
                        ErrorType.INFO -> Color(0xFFE3F2FD)
                    }
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when (type) {
                            ErrorType.ERROR -> Icons.Default.Error
                            ErrorType.WARNING -> Icons.Default.Warning
                            ErrorType.INFO -> Icons.Default.Info
                        },
                        contentDescription = null,
                        tint = when (type) {
                            ErrorType.ERROR -> Color(0xFFD32F2F)
                            ErrorType.WARNING -> Color(0xFFFF9800)
                            ErrorType.INFO -> Color(0xFF2196F3)
                        },
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyMedium,
                        color = when (type) {
                            ErrorType.ERROR -> Color(0xFFD32F2F)
                            ErrorType.WARNING -> Color(0xFFE65100)
                            ErrorType.INFO -> Color(0xFF1976D2)
                        },
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    
                    IconButton(
                        onClick = {
                            visible = false
                            onDismiss()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error, // Usar un icono de cerrar
                            contentDescription = "Cerrar",
                            tint = when (type) {
                                ErrorType.ERROR -> Color(0xFFD32F2F)
                                ErrorType.WARNING -> Color(0xFFFF9800)
                                ErrorType.INFO -> Color(0xFF2196F3)
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Componente para mostrar mensajes de éxito
 */
@Composable
fun SuccessDisplay(
    message: String?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (message != null) {
        var visible by remember { mutableStateOf(true) }
        
        LaunchedEffect(message) {
            visible = true
            // Auto-dismiss después de 3 segundos
            delay(3000)
            visible = false
            onDismiss()
        }
        
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE8F5E8)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    
                    IconButton(
                        onClick = {
                            visible = false
                            onDismiss()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Cerrar",
                            tint = Color(0xFF4CAF50)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Componente para mostrar loading
 */
@Composable
fun LoadingDisplay(
    isLoading: Boolean,
    message: String = "Cargando...",
    modifier: Modifier = Modifier
) {
    if (isLoading) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF5F5F5)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

/**
 * Tipos de error
 */
enum class ErrorType {
    ERROR,
    WARNING,
    INFO
}

/**
 * Componente combinado para manejar estados de UI
 */
@Composable
fun UiStateDisplay(
    isLoading: Boolean,
    error: String?,
    success: String?,
    onErrorDismiss: () -> Unit,
    onSuccessDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        ErrorDisplay(
            error = error,
            onDismiss = onErrorDismiss,
            type = ErrorType.ERROR
        )
        
        SuccessDisplay(
            message = success,
            onDismiss = onSuccessDismiss
        )
        
        LoadingDisplay(
            isLoading = isLoading
        )
    }
}
