package com.fjrh.karycleanfactory.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fjrh.karycleanfactory.data.local.entity.IngredienteInventarioEntity
import com.fjrh.karycleanfactory.ui.viewmodel.InventarioViewModel
import com.fjrh.karycleanfactory.ui.theme.fondoAzulGradient
import com.fjrh.karycleanfactory.ui.components.TarjetaIngrediente

@Composable
fun InventarioScreen(
    viewModel: InventarioViewModel,
    onAgregarClicked: () -> Unit
) {
    val ingredientes = viewModel.ingredientes.collectAsState(initial = emptyList<IngredienteInventarioEntity>()).value
    var searchQuery by remember { mutableStateOf("") }
    
    val ingredientesFiltrados = ingredientes.filter { ingrediente ->
        ingrediente.nombre.contains(searchQuery, ignoreCase = true) ||
        ingrediente.proveedor?.contains(searchQuery, ignoreCase = true) == true
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { onAgregarClicked() }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar ingrediente")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(fondoAzulGradient)
        ) {
            // Campo de búsqueda
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar ingredientes...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            
            if (ingredientesFiltrados.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (searchQuery.isBlank()) "No hay ingredientes en inventario" else "No se encontraron ingredientes",
                        color = MaterialTheme.colors.onSurface
                    )
                }
            } else {
                LazyColumn {
                    items(ingredientesFiltrados) { ingrediente ->
                        TarjetaIngrediente(
                            ingrediente = ingrediente,
                            onDelete = { viewModel.eliminarIngrediente(ingrediente) },
                            onEdit = { /* Abrir edición más adelante */ }
                        )
                    }
                }
            }
        }
    }
}
