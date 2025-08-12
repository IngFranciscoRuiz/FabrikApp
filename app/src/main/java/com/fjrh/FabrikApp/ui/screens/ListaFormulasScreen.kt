package com.fjrh.FabrikApp.ui.screens

import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fjrh.FabrikApp.data.local.entity.FormulaConIngredientes
import com.fjrh.FabrikApp.data.local.entity.FormulaEntity
import com.fjrh.FabrikApp.ui.viewmodel.FormulaViewModel
import com.google.gson.Gson
import kotlinx.coroutines.launch
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ListaFormulasScreen(
    viewModel: FormulaViewModel = hiltViewModel(),
    navController: NavController
) {
    val listaFormulas by viewModel.formulas.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    
    val formulasFiltradas = listaFormulas.filter { formula ->
        formula.formula.nombre.contains(searchQuery, ignoreCase = true)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF000000), Color(0xFF0D1A2F))
                )
            )
            .padding(16.dp)
    ) {
        // Host del Snackbar
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))

        Column {
            // Campo de búsqueda
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar fórmulas...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            if (formulasFiltradas.isEmpty()) {
                Text(
                    text = if (searchQuery.isBlank()) "No hay fórmulas registradas aún" else "No se encontraron fórmulas",
                    color = Color.White,
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn {
                    items(formulasFiltradas) { formula ->
                        FormulaAccordionCard(
                            formula = formula,
                            onEdit = { 
                                val json = Gson().toJson(formula)
                                val encoded = Uri.encode(json)
                                navController.navigate("editar_formula/$encoded")
                            },
                            onProduccion = {
                                val json = Gson().toJson(formula)
                                val encoded = Uri.encode(json)
                                navController.navigate("produccion/$encoded")
                            },
                            onDelete = { formulaEntity ->
                                viewModel.eliminarFormula(formulaEntity)
                                scope.launch {
                                    snackbarHostState.showSnackbar("Fórmula eliminada")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun FormulaAccordionCard(
    formula: FormulaConIngredientes,
    onEdit: () -> Unit,
    onProduccion: () -> Unit,
    onDelete: (FormulaEntity) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    val cardColor = Color(0xFFEAF0F6)
    val textColor = Color(0xFF1C2A3A)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .padding(bottom = 12.dp)
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = formula.formula.nombre,
                    fontSize = 18.sp,
                    color = textColor
                )
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = "Expandir",
                        tint = textColor
                    )
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))

                // Tabla de ingredientes
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text("Insumo", modifier = Modifier.weight(1f), color = textColor)
                        Text("Unidad", modifier = Modifier.weight(1f), color = textColor)
                        Text("Cantidad", modifier = Modifier.weight(1f), color = textColor)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    formula.ingredientes.forEach {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(it.nombre, modifier = Modifier.weight(1f), color = textColor)
                            Text(it.unidad, modifier = Modifier.weight(1f), color = textColor)
                            Text(it.cantidad.toString(), modifier = Modifier.weight(1f), color = textColor)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = onProduccion,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Text("Producción", color = Color.White)
                    }
                    Button(
                        onClick = onEdit,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                    ) {
                        Text("Editar", color = Color.White)
                    }
                    Button(
                        onClick = { showDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                    ) {
                        Text("Eliminar", color = Color.White)
                    }
                }
            }
        }
    }

    // Diálogo de confirmación
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmar eliminación") },
            text = {
                Text("¿Estás seguro de que deseas eliminar la fórmula '${formula.formula.nombre}'? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(formula.formula)
                    showDialog = false
                }) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
