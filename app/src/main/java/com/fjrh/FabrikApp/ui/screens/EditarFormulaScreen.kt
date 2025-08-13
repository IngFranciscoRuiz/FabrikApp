package com.fjrh.FabrikApp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.navigation.NavController
import com.fjrh.FabrikApp.domain.model.Ingrediente
import com.fjrh.FabrikApp.domain.model.Formula
import com.fjrh.FabrikApp.ui.viewmodel.FormulaViewModel
import com.fjrh.FabrikApp.data.local.entity.IngredienteInventarioEntity
import com.fjrh.FabrikApp.data.local.entity.FormulaConIngredientes
import kotlinx.coroutines.launch

@Composable
fun EditarFormulaScreen(
    viewModel: FormulaViewModel,
    navController: NavController,
    formula: FormulaConIngredientes
) {
    var nombreFormula by remember { mutableStateOf(formula.formula.nombre) }
    var selectedIngrediente by remember { mutableStateOf<IngredienteInventarioEntity?>(null) }
    var cantidad by remember { mutableStateOf("") }
    var expandedIngredientes by remember { mutableStateOf(false) }
    var unidadSeleccionada by remember { mutableStateOf("") }
    var expandedUnidades by remember { mutableStateOf(false) }
    
    val listaIngredientes = remember { mutableStateListOf<Ingrediente>() }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    val ingredientesInventario by viewModel.ingredientesInventario.collectAsState()
    
    // Lista de unidades disponibles
    val unidades = listOf("gr", "Kg", "ml", "L", "Pzas")
    
    // Cargar ingredientes existentes
    LaunchedEffect(formula) {
        listaIngredientes.clear()
        listaIngredientes.addAll(formula.ingredientes.map { ingredienteEntity ->
            Ingrediente(
                nombre = ingredienteEntity.nombre,
                unidad = ingredienteEntity.unidad,
                cantidad = ingredienteEntity.cantidad,
                costoPorUnidad = ingredienteEntity.costoPorUnidad
            )
        })
    }

    // Escuchar evento de éxito del ViewModel
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is FormulaViewModel.UiEvent.FormulaGuardada -> {
                    snackbarHostState.showSnackbar("¡Fórmula actualizada!")
                    navController.popBackStack()
                }
                else -> {}
            }
        }
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
                .verticalScroll(rememberScrollState())
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
                        text = "Editar Fórmula",
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
                        text = "${listaIngredientes.size}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Descripción
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
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = Color(0xFF1976D2),
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Text(
                        text = "Modifica los ingredientes y cantidades de tu fórmula",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF666666)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Campo nombre del producto
            ModernFormField(
                value = nombreFormula,
                onValueChange = { nombreFormula = it },
                label = "Nombre del producto",
                icon = Icons.Default.Description,
                color = Color(0xFF1976D2)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Sección de ingredientes
            Text(
                text = "Insumos por litro",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF1A1A1A),
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Selector de ingrediente
            Box {
                OutlinedTextField(
                    value = selectedIngrediente?.nombre ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Seleccionar insumo") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = Color(0xFF2196F3)
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = { expandedIngredientes = !expandedIngredientes },
                            enabled = ingredientesInventario.isNotEmpty()
                        ) {
                            Icon(
                                imageVector = if (expandedIngredientes) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Seleccionar",
                                tint = Color(0xFF2196F3)
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = ingredientesInventario.isNotEmpty(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2196F3),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                
                DropdownMenu(
                    expanded = expandedIngredientes,
                    onDismissRequest = { expandedIngredientes = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (ingredientesInventario.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("No hay insumos en inventario") },
                            onClick = { }
                        )
                    } else {
                        ingredientesInventario.forEach { ingrediente ->
                            DropdownMenuItem(
                                text = { 
                                    Text("${ingrediente.nombre} - ${ingrediente.unidad} - $${ingrediente.costoPorUnidad}")
                                },
                                onClick = {
                                    selectedIngrediente = ingrediente
                                    expandedIngredientes = false
                                }
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Campo cantidad
            ModernFormField(
                value = cantidad,
                onValueChange = { cantidad = it },
                label = "Cantidad por litro",
                icon = Icons.Default.Scale,
                color = Color(0xFF4CAF50)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Selector de unidad
            Box {
                OutlinedTextField(
                    value = unidadSeleccionada,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Unidad de medida") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Straighten,
                            contentDescription = null,
                            tint = Color(0xFFFF9800)
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { expandedUnidades = !expandedUnidades }) {
                            Icon(
                                imageVector = if (expandedUnidades) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Expandir unidades",
                                tint = Color(0xFFFF9800)
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF9800),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                
                DropdownMenu(
                    expanded = expandedUnidades,
                    onDismissRequest = { expandedUnidades = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    unidades.forEach { unidad ->
                        DropdownMenuItem(
                            text = { Text(unidad) },
                            onClick = {
                                unidadSeleccionada = unidad
                                expandedUnidades = false
                            }
                        )
                    }
                }
            }
            
            // Información del ingrediente seleccionado
            selectedIngrediente?.let { ingrediente ->
                Spacer(modifier = Modifier.height(12.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Información del insumo",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color(0xFF1976D2),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Unidad: ${ingrediente.unidad}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF666666)
                            )
                            Text(
                                text = "Stock: ${ingrediente.cantidadDisponible}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF666666)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Costo: $${String.format("%.2f", ingrediente.costoPorUnidad)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF1976D2),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Botón agregar ingrediente
            Button(
                onClick = {
                    if (selectedIngrediente != null && cantidad.isNotBlank() && unidadSeleccionada.isNotBlank()) {
                        val costoCalculado = calcularCostoPorUnidad(
                            ingrediente = selectedIngrediente!!,
                            unidadSeleccionada = unidadSeleccionada
                        )
                        
                        listaIngredientes.add(
                            Ingrediente(
                                nombre = selectedIngrediente!!.nombre,
                                unidad = unidadSeleccionada,
                                cantidad = cantidad,
                                costoPorUnidad = costoCalculado
                            )
                        )
                        selectedIngrediente = null
                        cantidad = ""
                        unidadSeleccionada = ""
                    }
                },
                enabled = selectedIngrediente != null && cantidad.isNotBlank() && unidadSeleccionada.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Agregar insumo", color = Color.White, fontWeight = FontWeight.Medium)
            }
            
            // Lista de ingredientes agregados
            if (listaIngredientes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                
                Text(
                    text = "Insumos agregados",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF1A1A1A),
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(listaIngredientes) { index, ingrediente ->
                        EditIngredientCard(
                            ingrediente = ingrediente,
                            onDelete = { listaIngredientes.removeAt(index) }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Costo total
                val costoTotal = listaIngredientes.sumOf { 
                    it.cantidad.toDoubleOrNull()?.let { cantidad -> 
                        cantidad * it.costoPorUnidad 
                    } ?: 0.0 
                }
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50)),
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
                            text = "Costo total por litro",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$${String.format("%.2f", costoTotal)}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Botón actualizar fórmula
                Button(
                    onClick = {
                        if (nombreFormula.isNotBlank() && listaIngredientes.isNotEmpty()) {
                            viewModel.actualizarFormula(
                                formulaId = formula.formula.id,
                                nombre = nombreFormula,
                                ingredientes = listaIngredientes.toList()
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Actualizar fórmula", color = Color.White, fontWeight = FontWeight.Medium)
                }
            }
        }
        
        // Snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun ModernFormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
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
            unfocusedBorderColor = Color(0xFFE0E0E0)
        ),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun EditIngredientCard(
    ingrediente: Ingrediente,
    onDelete: () -> Unit
) {
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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = ingrediente.nombre,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color(0xFF1A1A1A),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${ingrediente.cantidad} ${ingrediente.unidad} - $${String.format("%.2f", ingrediente.costoPorUnidad)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF666666)
                )
            }
            
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = Color(0xFFFFEBEE),
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color(0xFFF44336),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// Función para calcular el costo por unidad basado en la conversión
private fun calcularCostoPorUnidad(
    ingrediente: IngredienteInventarioEntity,
    unidadSeleccionada: String
): Double {
    val costoOriginal = ingrediente.costoPorUnidad
    val unidadOriginal = ingrediente.unidad
    
    return when {
        // Misma unidad, mismo costo
        unidadOriginal == unidadSeleccionada -> costoOriginal
        
        // Conversión de Kg a gr (1 Kg = 1000 gr)
        unidadOriginal == "Kg" && unidadSeleccionada == "gr" -> costoOriginal / 1000.0
        
        // Conversión de gr a Kg (1000 gr = 1 Kg)
        unidadOriginal == "gr" && unidadSeleccionada == "Kg" -> costoOriginal * 1000.0
        
        // Conversión de L a ml (1 L = 1000 ml)
        unidadOriginal == "L" && unidadSeleccionada == "ml" -> costoOriginal / 1000.0
        
        // Conversión de ml a L (1000 ml = 1 L)
        unidadOriginal == "ml" && unidadSeleccionada == "L" -> costoOriginal * 1000.0
        
        // Para Pzas, mantener el mismo costo
        unidadSeleccionada == "Pzas" -> costoOriginal
        
        // Si no hay conversión definida, usar el costo original
        else -> costoOriginal
    }
} 
