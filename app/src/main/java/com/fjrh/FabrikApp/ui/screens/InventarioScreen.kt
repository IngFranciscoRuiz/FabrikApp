package com.fjrh.FabrikApp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.fjrh.FabrikApp.data.local.entity.IngredienteInventarioEntity
import com.fjrh.FabrikApp.ui.viewmodel.InventarioViewModel
import com.fjrh.FabrikApp.ui.components.TarjetaIngrediente
import com.fjrh.FabrikApp.data.local.ConfiguracionDataStore
import com.fjrh.FabrikApp.domain.model.ConfiguracionStock

@Composable
fun InventarioScreen(
    viewModel: InventarioViewModel,
    onAgregarClicked: () -> Unit
) {
    val ingredientes by viewModel.ingredientes.collectAsState(initial = emptyList<IngredienteInventarioEntity>())
    val configuracion by viewModel.configuracion.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    
    val ingredientesFiltrados = ingredientes.filter { ingrediente ->
        ingrediente.nombre.contains(searchQuery, ignoreCase = true) ||
        ingrediente.proveedor?.contains(searchQuery, ignoreCase = true) == true
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
                .padding(bottom = 100.dp)
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
                    text = "Inventario",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFF1A1A1A),
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Contador de ingredientes
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1976D2)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "${ingredientes.size}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

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
                        contentDescription = "Buscar",
                        tint = Color(0xFF666666),
                        modifier = Modifier.size(20.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Buscar ingredientes...") },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        textStyle = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Estadísticas rápidas
            if (ingredientes.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Stock Bajo",
                        value = ingredientes.count { it.cantidadDisponible < 10 }.toString(),
                        color = Color(0xFFFF5722),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Stock OK",
                        value = ingredientes.count { it.cantidadDisponible >= 10 }.toString(),
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Lista de ingredientes
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF1976D2)
                    )
                }
            } else if (ingredientesFiltrados.isEmpty()) {
                EmptyState(
                    message = if (searchQuery.isBlank()) 
                        "No hay ingredientes en inventario" 
                    else 
                        "No se encontraron ingredientes"
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(ingredientesFiltrados) { ingrediente ->
                        ModernIngredientCard(
                            ingrediente = ingrediente,
                            onDelete = { viewModel.eliminarIngrediente(ingrediente) },
                            onEdit = { ingredienteEditado -> viewModel.actualizarIngrediente(ingredienteEditado) },
                            configuracion = configuracion
                        )
                    }
                }
            }
        }
        
        // FAB moderno
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 100.dp, end = 20.dp)
        ) {
            FloatingActionButton(
                onClick = { onAgregarClicked() },
                containerColor = Color(0xFF1976D2),
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar ingrediente",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = color,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF666666),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun EmptyState(message: String) {
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
                imageVector = Icons.Default.Inventory,
                contentDescription = null,
                tint = Color(0xFFCCCCCC),
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF666666),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ModernIngredientCard(
    ingrediente: IngredienteInventarioEntity,
    onDelete: () -> Unit,
    onEdit: (IngredienteInventarioEntity) -> Unit,
    configuracion: ConfiguracionStock? = null
) {
    var isEditing by remember { mutableStateOf(false) }
    var editedIngrediente by remember { mutableStateOf(ingrediente) }
    
    val stockAlertService = remember { com.fjrh.FabrikApp.domain.service.StockAlertService() }
    
    val colorSemaforo = if (configuracion != null) {
        stockAlertService.getStockColorInsumo(ingrediente.cantidadDisponible, configuracion)
    } else {
        when {
            ingrediente.cantidadDisponible <= 0 -> Color(0xFFE57373)
            ingrediente.cantidadDisponible < 10 -> Color(0xFFFFB74D)
            ingrediente.cantidadDisponible < 50 -> Color(0xFFFFF176)
            else -> Color(0xFF81C784)
        }
    }
    
    val textoStock = if (configuracion != null) {
        stockAlertService.getStockTextInsumo(ingrediente.cantidadDisponible, configuracion)
    } else {
        when {
            ingrediente.cantidadDisponible <= 0 -> "SIN STOCK"
            ingrediente.cantidadDisponible < 10 -> "STOCK BAJO"
            ingrediente.cantidadDisponible < 50 -> "STOCK MEDIO"
            else -> "STOCK OK"
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header con nombre y estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = ingrediente.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF1A1A1A),
                        fontWeight = FontWeight.Bold
                    )
                    
                    if (ingrediente.proveedor != null) {
                        Text(
                            text = "Proveedor: ${ingrediente.proveedor}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF666666)
                        )
                    }
                }
                
                // Indicador de stock
                Card(
                    colors = CardDefaults.cardColors(containerColor = colorSemaforo.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(colorSemaforo, RoundedCornerShape(4.dp))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = textoStock,
                            style = MaterialTheme.typography.bodySmall,
                            color = colorSemaforo,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Información de stock
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Cantidad disponible",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF666666)
                    )
                    Text(
                        text = "${ingrediente.cantidadDisponible} ${ingrediente.unidad}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF1A1A1A),
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Precio unitario",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF666666)
                    )
                    Text(
                        text = "$${ingrediente.costoPorUnidad}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF1A1A1A),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Acciones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = { isEditing = !isEditing }
                ) {
                    Icon(
                        imageVector = if (isEditing) Icons.Default.Cancel else Icons.Default.Edit,
                        contentDescription = if (isEditing) "Cancelar" else "Editar",
                        tint = Color(0xFF1976D2),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isEditing) "Cancelar" else "Editar",
                        color = Color(0xFF1976D2),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                TextButton(
                    onClick = { onDelete() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = Color(0xFFE57373),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Eliminar",
                        color = Color(0xFFE57373),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
