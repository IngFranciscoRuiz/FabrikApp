package com.fjrh.FabrikApp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fjrh.FabrikApp.data.local.entity.PedidoProveedorEntity
import com.fjrh.FabrikApp.ui.viewmodel.PedidosProveedorViewModel
import java.text.SimpleDateFormat
import java.util.*
import com.fjrh.FabrikApp.ui.utils.validarPrecio
import com.fjrh.FabrikApp.ui.utils.formatearPrecioConComas
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun PedidosProveedorScreen(
    viewModel: PedidosProveedorViewModel = hiltViewModel()
) {
    var showAddDialog by remember { mutableStateOf(false) }
    val pedidos by viewModel.pedidos.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    
    val pedidosFiltrados = pedidos.filter { pedido ->
        pedido.nombreProveedor.contains(searchQuery, ignoreCase = true) ||
        pedido.productos.contains(searchQuery, ignoreCase = true)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(top = 60.dp)
                .padding(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header moderno
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color(0xFF1A1A1A),
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { /* Navegar atrás */ }
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = "Pedidos a Proveedor",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFF1A1A1A),
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Estadísticas rápidas
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Total de pedidos
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1976D2)),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "${pedidos.size}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    
                    // Total de gastos
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFD32F2F)),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        val totalGastos = pedidos.sumOf { it.monto }
                        Text(
                            text = "$${String.format("%.0f", totalGastos)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Campo de búsqueda moderno
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color(0xFF666666),
                        modifier = Modifier.size(20.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Buscar pedidos...") },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true
                    )
                }
            }

            // Lista de pedidos
            if (pedidosFiltrados.isEmpty()) {
                OrdersEmptyState(
                    icon = Icons.Default.ShoppingCart,
                    title = if (searchQuery.isBlank()) "No hay pedidos" else "No se encontraron pedidos",
                    message = if (searchQuery.isBlank()) 
                        "No hay pedidos registrados aún. Registra tu primer pedido." 
                    else 
                        "No se encontraron pedidos que coincidan con '$searchQuery'"
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(pedidosFiltrados) { pedido ->
                        ModernPedidoCard(
                            pedido = pedido,
                            onEstadoChanged = { pedidoActualizado ->
                                viewModel.actualizarPedido(pedidoActualizado)
                            },
                            onDelete = { viewModel.eliminarPedido(pedido) }
                        )
                    }
                }
            }
        }
        
        // FAB para agregar nuevo pedido
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 100.dp, end = 20.dp),
            containerColor = Color(0xFF1976D2),
            contentColor = Color.White
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Agregar pedido",
                modifier = Modifier.size(24.dp)
            )
        }

        // Diálogo de agregar pedido
        if (showAddDialog) {
            ModernAgregarPedidoDialog(
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
fun OrdersEmptyState(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    message: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFFCCCCCC),
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF1A1A1A),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF666666),
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ModernPedidoCard(
    pedido: PedidoProveedorEntity,
    onEstadoChanged: (PedidoProveedorEntity) -> Unit = {},
    onDelete: () -> Unit = {}
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val colorEstado = when (pedido.estado) {
        "PAGADO" -> Color(0xFF4CAF50) // Verde para pagado
        else -> Color(0xFFFF9800) // Naranja para pendiente
    }
    
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
        directions = setOf(DismissDirection.EndToStart),
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
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    // Header con icono y monto
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = Color(0xFF1976D2),
                            modifier = Modifier.size(24.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = pedido.nombreProveedor,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFF1A1A1A),
                                fontWeight = FontWeight.Bold
                            )
                            
                            Text(
                                text = dateFormat.format(Date(pedido.fecha)),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF666666)
                            )
                        }
                        
                        Text(
                            text = "$${String.format("%.2f", pedido.monto)}",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color(0xFFD32F2F),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Detalles del pedido
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Productos:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF666666),
                                fontWeight = FontWeight.Medium
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Text(
                                text = pedido.productos,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF1A1A1A)
                            )
                            
                            pedido.descripcion?.let { descripcion ->
                                if (descripcion.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    Text(
                                        text = "Descripción:",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF666666),
                                        fontWeight = FontWeight.Medium
                                    )
                                    
                                    Spacer(modifier = Modifier.height(4.dp))
                                    
                                    Text(
                                        text = descripcion,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF1A1A1A)
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Estado y acciones
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Badge de estado
                        Card(
                            colors = CardDefaults.cardColors(containerColor = colorEstado),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = pedido.estado,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                        
                        // Botón de marcar como pagado
                        if (pedido.estado == "PENDIENTE") {
                            Button(
                                onClick = {
                                    val pedidoActualizado = pedido.copy(estado = "PAGADO")
                                    onEstadoChanged(pedidoActualizado)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Marcar Pagado",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    )
    
    // Diálogo de confirmación
    if (showDeleteDialog) {
        ModernEliminarPedidoDialog(
            pedido = pedido,
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
fun ModernAgregarPedidoDialog(
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
        title = { 
            Text(
                text = "Nuevo Pedido",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF1A1A1A),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Campo de proveedor
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Nombre del proveedor",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF666666),
                            fontWeight = FontWeight.Medium
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        TextField(
                            value = nombreProveedor,
                            onValueChange = { nombreProveedor = it },
                            placeholder = { Text("Ingresa el nombre del proveedor") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedIndicatorColor = Color(0xFF1976D2),
                                unfocusedIndicatorColor = Color(0xFFCCCCCC)
                            )
                        )
                    }
                }

                // Campo de productos
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Productos pedidos",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF666666),
                            fontWeight = FontWeight.Medium
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        TextField(
                            value = productos,
                            onValueChange = { productos = it },
                            placeholder = { Text("Describe los productos pedidos") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedIndicatorColor = Color(0xFF1976D2),
                                unfocusedIndicatorColor = Color(0xFFCCCCCC)
                            )
                        )
                    }
                }

                // Campo de monto
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Monto",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF666666),
                            fontWeight = FontWeight.Medium
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        TextField(
                            value = monto,
                            onValueChange = { 
                                if (validarPrecio(it)) {
                                    monto = it
                                }
                            },
                            placeholder = { Text("Ingresa el monto") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedIndicatorColor = Color(0xFF1976D2),
                                unfocusedIndicatorColor = Color(0xFFCCCCCC)
                            )
                        )
                    }
                }

                // Campo de descripción
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Descripción (opcional)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF666666),
                            fontWeight = FontWeight.Medium
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        TextField(
                            value = descripcion,
                            onValueChange = { descripcion = it },
                            placeholder = { Text("Agrega una descripción") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedIndicatorColor = Color(0xFF1976D2),
                                unfocusedIndicatorColor = Color(0xFFCCCCCC)
                            )
                        )
                    }
                }

                // Campo de estado
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Estado",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF666666),
                            fontWeight = FontWeight.Medium
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Box {
                            TextField(
                                value = estado,
                                onValueChange = { estado = it },
                                placeholder = { Text("Seleccionar estado") },
                                readOnly = true,
                                trailingIcon = {
                                    IconButton(onClick = { expandedEstado = !expandedEstado }) {
                                        Icon(
                                            imageVector = if (expandedEstado) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                            contentDescription = "Expandir",
                                            tint = Color(0xFF666666)
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedIndicatorColor = Color(0xFF1976D2),
                                    unfocusedIndicatorColor = Color(0xFFCCCCCC)
                                )
                            )
                            
                            DropdownMenu(
                                expanded = expandedEstado,
                                onDismissRequest = { expandedEstado = false },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White)
                            ) {
                                listOf("PENDIENTE", "PAGADO").forEach { estadoOption ->
                                    DropdownMenuItem(
                                        text = { 
                                            Text(
                                                text = estadoOption,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        },
                                        onClick = {
                                            estado = estadoOption
                                            expandedEstado = false
                                        }
                                    )
                                }
                            }
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
                         validarPrecio(monto),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Guardar Pedido",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Cancelar",
                    color = Color(0xFF666666)
                )
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
} 

@Composable
fun ModernEliminarPedidoDialog(
    pedido: PedidoProveedorEntity,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFD32F2F),
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "Eliminar Pedido",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF1A1A1A),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = { 
            Column {
                Text(
                    text = "¿Estás seguro de que quieres eliminar este pedido?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF666666)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Detalles del pedido:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF1A1A1A),
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Proveedor:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF666666)
                            )
                            Text(
                                text = pedido.nombreProveedor,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF1A1A1A),
                                fontWeight = FontWeight.Medium
                            )
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Monto:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF666666)
                            )
                            Text(
                                text = "$${String.format("%.2f", pedido.monto)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFD32F2F),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Estado:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF666666)
                            )
                            Text(
                                text = pedido.estado,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF1A1A1A),
                                fontWeight = FontWeight.Medium
                            )
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Fecha:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF666666)
                            )
                            Text(
                                text = dateFormat.format(Date(pedido.fecha)),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF1A1A1A),
                                fontWeight = FontWeight.Medium
                            )
                        }
                        
                        pedido.descripcion?.let { descripcion ->
                            if (descripcion.isNotBlank()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Descripción:",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF666666)
                                    )
                                    Text(
                                        text = descripcion,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF1A1A1A),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "ELIMINAR",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Cancelar",
                    color = Color(0xFF666666)
                )
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
} 
