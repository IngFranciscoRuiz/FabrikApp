package com.fjrh.FabrikApp.ui.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay

/**
 * Componente para detectar el gesto oculto de multiusuario
 * Gesto: Tocar 5 veces rápidamente en la esquina superior derecha
 */
@Composable
fun MultiUserGesture(
    onMultiUserActivated: () -> Unit,
    content: @Composable () -> Unit
) {
    var tapCount by remember { mutableStateOf(0) }
    var lastTapTime by remember { mutableStateOf(0L) }
    var showDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(tapCount) {
        if (tapCount >= 5) {
            showDialog = true
            tapCount = 0
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        content()
        
        // Área invisible para detectar el gesto (esquina superior derecha)
        Box(
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.TopEnd)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val currentTime = System.currentTimeMillis()
                        
                        // Verificar si el tap está en el área correcta (esquina superior derecha)
                        if (offset.x > size.width - 100 && offset.y < 100) {
                            // Verificar si es un tap rápido (dentro de 2 segundos)
                            if (currentTime - lastTapTime < 2000) {
                                tapCount++
                            } else {
                                tapCount = 1
                            }
                            lastTapTime = currentTime
                        }
                    }
                }
        )
    }
    
    if (showDialog) {
        MultiUserActivationDialog(
            onConfirm = {
                showDialog = false
                onMultiUserActivated()
            },
            onDismiss = {
                showDialog = false
            }
        )
    }
}

@Composable
private fun MultiUserActivationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE8F5E8)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = Color(0xFF4CAF50)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Modo Multiusuario",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF2E7D32)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "¿Deseas activar el modo multiusuario?\n\n" +
                           "Esta funcionalidad permite:\n" +
                           "• Múltiples usuarios en el mismo dispositivo\n" +
                           "• Roles y permisos personalizados\n" +
                           "• Sincronización de datos\n" +
                           "• Auditoría de actividades",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF424242)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF757575)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cancelar")
                    }
                    
                    Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Activar")
                    }
                }
            }
        }
    }
}

/**
 * Componente para mostrar el estado del modo multiusuario
 */
@Composable
fun MultiUserStatusIndicator(
    isMultiUserActive: Boolean,
    currentUser: String? = null,
    onSwitchUser: () -> Unit = {}
) {
    if (isMultiUserActive) {
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
                    imageVector = Icons.Default.Group,
                    contentDescription = null,
                    tint = Color(0xFF1976D2),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Multiusuario: ${currentUser ?: "Usuario"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF1976D2),
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = onSwitchUser) {
                    Icon(
                        imageVector = Icons.Default.SwitchAccount,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Cambiar",
                        color = Color(0xFF1976D2),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Componente para seleccionar usuario
 */
@Composable
fun UserSelectionDialog(
    users: List<String>,
    currentUser: String?,
    onUserSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Seleccionar Usuario",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                users.forEach { user ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = user == currentUser,
                            onClick = { onUserSelected(user) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = user,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    
                    Button(
                        onClick = { onDismiss() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        )
                    ) {
                        Text("Confirmar")
                    }
                }
            }
        }
    }
}

