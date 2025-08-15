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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
            .background(MaterialTheme.colorScheme.background)
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
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { navController.popBackStack() }
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = "Fórmulas",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
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
                        placeholder = { Text("Buscar fórmulas...") },
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

            // Lista de fórmulas
            if (formulasFiltradas.isEmpty()) {
                FormulasEmptyState(
                    icon = Icons.Default.Science,
                    title = if (searchQuery.isBlank()) "No hay fórmulas" else "No se encontraron fórmulas",
                    message = if (searchQuery.isBlank()) 
                        "No hay fórmulas registradas aún. Crea tu primera fórmula." 
                    else 
                        "No se encontraron fórmulas que coincidan con '$searchQuery'"
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(formulasFiltradas) { formula ->
                        ModernFormulaCard(
                            formula = formula,
                            onEdit = { 
                                println("DEBUG: === INICIO EDICIÓN FÓRMULA ===")
                                println("DEBUG: Fórmula original - ID: ${formula.formula.id}, Nombre: ${formula.formula.nombre}")
                                println("DEBUG: Cantidad de ingredientes original: ${formula.ingredientes.size}")
                                formula.ingredientes.forEach { ingrediente ->
                                    println("DEBUG: Ingrediente original: ${ingrediente.nombre} - ${ingrediente.cantidad} ${ingrediente.unidad} - $${ingrediente.costoPorUnidad}")
                                }
                                
                                val formulaSerializable = formula.toSerializable()
                                println("DEBUG: Fórmula serializable - ID: ${formulaSerializable.id}, Nombre: ${formulaSerializable.nombre}")
                                println("DEBUG: Cantidad de ingredientes serializable: ${formulaSerializable.ingredientes.size}")
                                
                                val json = Gson().toJson(formulaSerializable)
                                val encoded = Uri.encode(json)
                                println("DEBUG: Longitud del JSON: ${json.length}")
                                println("DEBUG: Longitud del JSON codificado: ${encoded.length}")
                                println("DEBUG: JSON generado: $json")
                                println("DEBUG: JSON codificado: $encoded")
                                println("DEBUG: === FIN EDICIÓN FÓRMULA ===")
                                
                                // Verificar si el JSON es demasiado largo
                                if (encoded.length > 1000) {
                                    println("DEBUG: ⚠️ JSON muy largo, puede causar problemas")
                                }
                                
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
        
        // FAB para agregar nueva fórmula
        FloatingActionButton(
            onClick = { navController.navigate("nueva_formula") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 100.dp, end = 20.dp),
            containerColor = Color(0xFF1976D2),
            contentColor = Color.White
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Agregar fórmula",
                modifier = Modifier.size(24.dp)
            )
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
fun FormulasEmptyState(
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
fun ModernFormulaCard(
    formula: FormulaConIngredientes,
    onEdit: () -> Unit,
    onProduccion: () -> Unit,
    onDelete: (FormulaEntity) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header de la tarjeta
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF1A1A1A),
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "${formula.ingredientes.size} ingredientes",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF666666)
                    )
                }
                
                IconButton(
                    onClick = { expanded = !expanded }
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Contraer" else "Expandir",
                        tint = Color(0xFF666666)
                    )
                }
            }

            // Contenido expandible
            if (expanded) {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Tabla de ingredientes
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
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
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        HorizontalDivider(color = Color(0xFFE0E0E0))
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Lista de ingredientes
                        formula.ingredientes.forEach { ingrediente ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = ingrediente.nombre,
                                    modifier = Modifier.weight(2f),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF1A1A1A),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = ingrediente.unidad,
                                    modifier = Modifier.weight(0.8f),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF1A1A1A)
                                )
                                Text(
                                    text = ingrediente.cantidad.toString(),
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF1A1A1A),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            
                            if (ingrediente != formula.ingredientes.last()) {
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Botones de acción
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onProduccion,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Producir",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1
                        )
                    }
                    
                    Button(
                        onClick = {
                            println("DEBUG: Botón Editar presionado para fórmula: ${formula.formula.nombre}")
                            onEdit()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Editar",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1
                        )
                    }
                    
                    Button(
                        onClick = { showDialog = true },
                        modifier = Modifier.weight(1f),
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
                            text = "Eliminar",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }

    // Diálogo de confirmación
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { 
                Text(
                    text = "Confirmar eliminación",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF1A1A1A),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "¿Estás seguro de que deseas eliminar la fórmula '${formula.formula.nombre}'? Esta acción no se puede deshacer.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF666666)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(formula.formula)
                        showDialog = false
                    }
                ) {
                    Text(
                        text = "Eliminar",
                        color = Color(0xFFD32F2F),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
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
}
