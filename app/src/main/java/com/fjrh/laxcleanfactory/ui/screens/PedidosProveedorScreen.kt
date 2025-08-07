package com.fjrh.laxcleanfactory.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.IconButton
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fjrh.laxcleanfactory.data.local.entity.PedidoProveedorEntity
import com.fjrh.laxcleanfactory.ui.viewmodel.PedidosProveedorViewModel
import java.text.SimpleDateFormat
import java.util.*
import com.fjrh.laxcleanfactory.ui.utils.validarPrecio
import com.fjrh.laxcleanfactory.ui.utils.formatearPrecioConComas

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedidosProveedorScreen(
    viewModel: PedidosProveedorViewModel = hiltViewModel()
) {
    var showAddDialog by remember { mutableStateOf(false) }
    val pedidos by viewModel.pedidos.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pedidos a Proveedor") },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar pedido")
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
            if (pedidos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay pedidos registrados")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(pedidos) { pedido ->
                        PedidoCard(
                            pedido = pedido,
                            onEstadoChanged = { pedidoActualizado ->
                                viewModel.actualizarPedido(pedidoActualizado)
                            }
                        )
                    }
                }
            }
        }

        if (showAddDialog) {
            AgregarPedidoDialog(
                onDismiss = { showAddDialog = false },
                onPedidoAgregado = { pedido ->
                    viewModel.agregarPedido(pedido)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun PedidoCard(
    pedido: PedidoProveedorEntity,
    onEstadoChanged: (PedidoProveedorEntity) -> Unit = {}
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val colorEstado = when (pedido.estado) {
        "PAGADO" -> Color(0xFFD32F2F) // Rojo sobrio para pagado
        else -> Color(0xFF1976D2) // Azul sobrio para pendiente
    }
    
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
                    text = pedido.nombreProveedor,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$${String.format("%.2f", pedido.monto)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = pedido.productos,
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = dateFormat.format(Date(pedido.fecha)),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = pedido.estado,
                        style = MaterialTheme.typography.bodySmall,
                        color = colorEstado,
                        fontWeight = FontWeight.Bold
                    )
                    
                    if (pedido.estado == "PENDIENTE") {
                        IconButton(
                            onClick = {
                                val pedidoActualizado = pedido.copy(estado = "PAGADO")
                                onEstadoChanged(pedidoActualizado)
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Marcar como pagado",
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
            
            pedido.descripcion?.let { descripcion ->
                if (descripcion.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = descripcion,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun AgregarPedidoDialog(
    onDismiss: () -> Unit,
    onPedidoAgregado: (PedidoProveedorEntity) -> Unit
) {
    var nombreProveedor by remember { mutableStateOf("") }
    var productos by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("PENDIENTE") }
    var expandedEstado by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Pedido") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = nombreProveedor,
                    onValueChange = { nombreProveedor = it },
                    label = { Text("Nombre del proveedor") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = productos,
                    onValueChange = { productos = it },
                    label = { Text("Productos pedidos") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                OutlinedTextField(
                    value = monto,
                    onValueChange = { 
                        if (validarPrecio(it)) {
                            monto = it
                        }
                    },
                    label = { Text("Monto") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    isError = monto.isNotBlank() && !validarPrecio(monto),
                    supportingText = {
                        if (monto.isNotBlank() && !validarPrecio(monto)) {
                            Text("Máximo 6 dígitos enteros y 2 decimales")
                        }
                    }
                )

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción (opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Dropdown para estado
                Box {
                    OutlinedTextField(
                        value = estado,
                        onValueChange = { estado = it },
                        label = { Text("Estado") },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { expandedEstado = !expandedEstado }) {
                                Icon(
                                    imageVector = if (expandedEstado) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Expandir"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    DropdownMenu(
                        expanded = expandedEstado,
                        onDismissRequest = { expandedEstado = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("PENDIENTE", "PAGADO").forEach { estadoOption ->
                            DropdownMenuItem(
                                text = { Text(estadoOption) },
                                onClick = {
                                    estado = estadoOption
                                    expandedEstado = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val montoValue = monto.toDoubleOrNull() ?: 0.0
                    
                    if (nombreProveedor.isNotBlank() && productos.isNotBlank() && montoValue > 0) {
                        val pedido = PedidoProveedorEntity(
                            nombreProveedor = nombreProveedor,
                            productos = productos,
                            monto = montoValue,
                            estado = estado,
                            descripcion = descripcion.takeIf { it.isNotBlank() }
                        )
                        onPedidoAgregado(pedido)
                    }
                },
                enabled = nombreProveedor.isNotBlank() && 
                         productos.isNotBlank() && 
                         monto.isNotBlank() &&
                         validarPrecio(monto)
            ) {
                Text("Guardar Pedido")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
} 
