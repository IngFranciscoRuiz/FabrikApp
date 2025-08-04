package com.fjrh.karycleanfactory.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fjrh.karycleanfactory.data.local.entity.BalanceEntity
import com.fjrh.karycleanfactory.ui.viewmodel.BalanceViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BalanceScreen(
    viewModel: BalanceViewModel = hiltViewModel()
) {
    var showAddDialog by remember { mutableStateOf(false) }
    val balance by viewModel.balance.collectAsState()
    val totalIngresos by viewModel.totalIngresos.collectAsState()
    val totalEgresos by viewModel.totalEgresos.collectAsState()
    val utilidad = totalIngresos - totalEgresos

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Balance Financiero") },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar movimiento")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Resumen financiero
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Resumen Financiero",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Ingresos", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                "$${String.format("%.2f", totalIngresos)}",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFF4CAF50),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column {
                            Text("Egresos", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                "$${String.format("%.2f", totalEgresos)}",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFFF44336),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    HorizontalDivider()
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Utilidad",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "$${String.format("%.2f", utilidad)}",
                            style = MaterialTheme.typography.titleLarge,
                            color = if (utilidad >= 0) Color(0xFF4CAF50) else Color(0xFFF44336),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    if (utilidad < 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "⚠️ Pérdida operativa",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFF44336)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Lista de movimientos
            Text(
                text = "Movimientos Recientes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (balance.isNullOrEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay movimientos registrados")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(balance) { movimiento ->
                        MovimientoCard(movimiento = movimiento)
                    }
                }
            }
        }

        if (showAddDialog) {
            AgregarMovimientoDialog(
                onDismiss = { showAddDialog = false },
                onMovimientoAgregado = { movimiento ->
                    viewModel.agregarMovimiento(movimiento)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun MovimientoCard(movimiento: BalanceEntity) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val color = if (movimiento.tipo == "INGRESO") Color(0xFF4CAF50) else Color(0xFFF44336)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = movimiento.concepto,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = dateFormat.format(Date(movimiento.fecha)),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                movimiento.descripcion?.let { desc ->
                    Text(
                        text = desc,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
            
            Text(
                text = "${if (movimiento.tipo == "INGRESO") "+" else "-"}$${String.format("%.2f", movimiento.monto)}",
                style = MaterialTheme.typography.titleMedium,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun AgregarMovimientoDialog(
    onDismiss: () -> Unit,
    onMovimientoAgregado: (BalanceEntity) -> Unit
) {
    var tipo by remember { mutableStateOf("INGRESO") }
    var concepto by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Movimiento") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Selector de tipo
                Row {
                    Text("Tipo:", modifier = Modifier.padding(end = 16.dp))
                    Row {
                        RadioButton(
                            selected = tipo == "INGRESO",
                            onClick = { tipo = "INGRESO" }
                        )
                        Text("Ingreso", modifier = Modifier.padding(start = 8.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Row {
                        RadioButton(
                            selected = tipo == "EGRESO",
                            onClick = { tipo = "EGRESO" }
                        )
                        Text("Egreso", modifier = Modifier.padding(start = 8.dp))
                    }
                }

                OutlinedTextField(
                    value = concepto,
                    onValueChange = { concepto = it },
                    label = { Text("Concepto") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = monto,
                    onValueChange = { monto = it },
                    label = { Text("Monto") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción (opcional)") },
                    modifier = Modifier.fillMaxWidth()
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
                enabled = concepto.isNotBlank() && monto.isNotBlank()
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
} 