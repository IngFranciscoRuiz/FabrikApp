package com.fjrh.FabrikApp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fjrh.FabrikApp.data.local.entity.IngredienteInventarioEntity
import com.fjrh.FabrikApp.data.local.entity.UnidadMedidaEntity
import com.fjrh.FabrikApp.ui.viewmodel.InventarioViewModel
import com.fjrh.FabrikApp.ui.viewmodel.UnidadesViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Agregar Insumo",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Mostrar mensajes de error/éxito
        errorMessage?.let { message ->
            ErrorMessageCard(
                message = message,
                onDismiss = { viewModel.clearMessages() }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        successMessage?.let { message ->
            SuccessMessageCard(
                message = message,
                onDismiss = { viewModel.clearMessages() }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Mostrar loading si está cargando
        if (isLoading) {
            LoadingIndicator("Guardando ingrediente...")
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Campo Nombre con validación
        OutlinedTextField(
            value = nombre,
            onValueChange = { 
                nombre = it
                viewModel.clearMessages()
            },
            label = { Text("Nombre del insumo *") },
            modifier = Modifier.fillMaxWidth(),
            isError = nombre.trim().isBlank() && nombre.isNotBlank(),
            supportingText = {
                if (nombre.trim().isBlank() && nombre.isNotBlank()) {
                    Text("El nombre es obligatorio", color = MaterialTheme.colorScheme.error)
                }
            },
            singleLine = true,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Dropdown para unidad con mejor UX
        Box {
            OutlinedTextField(
                value = unidad,
                onValueChange = {},
                readOnly = true,
                label = { Text("Unidad de medida *") },
                modifier = Modifier.fillMaxWidth(),
                isError = unidad.isBlank() && expanded,
                trailingIcon = {
                    IconButton(
                        onClick = { expanded = !expanded },
                        enabled = !isLoading
                    ) {
                        Icon(
                            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Seleccionar unidad"
                        )
                    }
                },
                singleLine = true,
                enabled = !isLoading
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                unidades.forEach { unidadMedida ->
                    DropdownMenuItem(
                        text = { Text(unidadMedida.nombre) },
                        onClick = {
                            unidad = unidadMedida.nombre
                            expanded = false
                            viewModel.clearMessages()
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Campo Cantidad con validación mejorada
        OutlinedTextField(
            value = cantidad,
            onValueChange = { newValue ->
                if (newValue.matches(Regex("^\\d{0,6}(\\.\\d{0,2})?$")) || newValue.isEmpty()) {
                    cantidad = newValue
                    viewModel.clearMessages()
                }
            },
            label = { Text("Cantidad disponible *") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            isError = cantidad.isNotBlank() && cantidad.toFloatOrNull() == null,
            supportingText = {
                if (cantidad.isNotBlank() && cantidad.toFloatOrNull() == null) {
                    Text("Ingresa un número válido", color = MaterialTheme.colorScheme.error)
                } else if (cantidad.isNotBlank()) {
                    Text("Ej: 100.5")
                }
            },
            singleLine = true,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo Costo con validación mejorada
        OutlinedTextField(
            value = costoPorUnidad,
            onValueChange = { newValue ->
                if (newValue.matches(Regex("^\\d{0,8}(\\.\\d{0,2})?$")) || newValue.isEmpty()) {
                    costoPorUnidad = newValue
                    viewModel.clearMessages()
                }
            },
            label = { Text("Costo por unidad *") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            isError = costoPorUnidad.isNotBlank() && costoPorUnidad.toDoubleOrNull() == null,
            supportingText = {
                if (costoPorUnidad.isNotBlank() && costoPorUnidad.toDoubleOrNull() == null) {
                    Text("Ingresa un número válido", color = MaterialTheme.colorScheme.error)
                } else if (costoPorUnidad.isNotBlank()) {
                    Text("Ej: 15.50")
                }
            },
            singleLine = true,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo Proveedor (opcional)
        OutlinedTextField(
            value = proveedor,
            onValueChange = { proveedor = it },
            label = { Text("Proveedor (opcional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botón con estado de carga
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
            enabled = isFormValid && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Guardando...")
            } else {
                Text("Guardar Insumo")
            }
        }
    }
}
