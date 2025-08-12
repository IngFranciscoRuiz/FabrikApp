package com.fjrh.FabrikApp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.IconButton
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fjrh.FabrikApp.data.local.entity.VentaEntity
import com.fjrh.FabrikApp.ui.viewmodel.VentasViewModel
import java.text.SimpleDateFormat
import java.util.*
import com.fjrh.FabrikApp.ui.utils.validarLitros
import com.fjrh.FabrikApp.ui.utils.validarPrecio
import com.fjrh.FabrikApp.ui.utils.formatearPrecio
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun VentasScreen(
    viewModel: VentasViewModel = hiltViewModel()
) {
    var showAddDialog by remember { mutableStateOf(false) }
    val ventas by viewModel.ventas.collectAsState()
    val stockProductos by viewModel.stockProductos.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ventas") },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar venta")
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
            if (ventas.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay ventas registradas")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(ventas) { venta ->
                        VentaCard(
                            venta = venta,
                            onDelete = { viewModel.eliminarVenta(venta) }
                        )
                    }
                }
            }
        }

        if (showAddDialog) {
            AgregarVentaDialog(
                stockProductos = stockProductos,
                onDismiss = { showAddDialog = false },
                onVentaAgregada = { venta ->
                    viewModel.agregarVenta(venta)
                    showAddDialog = false
                }
            )
        }


    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun VentaCard(
    venta: VentaEntity,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    

    
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    val dismissState = rememberDismissState(
        confirmStateChange = { dismissValue ->
            if (dismissValue == DismissValue.DismissedToStart) {
                showDeleteDialog = true
                true
            } else {
                false
            }
        }
    )
    
    // Resetear el estado cuando se cierre el diálogo
    LaunchedEffect(showDeleteDialog) {
        if (!showDeleteDialog) {
            dismissState.reset()
        }
    }
    
    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.EndToStart), // Solo swipe a la izquierda
        background = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFD32F2F))
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        dismissContent = {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = venta.nombreProducto,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$${String.format("%.2f", venta.litrosVendidos * venta.precioPorLitro)}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${venta.litrosVendidos} L")
                        Text("$${String.format("%.2f", venta.precioPorLitro)}/L")
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = dateFormat.format(Date(venta.fecha)),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    
                    venta.cliente?.let { cliente ->
                        Text(
                            text = "Cliente: $cliente",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    )
    
    // Diálogo local de confirmación
    if (showDeleteDialog) {
        EliminarVentaDialog(
            venta = venta,
            onConfirm = {
                onDelete()
                showDeleteDialog = false
            },
            onDismiss = { 
                showDeleteDialog = false
            }
        )
    }
}



@Composable
fun AgregarVentaDialog(
    stockProductos: List<com.fjrh.FabrikApp.domain.model.StockProducto>,
    onDismiss: () -> Unit,
    onVentaAgregada: (VentaEntity) -> Unit
) {
    var selectedProducto by remember { mutableStateOf("") }
    var litrosVendidos by remember { mutableStateOf("") }
    var precioPorLitro by remember { mutableStateOf("") }
    var cliente by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Venta") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Dropdown para seleccionar producto
                Box {
                    OutlinedTextField(
                        value = selectedProducto,
                        onValueChange = { selectedProducto = it },
                        label = { Text("Producto") },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { expanded = !expanded }) {
                                Icon(
                                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Expandir"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        stockProductos.filter { it.stock > 0 }.forEach { producto ->
                            DropdownMenuItem(
                                text = { Text("${producto.nombre} (${producto.stock} L disponible)") },
                                onClick = {
                                    selectedProducto = producto.nombre
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = litrosVendidos,
                    onValueChange = { 
                        if (validarLitros(it)) {
                            litrosVendidos = it
                        }
                    },
                    label = { Text("Litros vendidos") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    isError = litrosVendidos.isNotBlank() && !validarLitros(litrosVendidos),
                    supportingText = {
                        if (litrosVendidos.isNotBlank() && !validarLitros(litrosVendidos)) {
                            Text("Máximo 6 dígitos enteros y 3 decimales")
                        }
                    }
                )

                OutlinedTextField(
                    value = precioPorLitro,
                    onValueChange = { 
                        if (validarPrecio(it)) {
                            precioPorLitro = it
                        }
                    },
                    label = { Text("Precio por litro") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    isError = precioPorLitro.isNotBlank() && !validarPrecio(precioPorLitro),
                    supportingText = {
                        if (precioPorLitro.isNotBlank() && !validarPrecio(precioPorLitro)) {
                            Text("Máximo 6 dígitos enteros y 2 decimales")
                        }
                    }
                )

                OutlinedTextField(
                    value = cliente,
                    onValueChange = { cliente = it },
                    label = { Text("Cliente (opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val litros = litrosVendidos.toFloatOrNull() ?: 0f
                    val precio = precioPorLitro.toDoubleOrNull() ?: 0.0
                    
                    // Validar stock disponible
                    val productoSeleccionado = stockProductos.find { it.nombre == selectedProducto }
                    val stockDisponible = productoSeleccionado?.stock ?: 0f
                    
                    if (selectedProducto.isNotBlank() && litros > 0 && precio > 0) {
                        if (litros <= stockDisponible) {
                            val venta = VentaEntity(
                                nombreProducto = selectedProducto,
                                litrosVendidos = litros,
                                precioPorLitro = precio,
                                fecha = System.currentTimeMillis(),
                                cliente = cliente.takeIf { it.isNotBlank() }
                            )
                            onVentaAgregada(venta)
                        } else {
                            // Mostrar error de stock insuficiente
                            // TODO: Implementar Snackbar o AlertDialog
                        }
                    }
                },
                enabled = selectedProducto.isNotBlank() && 
                         litrosVendidos.isNotBlank() && 
                         precioPorLitro.isNotBlank() &&
                         validarLitros(litrosVendidos) &&
                         validarPrecio(precioPorLitro)
            ) {
                Text("Guardar Venta")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
} 

@Composable
fun EliminarVentaDialog(
    venta: VentaEntity,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                text = "⚠️ Eliminar Venta",
                color = Color(0xFFD32F2F)
            )
        },
        text = { 
            Column {
                Text(
                    text = "¿Estás seguro de que quieres eliminar esta venta?",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Esta acción no se puede deshacer y afectará el balance financiero.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "Detalles de la venta:",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Producto: ${venta.nombreProducto}")
                        Text("Litros: ${venta.litrosVendidos} L")
                        Text("Precio: $${String.format("%.2f", venta.precioPorLitro)}/L")
                        Text("Total: $${String.format("%.2f", venta.litrosVendidos * venta.precioPorLitro)}")
                        Text("Fecha: ${dateFormat.format(Date(venta.fecha))}")
                        venta.cliente?.let { cliente ->
                            Text("Cliente: $cliente")
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD32F2F)
                )
            ) {
                Text("ELIMINAR")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
} 
