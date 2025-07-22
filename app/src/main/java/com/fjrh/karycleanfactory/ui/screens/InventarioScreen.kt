package com.fjrh.karycleanfactory.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.input.TextFieldDefaults
import androidx.compose.material3.TextFieldDefaults
//import androidx.compose.material.TextFieldDefaults


import androidx.compose.ui.unit.dp
import com.fjrh.karycleanfactory.data.local.entity.IngredienteInventarioEntity
import com.fjrh.karycleanfactory.ui.viewmodel.InventarioViewModel
import java.time.LocalDate

@Composable
fun InventarioScreen(viewModel: InventarioViewModel) {
    val inventario by viewModel.inventario.collectAsState()

    var nombre by remember { mutableStateOf("") }
    var unidad by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var proveedor by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Registrar ingrediente", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = unidad,
            onValueChange = { unidad = it },
            label = { Text("Unidad (ej. kg, L)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = cantidad,
            onValueChange = { cantidad = it },
            label = { Text("Cantidad disponible") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = proveedor,
            onValueChange = { proveedor = it },
            label = { Text("Proveedor") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (nombre.isNotBlank() && unidad.isNotBlank() && cantidad.isNotBlank()) {
                    val nuevoIngrediente = IngredienteInventarioEntity(
                        nombre = nombre,
                        unidad = unidad,
                        cantidadDisponible = cantidad.toFloatOrNull() ?: 0f,
                        proveedor = proveedor,
                        fechaIngreso = System.currentTimeMillis()
                    )
                    viewModel.insertarIngrediente(nuevoIngrediente)
                    nombre = ""
                    unidad = ""
                    cantidad = ""
                    proveedor = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Agregar al inventario")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Inventario actual", style = MaterialTheme.typography.titleMedium)

        LazyColumn {
            items(inventario) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Ingrediente: ${item.nombre}")
                        Text("Unidad: ${item.unidad}")
                        Text("Cantidad: ${item.cantidadDisponible}")
                        Text("Proveedor: ${item.proveedor}")
                        Text("Fecha ingreso: ${item.fechaIngreso}")
                    }
                }
            }
        }
    }
}

