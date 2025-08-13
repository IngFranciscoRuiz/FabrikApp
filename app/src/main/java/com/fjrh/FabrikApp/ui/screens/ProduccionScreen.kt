package com.fjrh.FabrikApp.ui.screens

import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.fjrh.FabrikApp.data.local.entity.HistorialProduccionEntity
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.platform.LocalContext

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fjrh.FabrikApp.data.local.entity.FormulaConIngredientes
import com.fjrh.FabrikApp.ui.viewmodel.FormulaViewModel
import com.fjrh.FabrikApp.ui.utils.validarLitros

fun String.toFloatOrZero(): Float = this.toFloatOrNull() ?: 0f

@Composable
fun ProduccionScreen(
    formula: FormulaConIngredientes?,
    viewModel: FormulaViewModel
) {
    var litrosDeseados by remember { mutableStateOf("") }
    var selectedFormula by remember { mutableStateOf<FormulaConIngredientes?>(formula) }
    val listaFormulas by viewModel.formulas.collectAsState()
    val ingredientesInventario by viewModel.ingredientesInventario.collectAsState()
    val checkedItems = remember { mutableStateMapOf<Long, Boolean>() }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Escuchar evento de éxito al producir lote
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is FormulaViewModel.UiEvent.LoteProducido -> {
                    snackbarHostState.showSnackbar("¡Lote producido exitosamente!")
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
                    text = "Producción",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFF1A1A1A),
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Contador de fórmulas
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1976D2)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "${listaFormulas.size}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Selección de fórmula
            if (selectedFormula == null) {
                if (listaFormulas.isEmpty()) {
                    EmptyState(
                        icon = Icons.Default.Science,
                        title = "No hay fórmulas",
                        message = "No hay fórmulas registradas para producción."
                    )
                } else {
                    FormulaSelector(
                        formulas = listaFormulas,
                        onFormulaSelected = { selectedFormula = it }
                    )
                }
            }

            // Contenido de producción
            selectedFormula?.let { formula ->
                ProductionContent(
                    formula = formula,
                    litrosDeseados = litrosDeseados,
                    onLitrosChange = { litrosDeseados = it },
                    ingredientesInventario = ingredientesInventario,
                    checkedItems = checkedItems,
                    onProduce = {
                        scope.launch {
                            // Descontar ingredientes del inventario
                            formula.ingredientes.forEach { ingrediente ->
                                val cantidadCalculada = litrosDeseados.toFloatOrZero() * ingrediente.cantidad.toFloatOrZero()
                                val ingredienteInventario = ingredientesInventario.find { 
                                    it.nombre.equals(ingrediente.nombre, ignoreCase = true) 
                                }
                                
                                ingredienteInventario?.let { ingredienteEnInventario ->
                                    val cantidadADescontar = convertirUnidades(
                                        cantidad = cantidadCalculada,
                                        unidadOrigen = ingrediente.unidad,
                                        unidadDestino = ingredienteEnInventario.unidad
                                    )
                                    
                                    val nuevoStock = ingredienteEnInventario.cantidadDisponible - cantidadADescontar
                                    val ingredienteActualizado = ingredienteEnInventario.copy(
                                        cantidadDisponible = nuevoStock
                                    )
                                    viewModel.actualizarIngredienteInventario(ingredienteActualizado)
                                }
                            }
                            
                            // Registrar el historial de producción
                            val historial = HistorialProduccionEntity(
                                nombreFormula = formula.formula.nombre,
                                litrosProducidos = litrosDeseados.toFloatOrZero(),
                                fecha = System.currentTimeMillis()
                            )
                            viewModel.insertarHistorial(historial)
                            
                            snackbarHostState.showSnackbar("¡Lote producido exitosamente!")
                        }
                    }
                )
            }
        }
        
        // Snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp)
        )
    }
}

@Composable
fun EmptyState(
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

@Composable
fun FormulaSelector(
    formulas: List<FormulaConIngredientes>,
    onFormulaSelected: (FormulaConIngredientes) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Seleccionar Fórmula",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF1A1A1A),
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(formulas) { formula ->
                    FormulaOption(
                        formula = formula,
                        onClick = { onFormulaSelected(formula) }
                    )
                }
            }
        }
    }
}

@Composable
fun FormulaOption(
    formula: FormulaConIngredientes,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Science,
                contentDescription = null,
                tint = Color(0xFF1976D2),
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = formula.formula.nombre,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF1A1A1A),
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = "${formula.ingredientes.size} ingredientes",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF666666)
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFFCCCCCC),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun ProductionContent(
    formula: FormulaConIngredientes,
    litrosDeseados: String,
    onLitrosChange: (String) -> Unit,
    ingredientesInventario: List<com.fjrh.FabrikApp.data.local.entity.IngredienteInventarioEntity>,
    checkedItems: MutableMap<Long, Boolean>,
    onProduce: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Información de la fórmula
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = formula.formula.nombre,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF1A1A1A),
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Fórmula de producción",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF666666)
                )
            }
        }

        // Campo de litros
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Litros a producir",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF1A1A1A),
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                TextField(
                    value = litrosDeseados,
                    onValueChange = { 
                        if (validarLitros(it)) {
                            onLitrosChange(it)
                        }
                    },
                    placeholder = { Text("Ingresa la cantidad de litros") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF8F9FA),
                        unfocusedContainerColor = Color(0xFFF8F9FA),
                        focusedIndicatorColor = Color(0xFF1976D2),
                        unfocusedIndicatorColor = Color(0xFFCCCCCC)
                    )
                )
            }
        }

        val litros = litrosDeseados.toFloatOrZero()

        if (litros > 0f) {
            // Información de costos
            CostInfoCard(formula = formula, litros = litros)
            
            // Lista de ingredientes
            IngredientsListCard(
                formula = formula,
                litros = litros,
                ingredientesInventario = ingredientesInventario,
                checkedItems = checkedItems
            )
            
            // Botón de producción
            val todosIngredientesDisponibles = formula.ingredientes.all { ingrediente ->
                val cantidadCalculada = litros * ingrediente.cantidad.toFloatOrZero()
                val ingredienteInventario = ingredientesInventario.find { 
                    it.nombre.equals(ingrediente.nombre, ignoreCase = true) 
                }
                ingredienteInventario?.let { inventario ->
                    val stockDisponibleConvertido = convertirUnidades(
                        cantidad = inventario.cantidadDisponible,
                        unidadOrigen = inventario.unidad,
                        unidadDestino = ingrediente.unidad
                    )
                    stockDisponibleConvertido >= cantidadCalculada
                } ?: false
            }

            val allChecked = formula.ingredientes.all { checkedItems[it.id] == true }

            Button(
                onClick = { onProduce() },
                enabled = allChecked && todosIngredientesDisponibles,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Iniciar Producción",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
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
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFF1976D2),
                        modifier = Modifier.size(32.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "Ingresa los litros deseados para calcular cantidades",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF666666),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun CostInfoCard(
    formula: FormulaConIngredientes,
    litros: Float
) {
    // Calcular costo total por litro
    val costoPorLitro = formula.ingredientes.sumOf { ingrediente ->
        ingrediente.cantidad.toDoubleOrNull()?.let { cantidad ->
            cantidad * ingrediente.costoPorUnidad
        } ?: 0.0
    }
    
    // Calcular costo total de la producción
    val costoTotalProduccion = costoPorLitro * litros

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Información de Costos",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF1A1A1A),
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Costo por litro:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF666666)
                )
                Text(
                    text = "$${String.format("%.2f", costoPorLitro)}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF1A1A1A),
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Litros a producir:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF666666)
                )
                Text(
                    text = "$litros",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF1A1A1A),
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Divider(color = Color(0xFFE0E0E0))
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Costo total:",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF1A1A1A),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$${String.format("%.2f", costoTotalProduccion)}",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF1976D2),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun IngredientsListCard(
    formula: FormulaConIngredientes,
    litros: Float,
    ingredientesInventario: List<com.fjrh.FabrikApp.data.local.entity.IngredienteInventarioEntity>,
    checkedItems: MutableMap<Long, Boolean>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Insumos Necesarios",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF1A1A1A),
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Header de la tabla
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Insumo",
                    modifier = Modifier.weight(2f),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF666666),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Unidad",
                    modifier = Modifier.weight(0.8f),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF666666),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Cantidad",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF666666),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Stock",
                    modifier = Modifier.weight(0.5f),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF666666),
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Divider(color = Color(0xFFE0E0E0))
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(formula.ingredientes) { ingrediente ->
                    val cantidadCalculada = litros * ingrediente.cantidad.toFloatOrZero()
                    val isChecked = checkedItems[ingrediente.id] ?: false
                    
                    // Buscar el ingrediente en el inventario
                    val ingredienteInventario = ingredientesInventario.find { 
                        it.nombre.equals(ingrediente.nombre, ignoreCase = true) 
                    }
                    
                    // Convertir unidades para comparar correctamente
                    val stockDisponibleConvertido = ingredienteInventario?.let { inventario ->
                        convertirUnidades(
                            cantidad = inventario.cantidadDisponible,
                            unidadOrigen = inventario.unidad,
                            unidadDestino = ingrediente.unidad
                        )
                    } ?: 0f
                    
                    // Validar si hay suficiente stock
                    val hayStockSuficiente = stockDisponibleConvertido >= cantidadCalculada
                    
                    // Color según disponibilidad
                    val colorTexto = if (hayStockSuficiente) {
                        Color(0xFF1A1A1A)
                    } else {
                        Color(0xFFE57373)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = ingrediente.nombre, 
                            modifier = Modifier.weight(2f),
                            color = colorTexto,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = ingrediente.unidad, 
                            modifier = Modifier.weight(0.8f),
                            color = colorTexto,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "%.2f".format(cantidadCalculada), 
                            modifier = Modifier.weight(1f),
                            color = colorTexto,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (!hayStockSuficiente) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Stock insuficiente",
                                tint = Color(0xFFE57373),
                                modifier = Modifier
                                    .weight(0.5f)
                                    .size(16.dp)
                            )
                        } else {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { checkedItems[ingrediente.id] = it },
                                modifier = Modifier.weight(0.5f),
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(0xFF1976D2),
                                    uncheckedColor = Color(0xFFCCCCCC)
                                )
                            )
                        }
                    }
                    
                    // Mostrar información de stock si no hay suficiente
                    if (!hayStockSuficiente && ingredienteInventario != null) {
                        Text(
                            text = "Stock disponible: ${ingredienteInventario.cantidadDisponible} ${ingredienteInventario.unidad}",
                            color = Color(0xFFE57373),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

// Función para convertir unidades
private fun convertirUnidades(
    cantidad: Float,
    unidadOrigen: String,
    unidadDestino: String
): Float {
    return when {
        // Misma unidad
        unidadOrigen == unidadDestino -> cantidad
        
        // Conversión de Kg a gr (1 Kg = 1000 gr)
        unidadOrigen == "Kg" && unidadDestino == "gr" -> cantidad * 1000f
        
        // Conversión de gr a Kg (1000 gr = 1 Kg)
        unidadOrigen == "gr" && unidadDestino == "Kg" -> cantidad / 1000f
        
        // Conversión de L a ml (1 L = 1000 ml)
        unidadOrigen == "L" && unidadDestino == "ml" -> cantidad * 1000f
        
        // Conversión de ml a L (1000 ml = 1 L)
        unidadOrigen == "ml" && unidadDestino == "L" -> cantidad / 1000f
        
        // Para Pzas, mantener la misma cantidad
        unidadDestino == "Pzas" -> cantidad
        
        // Si no hay conversión definida, usar la cantidad original
        else -> cantidad
    }
}
