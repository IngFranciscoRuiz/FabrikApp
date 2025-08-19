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
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun VentasScreen(
    navController: NavController,
    viewModel: VentasViewModel = hiltViewModel()
) {
    var showAddDialog by remember { mutableStateOf(false) }
    val ventas by viewModel.ventas.collectAsState()
    val stockProductos by viewModel.stockProductos.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    
    val ventasFiltradas = ventas.filter { venta ->
        venta.nombreProducto.contains(searchQuery, ignoreCase = true) ||
        venta.cliente?.contains(searchQuery, ignoreCase = true) == true
    }

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
                .padding(bottom = 16.dp),
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
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { navController.navigateUp() }
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = "Ventas",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Estadísticas rápidas
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Total de ventas
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "${ventas.size}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    
                    // Total de ingresos
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        val totalIngresos = ventas.sumOf { it.litrosVendidos * it.precioPorLitro }
                        Text(
                            text = "$${String.format("%.0f", totalIngresos)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondary,
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
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Buscar ventas...") },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = Color(0xFF1A1A1A),
                            unfocusedTextColor = Color(0xFF1A1A1A)
                        ),
                        singleLine = true
                    )
                }
            }

            // Lista de ventas
            if (ventasFiltradas.isEmpty()) {
                SalesEmptyState(
                    icon = Icons.Default.AttachMoney,
                    title = if (searchQuery.isBlank()) "No hay ventas" else "No se encontraron ventas",
                    message = if (searchQuery.isBlank()) 
                        "No hay ventas registradas aún. Registra tu primera venta." 
                    else 
                        "No se encontraron ventas que coincidan con '$searchQuery'"
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(ventasFiltradas) { venta ->
                        ModernVentaCard(
                            venta = venta,
                            onDelete = { viewModel.eliminarVenta(venta) }
                        )
                    }
                }
            }
        }
        
        // FAB para agregar nueva venta
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 150.dp, end = 20.dp),
            containerColor = Color(0xFF4CAF50),
            contentColor = Color.White
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Agregar venta",
                modifier = Modifier.size(24.dp)
            )
        }

        // Diálogo de agregar venta
        if (showAddDialog) {
            ModernAgregarVentaDialog(
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

@Composable
fun SalesEmptyState(
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
fun ModernVentaCard(
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
                    // Header con icono y total
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AttachMoney,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(24.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column(
                            modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = venta.nombreProducto,
                    style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFF1A1A1A),
                    fontWeight = FontWeight.Bold
                )
                            
                            Text(
                                text = dateFormat.format(Date(venta.fecha)),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF666666)
                            )
                        }
                        
                Text(
                    text = "$${String.format("%.2f", venta.litrosVendidos * venta.precioPorLitro)}",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Detalles de la venta
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Litros vendidos:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF666666)
                                )
                                Text(
                                    text = "${venta.litrosVendidos} L",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF1A1A1A),
                                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                                Text(
                                    text = "Precio por litro:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF666666)
                                )
                                Text(
                                    text = "$${String.format("%.2f", venta.precioPorLitro)}/L",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF1A1A1A),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            
                            venta.cliente?.let { cliente ->
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
            Text(
                                        text = "Cliente:",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF666666)
                                    )
                Text(
                                        text = cliente,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF1A1A1A),
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
                }
            }
        }
    )
    
    // Diálogo de confirmación
    if (showDeleteDialog) {
        ModernEliminarVentaDialog(
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
fun ModernAgregarVentaDialog(
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
        title = { 
            Text(
                text = "Nueva Venta",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF1A1A1A),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Dropdown para seleccionar producto
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Producto",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF666666),
                            fontWeight = FontWeight.Medium
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Box {
                            TextField(
                        value = selectedProducto,
                        onValueChange = { selectedProducto = it },
                                placeholder = { Text("Seleccionar producto") },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { expanded = !expanded }) {
                                Icon(
                                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
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
                                    unfocusedIndicatorColor = Color(0xFFCCCCCC),
                                    focusedTextColor = Color(0xFF1A1A1A),
                                    unfocusedTextColor = Color(0xFF1A1A1A)
                                )
                    )
                    
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .fillMaxWidth()
                                    .background(Color.White)
                    ) {
                        stockProductos.filter { it.stock > 0 }.forEach { producto ->
                            DropdownMenuItem(
                                        text = { 
                                            Text(
                                                text = "${producto.nombre} (${producto.stock} L disponible)",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color(0xFF1A1A1A)
                                            )
                                        },
                                onClick = {
                                    selectedProducto = producto.nombre
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                    }
                }

                // Campo de litros
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Litros vendidos",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF666666),
                            fontWeight = FontWeight.Medium
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        TextField(
                    value = litrosVendidos,
                    onValueChange = { 
                        if (validarLitros(it)) {
                            litrosVendidos = it
                        }
                    },
                            placeholder = { Text("Ingresa los litros") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedIndicatorColor = Color(0xFF1976D2),
                                unfocusedIndicatorColor = Color(0xFFCCCCCC),
                                focusedTextColor = Color(0xFF1A1A1A),
                                unfocusedTextColor = Color(0xFF1A1A1A)
                            )
                        )
                    }
                }

                // Campo de precio
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Precio por litro",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF666666),
                            fontWeight = FontWeight.Medium
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        TextField(
                    value = precioPorLitro,
                    onValueChange = { 
                        if (validarPrecio(it)) {
                            precioPorLitro = it
                        }
                    },
                            placeholder = { Text("Ingresa el precio") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedIndicatorColor = Color(0xFF1976D2),
                                unfocusedIndicatorColor = Color(0xFFCCCCCC),
                                focusedTextColor = Color(0xFF1A1A1A),
                                unfocusedTextColor = Color(0xFF1A1A1A)
                            )
                        )
                    }
                }

                // Campo de cliente
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Cliente (opcional)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF666666),
                            fontWeight = FontWeight.Medium
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        TextField(
                    value = cliente,
                    onValueChange = { cliente = it },
                            placeholder = { Text("Nombre del cliente") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedIndicatorColor = Color(0xFF1976D2),
                                unfocusedIndicatorColor = Color(0xFFCCCCCC),
                                focusedTextColor = Color(0xFF1A1A1A),
                                unfocusedTextColor = Color(0xFF1A1A1A)
                            )
                        )
                    }
                }
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
                        }
                    }
                },
                enabled = selectedProducto.isNotBlank() && 
                         litrosVendidos.isNotBlank() && 
                         precioPorLitro.isNotBlank() &&
                         validarLitros(litrosVendidos) &&
                         validarPrecio(precioPorLitro),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Guardar Venta",
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
fun ModernEliminarVentaDialog(
    venta: VentaEntity,
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
                    text = "Eliminar Venta",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF1A1A1A),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = { 
            Column {
                Text(
                    text = "¿Estás seguro de que quieres eliminar esta venta?",
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
                            text = "Detalles de la venta:",
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
                                text = "Producto:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF666666)
                            )
                            Text(
                                text = venta.nombreProducto,
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
                                text = "Litros:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF666666)
                            )
                            Text(
                                text = "${venta.litrosVendidos} L",
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
                                text = "Precio:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF666666)
                            )
                            Text(
                                text = "$${String.format("%.2f", venta.precioPorLitro)}/L",
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
                                text = "Total:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF666666)
                            )
                            Text(
                                text = "$${String.format("%.2f", venta.litrosVendidos * venta.precioPorLitro)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF4CAF50),
                                fontWeight = FontWeight.Bold
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
                                text = dateFormat.format(Date(venta.fecha)),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF1A1A1A),
                                fontWeight = FontWeight.Medium
                            )
                        }
                        
                        venta.cliente?.let { cliente ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Cliente:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF666666)
                                )
                                Text(
                                    text = cliente,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF1A1A1A),
                                    fontWeight = FontWeight.Medium
                                )
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
