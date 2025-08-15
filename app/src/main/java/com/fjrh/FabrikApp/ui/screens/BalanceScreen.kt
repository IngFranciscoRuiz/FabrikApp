package com.fjrh.FabrikApp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.fjrh.FabrikApp.data.local.entity.BalanceEntity
import com.fjrh.FabrikApp.ui.viewmodel.BalanceViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BalanceScreen(
    navController: NavController,
    viewModel: BalanceViewModel = hiltViewModel()
) {
    var showAddDialog by remember { mutableStateOf(false) }
    val balance by viewModel.balance.collectAsState()
    val totalIngresos by viewModel.totalIngresos.collectAsState()
    val totalEgresos by viewModel.totalEgresos.collectAsState()
    val utilidad = totalIngresos - totalEgresos

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(top = 60.dp)
                .padding(bottom = 100.dp)
        ) {
            // Header moderno
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(12.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color(0xFF1976D2)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Text(
                        text = "Balance Financiero",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color(0xFF1A1A1A),
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1976D2)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "${balance?.size ?: 0}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Resumen financiero
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalance,
                            contentDescription = null,
                            tint = Color(0xFF1976D2),
                            modifier = Modifier.size(24.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Text(
                            text = "Resumen Financiero",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color(0xFF1A1A1A),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Estadísticas principales
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        BalanceStatCard(
                            title = "Ingresos",
                            value = "$${String.format("%.2f", totalIngresos)}",
                            color = Color(0xFF4CAF50),
                            modifier = Modifier.weight(1f)
                        )
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        BalanceStatCard(
                            title = "Egresos",
                            value = "$${String.format("%.2f", totalEgresos)}",
                            color = Color(0xFFF44336),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    HorizontalDivider(color = Color(0xFFE0E0E0))
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Utilidad
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Utilidad",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF1A1A1A),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "$${String.format("%.2f", utilidad)}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = if (utilidad >= 0) Color(0xFF4CAF50) else Color(0xFFF44336),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    if (utilidad < 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFF44336),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Pérdida operativa",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFF44336),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Lista de movimientos
            Text(
                text = "Movimientos Recientes",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF1A1A1A),
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (balance.isNullOrEmpty()) {
                BalanceEmptyState()
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(balance) { movimiento ->
                        ModernMovimientoCard(movimiento = movimiento)
                    }
                }
            }
        }
        
        // FAB para agregar movimiento
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(horizontal = 20.dp, vertical = 60.dp),
            containerColor = Color(0xFF1976D2),
            contentColor = Color.White
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Agregar movimiento",
                modifier = Modifier.size(24.dp)
            )
        }
        
        // Snackbar
        SnackbarHost(
            hostState = remember { SnackbarHostState() },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    if (showAddDialog) {
        ModernAgregarMovimientoDialog(
            onDismiss = { showAddDialog = false },
            onMovimientoAgregado = { movimiento ->
                viewModel.agregarMovimiento(movimiento)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun BalanceStatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF666666)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ModernMovimientoCard(movimiento: BalanceEntity) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val color = if (movimiento.tipo == "INGRESO") Color(0xFF4CAF50) else Color(0xFFF44336)
    val icon = if (movimiento.tipo == "INGRESO") Icons.Default.TrendingUp else Icons.Default.TrendingDown
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = movimiento.concepto,
                        style = MaterialTheme.typography.titleSmall,
                        color = Color(0xFF1A1A1A),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = dateFormat.format(Date(movimiento.fecha)),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF999999)
                    )
                    movimiento.descripcion?.let { desc ->
                        if (desc.isNotBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = desc,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF666666)
                            )
                        }
                    }
                }
            }
            
            Card(
                colors = CardDefaults.cardColors(containerColor = color),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "${if (movimiento.tipo == "INGRESO") "+" else "-"}$${String.format("%.2f", movimiento.monto)}",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun BalanceEmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.AccountBalance,
                contentDescription = null,
                tint = Color(0xFFCCCCCC),
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No hay movimientos registrados",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF666666),
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Toca el botón + para registrar tu primer movimiento",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF999999),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ModernAgregarMovimientoDialog(
    onDismiss: () -> Unit,
    onMovimientoAgregado: (BalanceEntity) -> Unit
) {
    var tipo by remember { mutableStateOf("INGRESO") }
    var concepto by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                text = "Nuevo Movimiento",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Selector de tipo
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = tipo == "INGRESO",
                                onClick = { tipo = "INGRESO" },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color(0xFF4CAF50)
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Ingreso",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF1A1A1A)
                            )
                        }
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = tipo == "EGRESO",
                                onClick = { tipo = "EGRESO" },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color(0xFFF44336)
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Egreso",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF1A1A1A)
                            )
                        }
                    }
                }

                BalanceFormField(
                    value = concepto,
                    onValueChange = { concepto = it },
                    label = "Concepto",
                    icon = Icons.Default.Description,
                    color = Color(0xFF1976D2)
                )

                BalanceFormField(
                    value = monto,
                    onValueChange = { monto = it },
                    label = "Monto",
                    icon = Icons.Default.AttachMoney,
                    color = if (tipo == "INGRESO") Color(0xFF4CAF50) else Color(0xFFF44336)
                )

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción (opcional)", color = Color(0xFF1A1A1A)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1976D2),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedTextColor = Color(0xFF1A1A1A),
                        unfocusedTextColor = Color(0xFF1A1A1A),
                        focusedLabelColor = Color(0xFF1976D2),
                        unfocusedLabelColor = Color(0xFF666666)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val montoValue = monto.toDoubleOrNull() ?: 0.0
                    if (concepto.isNotBlank() && montoValue > 0) {
                        val movimiento = BalanceEntity(
                            tipo = tipo,
                            concepto = concepto,
                            monto = montoValue,
                            fecha = System.currentTimeMillis(),
                            descripcion = descripcion.takeIf { it.isNotBlank() }
                        )
                        onMovimientoAgregado(movimiento)
                    }
                },
                enabled = concepto.isNotBlank() && monto.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Guardar", color = Color.White, fontWeight = FontWeight.Medium)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF666666))
            ) {
                Text("Cancelar")
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun BalanceFormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color(0xFF1A1A1A)) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color
            )
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = color,
            unfocusedBorderColor = Color(0xFFE0E0E0),
            focusedTextColor = Color(0xFF1A1A1A),
            unfocusedTextColor = Color(0xFF1A1A1A),
            focusedLabelColor = color,
            unfocusedLabelColor = Color(0xFF666666)
        ),
        shape = RoundedCornerShape(12.dp)
    )
} 
