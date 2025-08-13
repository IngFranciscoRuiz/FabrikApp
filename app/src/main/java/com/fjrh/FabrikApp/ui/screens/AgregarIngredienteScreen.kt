package com.fjrh.FabrikApp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fjrh.FabrikApp.data.local.entity.IngredienteInventarioEntity
import com.fjrh.FabrikApp.data.local.entity.UnidadMedidaEntity
import com.fjrh.FabrikApp.ui.viewmodel.InventarioViewModel
import com.fjrh.FabrikApp.ui.viewmodel.UnidadesViewModel
import com.fjrh.FabrikApp.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarIngredienteScreen(
    viewModel: InventarioViewModel = hiltViewModel(),
    unidadesViewModel: UnidadesViewModel = hiltViewModel(),
    onGuardarExitoso: () -> Unit = {}
) {
    var nombre by remember { mutableStateOf("") }
    var unidad by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var costoPorUnidad by remember { mutableStateOf("") }
    var proveedor by remember { mutableStateOf("") }

    val unidades by unidadesViewModel.unidades.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    var expanded by remember { mutableStateOf(false) }
    var isFormValid by remember { mutableStateOf(false) }

    // Validación en tiempo real
    LaunchedEffect(nombre, unidad, cantidad, costoPorUnidad) {
        isFormValid = nombre.trim().isNotBlank() && 
                     unidad.isNotBlank() && 
                     cantidad.trim().isNotBlank() && 
                     costoPorUnidad.trim().isNotBlank() &&
                     cantidad.toFloatOrNull() != null &&
                     costoPorUnidad.toDoubleOrNull() != null
    }

    // Limpiar mensajes cuando se navega
    LaunchedEffect(Unit) {
        viewModel.clearMessages()
    }

    // Manejar éxito
    LaunchedEffect(successMessage) {
        successMessage?.let {
            onGuardarExitoso()
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
                        .clickable { /* TODO: Implementar navegación */ }
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = "Agregar Insumo",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFF1A1A1A),
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Icon(
                    imageVector = Icons.Default.WaterDrop,
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
                        text = "Nuevo Insumo",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF1A1A1A),
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Agrega un nuevo ingrediente al inventario",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF666666),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Mostrar mensajes de error/éxito
            errorMessage?.let { message ->
                ModernErrorCard(
                    message = message,
                    onDismiss = { viewModel.clearMessages() }
                )
            }

            successMessage?.let { message ->
                ModernSuccessCard(
                    message = message,
                    onDismiss = { viewModel.clearMessages() }
                )
            }

            // Mostrar loading si está cargando
            if (isLoading) {
                ModernLoadingCard("Guardando ingrediente...")
            }

            // Formulario
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
                        value = nombre,
                        onValueChange = { 
                            nombre = it
                            viewModel.clearMessages()
                        },
                        label = "Nombre del insumo",
                        icon = Icons.Default.Inventory,
                        color = Color(0xFF4CAF50),
                        isError = nombre.trim().isBlank() && nombre.isNotBlank(),
                        errorMessage = "El nombre es obligatorio",
                        enabled = !isLoading
                    )

                    // Dropdown para unidad
                    ModernDropdownField(
                        value = unidad,
                        onValueChange = { unidad = it },
                        label = "Unidad de medida",
                        icon = Icons.Default.Straighten,
                        color = Color(0xFF2196F3),
                        options = unidades.map { it.nombre },
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                        isError = unidad.isBlank() && expanded,
                        errorMessage = "Selecciona una unidad",
                        enabled = !isLoading
                    )

                    // Campo Cantidad
                    ModernFormField(
                        value = cantidad,
                        onValueChange = { newValue ->
                            if (newValue.matches(Regex("^\\d{0,6}(\\.\\d{0,2})?$")) || newValue.isEmpty()) {
                                cantidad = newValue
                                viewModel.clearMessages()
                            }
                        },
                        label = "Cantidad disponible",
                        icon = Icons.Default.Scale,
                        color = Color(0xFFFF9800),
                        keyboardType = KeyboardType.Decimal,
                        isError = cantidad.isNotBlank() && cantidad.toFloatOrNull() == null,
                        errorMessage = "Ingresa un número válido",
                        hint = "Ej: 100.5",
                        enabled = !isLoading
                    )

                    // Campo Costo
                    ModernFormField(
                        value = costoPorUnidad,
                        onValueChange = { newValue ->
                            if (newValue.matches(Regex("^\\d{0,8}(\\.\\d{0,2})?$")) || newValue.isEmpty()) {
                                costoPorUnidad = newValue
                                viewModel.clearMessages()
                            }
                        },
                        label = "Costo por unidad",
                        icon = Icons.Default.AttachMoney,
                        color = Color(0xFF9C27B0),
                        keyboardType = KeyboardType.Decimal,
                        isError = costoPorUnidad.isNotBlank() && costoPorUnidad.toDoubleOrNull() == null,
                        errorMessage = "Ingresa un número válido",
                        hint = "Ej: 15.50",
                        enabled = !isLoading
                    )

                    // Campo Proveedor
                    ModernFormField(
                        value = proveedor,
                        onValueChange = { proveedor = it },
                        label = "Proveedor (opcional)",
                        icon = Icons.Default.Business,
                        color = Color(0xFF607D8B),
                        enabled = !isLoading
                    )
                }
            }

            // Botón de guardar
            Button(
                onClick = {
                    if (isFormValid) {
                        try {
                            val ingrediente = IngredienteInventarioEntity(
                                nombre = nombre.trim(),
                                unidad = unidad,
                                cantidadDisponible = cantidad.toFloat(),
                                costoPorUnidad = costoPorUnidad.toDouble(),
                                proveedor = proveedor.trim(),
                                fechaIngreso = System.currentTimeMillis()
                            )

                            viewModel.agregarIngrediente(ingrediente)
                        } catch (e: NumberFormatException) {
                            // El error se maneja en el ViewModel
                        } catch (e: Exception) {
                            // El error se maneja en el ViewModel
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = isFormValid && !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardando...")
                } else {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardar Insumo")
                }
            }
        }
    }
}

@Composable
fun ModernFormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    errorMessage: String = "",
    hint: String = "",
    enabled: Boolean = true
) {
    Column {
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
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = Modifier.fillMaxWidth(),
            isError = isError,
            supportingText = {
                if (isError) {
                    Text(errorMessage, color = Color(0xFFD32F2F))
                } else if (hint.isNotBlank()) {
                    Text(hint, color = Color(0xFF666666))
                }
            },
            singleLine = true,
            enabled = enabled,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = color,
                unfocusedBorderColor = Color(0xFFE0E0E0)
            ),
            shape = RoundedCornerShape(12.dp)
        )
    }
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
    isError: Boolean = false,
    errorMessage: String = "",
    enabled: Boolean = true
) {
    Column {
        Box {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                label = { Text(label) },
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
                isError = isError,
                supportingText = {
                    if (isError) {
                        Text(errorMessage, color = Color(0xFFD32F2F))
                    }
                },
                singleLine = true,
                enabled = enabled,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = color,
                    unfocusedBorderColor = Color(0xFFE0E0E0)
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
}

@Composable
fun ModernErrorCard(
    message: String,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = Color(0xFFD32F2F)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFD32F2F),
                modifier = Modifier.weight(1f)
            )
            
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar",
                    tint = Color(0xFFD32F2F)
                )
            }
        }
    }
}

@Composable
fun ModernSuccessCard(
    message: String,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF388E3C)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF388E3C),
                modifier = Modifier.weight(1f)
            )
            
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar",
                    tint = Color(0xFF388E3C)
                )
            }
        }
    }
}

@Composable
fun ModernLoadingCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color(0xFF1976D2)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF1976D2)
            )
        }
    }
}
