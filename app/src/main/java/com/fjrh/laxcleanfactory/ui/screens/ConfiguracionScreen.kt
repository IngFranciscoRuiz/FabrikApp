package com.fjrh.laxcleanfactory.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.fjrh.laxcleanfactory.domain.model.ConfiguracionStock
import com.fjrh.laxcleanfactory.ui.viewmodel.ConfiguracionViewModel
import com.fjrh.laxcleanfactory.ui.utils.validarPrecio

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracionScreen(
    navController: NavController,
    viewModel: ConfiguracionViewModel = hiltViewModel()
) {
    var stockAltoProductos by remember { mutableStateOf("") }
    var stockMedioProductos by remember { mutableStateOf("") }
        var stockBajoProductos by remember { mutableStateOf("") }

    var stockAltoInsumos by remember { mutableStateOf("") }
    var stockMedioInsumos by remember { mutableStateOf("") }
    var stockBajoInsumos by remember { mutableStateOf("") }
    var showSaveDialog by remember { mutableStateOf(false) }

    // Cargar configuración actual
    LaunchedEffect(Unit) {
                        viewModel.configuracion.collect { config ->
                    stockAltoProductos = config.stockAltoProductos.toString()
                    stockMedioProductos = config.stockMedioProductos.toString()
                    stockBajoProductos = config.stockBajoProductos.toString()

                    stockAltoInsumos = config.stockAltoInsumos.toString()
                    stockMedioInsumos = config.stockMedioInsumos.toString()
                    stockBajoInsumos = config.stockBajoInsumos.toString()
                }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { showSaveDialog = true }) {
                        Icon(Icons.Default.Save, contentDescription = "Guardar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Configuración de Stock",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            // PRODUCTOS TERMINADOS
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "🏭 PRODUCTOS TERMINADOS",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = stockAltoProductos,
                        onValueChange = { 
                            if (validarPrecio(it)) {
                                stockAltoProductos = it
                            }
                        },
                        label = { Text("Stock Alto (L)") },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        isError = stockAltoProductos.isNotBlank() && !validarPrecio(stockAltoProductos),
                        supportingText = {
                            if (stockAltoProductos.isNotBlank() && !validarPrecio(stockAltoProductos)) {
                                Text("Máximo 6 dígitos enteros y 2 decimales")
                            }
                        }
                    )

                    OutlinedTextField(
                        value = stockMedioProductos,
                        onValueChange = { 
                            if (validarPrecio(it)) {
                                stockMedioProductos = it
                            }
                        },
                        label = { Text("Stock Medio (L)") },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        isError = stockMedioProductos.isNotBlank() && !validarPrecio(stockMedioProductos),
                        supportingText = {
                            if (stockMedioProductos.isNotBlank() && !validarPrecio(stockMedioProductos)) {
                                Text("Máximo 6 dígitos enteros y 2 decimales")
                            }
                        }
                    )

                    OutlinedTextField(
                        value = stockBajoProductos,
                        onValueChange = { 
                            if (validarPrecio(it)) {
                                stockBajoProductos = it
                            }
                        },
                        label = { Text("Stock Bajo (L)") },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        isError = stockBajoProductos.isNotBlank() && !validarPrecio(stockBajoProductos),
                        supportingText = {
                            if (stockBajoProductos.isNotBlank() && !validarPrecio(stockBajoProductos)) {
                                Text("Máximo 6 dígitos enteros y 2 decimales")
                            }
                        }
                    )


                }
            }

            // INSUMOS
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "🧪 INSUMOS",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = stockAltoInsumos,
                        onValueChange = { 
                            if (validarPrecio(it)) {
                                stockAltoInsumos = it
                            }
                        },
                        label = { Text("Stock Alto (kg)") },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        isError = stockAltoInsumos.isNotBlank() && !validarPrecio(stockAltoInsumos),
                        supportingText = {
                            if (stockAltoInsumos.isNotBlank() && !validarPrecio(stockAltoInsumos)) {
                                Text("Máximo 6 dígitos enteros y 2 decimales")
                            }
                        }
                    )

                    OutlinedTextField(
                        value = stockMedioInsumos,
                        onValueChange = { 
                            if (validarPrecio(it)) {
                                stockMedioInsumos = it
                            }
                        },
                        label = { Text("Stock Medio (kg)") },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        isError = stockMedioInsumos.isNotBlank() && !validarPrecio(stockMedioInsumos),
                        supportingText = {
                            if (stockMedioInsumos.isNotBlank() && !validarPrecio(stockMedioInsumos)) {
                                Text("Máximo 6 dígitos enteros y 2 decimales")
                            }
                        }
                    )

                    OutlinedTextField(
                        value = stockBajoInsumos,
                        onValueChange = { 
                            if (validarPrecio(it)) {
                                stockBajoInsumos = it
                            }
                        },
                        label = { Text("Stock Bajo (kg)") },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        isError = stockBajoInsumos.isNotBlank() && !validarPrecio(stockBajoInsumos),
                        supportingText = {
                            if (stockBajoInsumos.isNotBlank() && !validarPrecio(stockBajoInsumos)) {
                                Text("Máximo 6 dígitos enteros y 2 decimales")
                            }
                        }
                    )


                }
            }

            // Información de ayuda
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Información",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1976D2)
                    )
                                         Text(
                         text = "• Productos: Cloro, desinfectantes, etc. (en litros)",
                         style = MaterialTheme.typography.bodySmall
                     )
                     Text(
                         text = "• Insumos: Hipoclorito, ácido cítrico, etc. (en kg)",
                         style = MaterialTheme.typography.bodySmall
                     )
                     Text(
                         text = "• Las alertas se muestran en las pantallas correspondientes",
                         style = MaterialTheme.typography.bodySmall
                     )
                }
            }
        }

        if (showSaveDialog) {
            AlertDialog(
                onDismissRequest = { showSaveDialog = false },
                title = { Text("Guardar Configuración") },
                text = { Text("¿Estás seguro de que quieres guardar estos cambios?") },
                confirmButton = {
                    Button(
                        onClick = {
                                                                                     val config = ConfiguracionStock(
                                stockAltoProductos = stockAltoProductos.toFloatOrNull() ?: 100f,
                                stockMedioProductos = stockMedioProductos.toFloatOrNull() ?: 50f,
                                stockBajoProductos = stockBajoProductos.toFloatOrNull() ?: 25f,
                                stockAltoInsumos = stockAltoInsumos.toFloatOrNull() ?: 200f,
                                stockMedioInsumos = stockMedioInsumos.toFloatOrNull() ?: 100f,
                                stockBajoInsumos = stockBajoInsumos.toFloatOrNull() ?: 50f
                            )
                            viewModel.guardarConfiguracion(config)
                            showSaveDialog = false
                        }
                    ) {
                        Text("Guardar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSaveDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
} 