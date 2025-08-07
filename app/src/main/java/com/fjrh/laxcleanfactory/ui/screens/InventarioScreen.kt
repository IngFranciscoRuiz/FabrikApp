package com.fjrh.laxcleanfactory.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fjrh.laxcleanfactory.data.local.entity.IngredienteInventarioEntity
import com.fjrh.laxcleanfactory.ui.viewmodel.InventarioViewModel
import com.fjrh.laxcleanfactory.ui.theme.fondoLaxCleanGradient
import com.fjrh.laxcleanfactory.ui.components.TarjetaIngrediente
import com.fjrh.laxcleanfactory.data.local.ConfiguracionDataStore
import com.fjrh.laxcleanfactory.domain.model.ConfiguracionStock

@Composable
fun InventarioScreen(
    viewModel: InventarioViewModel,
    onAgregarClicked: () -> Unit
) {
    val ingredientes by viewModel.ingredientes.collectAsState(initial = emptyList<IngredienteInventarioEntity>())
    val configuracion by viewModel.configuracion.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    
    val ingredientesFiltrados = ingredientes.filter { ingrediente ->
        ingrediente.nombre.contains(searchQuery, ignoreCase = true) ||
        ingrediente.proveedor?.contains(searchQuery, ignoreCase = true) == true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventario de Insumos", fontWeight = FontWeight.Bold) },
                backgroundColor = Color.Transparent,
                elevation = 0.dp
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAgregarClicked() },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = Color.White,
                modifier = Modifier.padding(bottom = 50.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar insumo")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(fondoLaxCleanGradient)
        ) {
            // Campo de búsqueda mejorado
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar insumos...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Buscar")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    backgroundColor = Color.White.copy(alpha = 0.9f),
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black.copy(alpha = 0.7f)
                )
            )
            
            if (ingredientesFiltrados.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (searchQuery.isBlank()) "No hay insumos en inventario" else "No se encontraron insumos",
                        color = MaterialTheme.colors.onSurface
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(ingredientesFiltrados) { ingrediente ->
                        TarjetaIngrediente(
                            ingrediente = ingrediente,
                            onDelete = { viewModel.eliminarIngrediente(ingrediente) },
                            onEdit = { ingredienteEditado -> viewModel.actualizarIngrediente(ingredienteEditado) },
                            configuracion = configuracion
                        )
                    }
                }
            }
        }
    }
}
