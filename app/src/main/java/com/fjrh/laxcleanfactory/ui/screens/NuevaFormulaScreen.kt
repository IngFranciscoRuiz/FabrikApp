package com.fjrh.laxcleanfactory.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fjrh.laxcleanfactory.domain.model.Ingrediente
import com.fjrh.laxcleanfactory.domain.model.Formula
import com.fjrh.laxcleanfactory.ui.viewmodel.FormulaViewModel
import com.fjrh.laxcleanfactory.data.local.entity.IngredienteInventarioEntity
import kotlinx.coroutines.launch

@Composable
fun NuevaFormulaScreen(
    viewModel: FormulaViewModel,
    navController: NavController
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

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Fórmula nueva",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = nombreFormula,
                onValueChange = { nombreFormula = it },
                label = { Text("Nombre del producto") },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "INSUMOS POR LITRO:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Dropdown para seleccionar ingrediente del inventario
            var expanded by remember { mutableStateOf(false) }
            
            // Debug: mostrar cantidad de ingredientes
            Text("Insumos disponibles: ${ingredientesInventario.size}")
            
            Box {
                OutlinedTextField(
                    value = selectedIngrediente?.nombre ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Seleccionar insumo") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(
                                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Expandir"
                            )
                        }
                    }
                )
                
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
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
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = cantidad,
                onValueChange = { cantidad = it },
                label = { Text("Cantidad por litro") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Dropdown para seleccionar unidad de medida
            Box {
                OutlinedTextField(
                    value = unidadSeleccionada,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Unidad de medida") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { expandedUnidades = !expandedUnidades }) {
                            Icon(
                                imageVector = if (expandedUnidades) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Expandir unidades"
                            )
                        }
                    }
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

            // Mostrar información del ingrediente seleccionado
            selectedIngrediente?.let { ingrediente ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "Información del ingrediente:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Unidad: ${ingrediente.unidad}")
                        Text("Costo por unidad: $${String.format("%.2f", ingrediente.costoPorUnidad)}")
                        Text("Stock disponible: ${ingrediente.cantidadDisponible}")
                    }
                }
            }

            Button(
                onClick = {
                    if (selectedIngrediente != null && cantidad.isNotBlank() && unidadSeleccionada.isNotBlank()) {
                        // Calcular el costo basado en la unidad seleccionada
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
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF944D2E))
            ) {
                Text("Agregar ingrediente", color = Color.White)
            }

            if (listaIngredientes.isNotEmpty()) {
                Text(
                    text = "Insumos añadidos:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                listaIngredientes.forEachIndexed { index, ingrediente ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${ingrediente.nombre} - ${ingrediente.cantidad} ${ingrediente.unidad} - $${ingrediente.costoPorUnidad}",
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { listaIngredientes.removeAt(index) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                        }
                    }
                }

                // Mostrar costo total
                val costoTotal = listaIngredientes.sumOf { 
                    it.cantidad.toDoubleOrNull()?.let { cantidad -> 
                        cantidad * it.costoPorUnidad 
                    } ?: 0.0 
                }
                
                if (listaIngredientes.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Costo total por litro:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$${String.format("%.2f", costoTotal)}",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color(0xFF944D2E),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Button(
                onClick = {
                    if (nombreFormula.isNotBlank() && listaIngredientes.isNotEmpty()) {
                        viewModel.guardarFormula(
                            Formula(
                                nombre = nombreFormula,
                                ingredientes = listaIngredientes.toList()
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFE8DC))
            ) {
                Text("Guardar fórmula", color = Color(0xFF944D2E))
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
