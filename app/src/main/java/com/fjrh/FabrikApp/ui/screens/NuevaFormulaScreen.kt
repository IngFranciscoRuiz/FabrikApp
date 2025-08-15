package com.fjrh.FabrikApp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fjrh.FabrikApp.domain.model.Ingrediente
import com.fjrh.FabrikApp.domain.model.Formula
import com.fjrh.FabrikApp.data.local.entity.IngredienteInventarioEntity
import com.fjrh.FabrikApp.ui.viewmodel.FormulaViewModel
import kotlinx.coroutines.launch

@Composable
fun NuevaFormulaScreen(
    viewModel: FormulaViewModel,
    navController: NavController,
    formulaParaEditar: com.fjrh.FabrikApp.data.local.entity.FormulaConIngredientes? = null
) {
    var nombreFormula by remember { mutableStateOf("") }
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
    
    // Pre-llenar campos si se está editando una fórmula
    LaunchedEffect(formulaParaEditar) {
        formulaParaEditar?.let { formula ->
            nombreFormula = formula.formula.nombre
            listaIngredientes.clear()
            listaIngredientes.addAll(
                formula.ingredientes.map { ingredienteEntity ->
                    Ingrediente(
                        nombre = ingredienteEntity.nombre,
                        cantidad = ingredienteEntity.cantidad,
                        unidad = ingredienteEntity.unidad,
                        costoPorUnidad = ingredienteEntity.costoPorUnidad
                    )
                }
            )
        } ?: run {
            nombreFormula = ""
            listaIngredientes.clear()
        }
    }

    // Escuchar evento de éxito del ViewModel
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is FormulaViewModel.UiEvent.FormulaGuardada -> {
                    snackbarHostState.showSnackbar("¡Fórmula guardada!")
                    navController.popBackStack()
                }
                FormulaViewModel.UiEvent.LoteProducido -> TODO()
                else -> {}
            }
        }
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
                .padding(bottom = 100.dp)
                .verticalScroll(rememberScrollState()),
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
                        .clickable { navController.popBackStack() }
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = if (formulaParaEditar != null) "Editar Fórmula" else "Nueva Fórmula",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFF1A1A1A),
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = Color(0xFF1976D2),
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Descripción
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (formulaParaEditar != null) "Editar Fórmula" else "Crear Nueva Fórmula",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF1A1A1A),
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Define los ingredientes y cantidades para tu producto",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF666666),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Formulario principal
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Campo Nombre
                    ModernFormField(
                        value = nombreFormula,
                        onValueChange = { nombreFormula = it },
                        label = "Nombre del producto",
                        icon = Icons.Default.Inventory,
                        color = Color(0xFF4CAF50)
                    )

                    Text(
                        text = "Insumos por litro",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )

                    // Selector de ingrediente
                    Box {
                        OutlinedTextField(
                            value = selectedIngrediente?.nombre ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Seleccionar insumo", color = Color(0xFF1A1A1A)) },
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
                                unfocusedBorderColor = Color(0xFFE0E0E0),
                                focusedTextColor = Color(0xFF1A1A1A),
                                unfocusedTextColor = Color(0xFF1A1A1A),
                                focusedLabelColor = Color(0xFF2196F3),
                                unfocusedLabelColor = Color(0xFF666666)
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

                    // Campo Cantidad
                    ModernFormField(
                        value = cantidad,
                        onValueChange = { cantidad = it },
                        label = "Cantidad por litro",
                        icon = Icons.Default.Scale,
                        color = Color(0xFFFF9800),
                        keyboardType = KeyboardType.Decimal
                    )

                    // Dropdown para unidad de medida
                    ModernDropdownField(
                        value = unidadSeleccionada,
                        onValueChange = { unidadSeleccionada = it },
                        label = "Unidad de medida",
                        icon = Icons.Default.Straighten,
                        color = Color(0xFF9C27B0),
                        options = unidades,
                        expanded = expandedUnidades,
                        onExpandedChange = { expandedUnidades = it }
                    )

                    // Información del ingrediente seleccionado
                    selectedIngrediente?.let { ingrediente ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Información del ingrediente",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1A1A1A)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Unidad: ${ingrediente.unidad}", color = Color(0xFF666666))
                                Text("Costo por unidad: $${String.format("%.2f", ingrediente.costoPorUnidad)}", color = Color(0xFF666666))
                                Text("Stock disponible: ${ingrediente.cantidadDisponible}", color = Color(0xFF666666))
                            }
                        }
                    }

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
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF944D2E)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Agregar ingrediente")
                    }
                }
            }

            // Lista de ingredientes agregados
            if (listaIngredientes.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Insumos añadidos",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A)
                        )

                        listaIngredientes.forEachIndexed { index, ingrediente ->
                            ModernIngredientCard(
                                ingrediente = ingrediente,
                                onDelete = { listaIngredientes.removeAt(index) }
                            )
                        }

                        // Costo total
                        val costoTotal = listaIngredientes.sumOf { ingrediente -> 
                            val cantidad = ingrediente.cantidad.toDoubleOrNull() ?: 0.0
                            val ingredienteInventario = ingredientesInventario.find { it.nombre == ingrediente.nombre }
                            val costoPorUnidadConvertido = if (ingredienteInventario != null) {
                                calcularCostoPorUnidad(ingredienteInventario, ingrediente.unidad)
                            } else {
                                ingrediente.costoPorUnidad
                            }
                            cantidad * costoPorUnidadConvertido
                        }
                        
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Costo total por litro",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF388E3C)
                                )
                                Text(
                                    text = "$${String.format("%.2f", costoTotal)}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = Color(0xFF388E3C),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Botón guardar
            Button(
                onClick = {
                    if (nombreFormula.isNotBlank() && listaIngredientes.isNotEmpty()) {
                        if (formulaParaEditar != null) {
                            viewModel.actualizarFormula(
                                formulaId = formulaParaEditar.formula.id,
                                nombre = nombreFormula,
                                ingredientes = listaIngredientes.toList()
                            )
                        } else {
                            viewModel.guardarFormula(
                                Formula(
                                    nombre = nombreFormula,
                                    ingredientes = listaIngredientes.toList()
                                )
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = nombreFormula.isNotBlank() && listaIngredientes.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (formulaParaEditar != null) "Actualizar Fórmula" else "Guardar Fórmula"
                )
            }
        }

        // Snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}

@Composable
fun ModernFormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    keyboardType: KeyboardType = KeyboardType.Text
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
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = keyboardType),
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

@Composable
fun ModernDropdownField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    options: List<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    Box {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, color = Color(0xFF1A1A1A)) },
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = { onExpandedChange(!expanded) },
                    enabled = enabled
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Seleccionar",
                        tint = color
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = enabled,
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

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueChange(option)
                        onExpandedChange(false)
                    }
                )
            }
        }
    }
}

@Composable
fun ModernIngredientCard(
    ingrediente: Ingrediente,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        shape = RoundedCornerShape(12.dp)
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
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    text = "${ingrediente.cantidad} ${ingrediente.unidad} - $${ingrediente.costoPorUnidad}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF666666)
                )
            }
            
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color(0xFFD32F2F)
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
