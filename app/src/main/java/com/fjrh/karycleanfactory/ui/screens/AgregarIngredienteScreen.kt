package com.fjrh.karycleanfactory.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fjrh.karycleanfactory.data.local.entity.IngredienteInventarioEntity
import com.fjrh.karycleanfactory.ui.viewmodel.InventarioViewModel
import androidx.compose.material3.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarIngredienteScreen(
    viewModel: InventarioViewModel = hiltViewModel(),
    onGuardarExitoso: () -> Unit = {}
) {
    var nombre by remember { mutableStateOf("") }
    var unidad by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var costoPorUnidad by remember { mutableStateOf("") }
    var proveedor by remember { mutableStateOf("") }

    val unidades = listOf("L", "ml", "gr", "Kg", "Pzas")

    var expanded by remember { mutableStateOf(false) }
    var errorMensaje by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Agregar Ingrediente",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Dropdown para unidad
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = unidad,
                onValueChange = {},
                readOnly = true,
                label = { Text("Unidad") },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                unidades.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            unidad = item
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = cantidad,
            onValueChange = {
                if (it.matches(Regex("^\\d{0,4}(\\.\\d{0,2})?$"))) cantidad = it
            },
            label = { Text("Cantidad disponible") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = costoPorUnidad,
            onValueChange = {
                if (it.matches(Regex("^\\d{0,6}(\\.\\d{0,2})?$"))) costoPorUnidad = it
            },
            label = { Text("Costo por unidad") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = proveedor,
            onValueChange = { proveedor = it },
            label = { Text("Proveedor") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        errorMensaje?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                if (nombre.isBlank() || unidad.isBlank() || cantidad.isBlank() || costoPorUnidad.isBlank()) {
                    errorMensaje = "Todos los campos obligatorios deben estar llenos."
                } else {
                    val ingrediente = IngredienteInventarioEntity(
                        nombre = nombre.trim(),
                        unidad = unidad,
                        cantidadDisponible = cantidad.toFloat(),
                        costoPorUnidad = costoPorUnidad.toDouble(),
                        proveedor = proveedor.trim(),
                        fechaIngreso = System.currentTimeMillis()
                    )

                    viewModel.agregarIngrediente(ingrediente)
                    errorMensaje = null
                    onGuardarExitoso()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar")
        }
    }
}
