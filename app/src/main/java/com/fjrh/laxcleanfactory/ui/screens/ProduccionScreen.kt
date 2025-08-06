package com.fjrh.laxcleanfactory.ui.screens

import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.fjrh.laxcleanfactory.data.local.entity.HistorialProduccionEntity
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.platform.LocalContext

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fjrh.laxcleanfactory.data.local.entity.FormulaConIngredientes
import com.fjrh.laxcleanfactory.ui.viewmodel.FormulaViewModel
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.style.TextOverflow
import com.fjrh.laxcleanfactory.ui.utils.validarLitros


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

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (selectedFormula == null) {
                if (listaFormulas.isEmpty()) {
                    Text("⚠️ No hay fórmulas registradas.")
                } else {
                    Text("Selecciona una fórmula para calcular producción:")
                    var expanded by remember { mutableStateOf(false) }

                    Box {
                        OutlinedButton(onClick = { expanded = true }) {
                            Text("Seleccionar fórmula")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            listaFormulas.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item.formula.nombre) },
                                    onClick = {
                                        selectedFormula = item
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            selectedFormula?.let { formula ->
                Text(
                    text = "Producción: ${formula.formula.nombre}",
                    style = MaterialTheme.typography.headlineSmall
                )

                OutlinedTextField(
                    value = litrosDeseados,
                    onValueChange = { 
                        if (validarLitros(it)) {
                            litrosDeseados = it
                        }
                    },
                    label = { Text("Litros a producir") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = litrosDeseados.isNotBlank() && !validarLitros(litrosDeseados),
                    supportingText = {
                        if (litrosDeseados.isNotBlank() && !validarLitros(litrosDeseados)) {
                            Text("Máximo 6 dígitos enteros y 3 decimales")
                        }
                    }
                )

                val litros = litrosDeseados.toFloatOrZero()

                if (litros > 0f) {
                    // Calcular costo total por litro
                    val costoPorLitro = formula.ingredientes.sumOf { ingrediente ->
                        ingrediente.cantidad.toDoubleOrNull()?.let { cantidad ->
                            cantidad * ingrediente.costoPorUnidad
                        } ?: 0.0
                    }
                    
                    // Calcular costo total de la producción
                    val costoTotalProduccion = costoPorLitro * litros

                    // Mostrar información de costos
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Información de Costos",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Costo por litro:")
                                Text("$${String.format("%.2f", costoPorLitro)}")
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Litros a producir:")
                                Text("$litros")
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Costo total:",
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                )
                                Text(
                                    "$${String.format("%.2f", costoTotalProduccion)}",
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("Insumos necesarios:", style = MaterialTheme.typography.titleMedium)

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Insumo", modifier = Modifier.weight(2f), style = MaterialTheme.typography.titleSmall)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Unidad", modifier = Modifier.weight(0.8f), style = MaterialTheme.typography.titleSmall)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Cantidad", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("✔️", modifier = Modifier.weight(0.3f), style = MaterialTheme.typography.titleSmall)
                            }
                        }

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
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.error
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
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = ingrediente.unidad, 
                                    modifier = Modifier.weight(0.8f),
                                    color = colorTexto,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "%.2f".format(cantidadCalculada), 
                                    modifier = Modifier.weight(1f),
                                    color = colorTexto,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                if (!hayStockSuficiente) {
                                    Text(
                                        text = "⚠️", 
                                        modifier = Modifier.weight(0.3f),
                                        color = MaterialTheme.colorScheme.error
                                    )
                                } else {
                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = { checkedItems[ingrediente.id] = it },
                                        modifier = Modifier.weight(0.3f)
                                    )
                                }
                            }
                            
                            // Mostrar información de stock si no hay suficiente
                            if (!hayStockSuficiente && ingredienteInventario != null) {
                                Text(
                                    text = "Stock disponible: ${ingredienteInventario.cantidadDisponible} ${ingredienteInventario.unidad} (${String.format("%.2f", stockDisponibleConvertido)} ${ingrediente.unidad})",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    // Validar que todos los ingredientes tengan stock suficiente
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
                        onClick = {
                            scope.launch {
                                // Descontar ingredientes del inventario
                                formula.ingredientes.forEach { ingrediente ->
                                    val cantidadCalculada = litros * ingrediente.cantidad.toFloatOrZero()
                                    val ingredienteInventario = ingredientesInventario.find { 
                                        it.nombre.equals(ingrediente.nombre, ignoreCase = true) 
                                    }
                                    
                                    ingredienteInventario?.let { ingredienteEnInventario ->
                                        // Convertir la cantidad calculada a la unidad del inventario
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
                                    litrosProducidos = litros,
                                    fecha = System.currentTimeMillis()
                                )
                                viewModel.insertarHistorial(historial)
                                
                                // Mostrar mensaje de éxito
                                snackbarHostState.showSnackbar("¡Lote producido y stock actualizado!")
                            }
                        },
                        enabled = allChecked && todosIngredientesDisponibles,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Lote realizado")
                    }

                } else {
                    Text(
                        text = "Ingresa los litros deseados para calcular cantidades.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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
