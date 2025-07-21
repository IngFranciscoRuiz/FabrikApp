package com.fjrh.karycleanfactory.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fjrh.karycleanfactory.data.local.entity.IngredienteEntity
import com.fjrh.karycleanfactory.ui.viewmodel.FormulaViewModel

@Composable
fun NuevaFormulaScreen(
    viewModel: FormulaViewModel,
    navController: NavController
) {
    var nombreFormula by remember { mutableStateOf("") }
    var nombreIngrediente by remember { mutableStateOf("") }
    var unidad by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }

    val listaIngredientes = remember { mutableStateListOf<IngredienteEntity>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
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
            text = "INGREDIENTES POR LITRO:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = nombreIngrediente,
            onValueChange = { nombreIngrediente = it },
            label = { Text("Ingrediente") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = unidad,
                onValueChange = { unidad = it },
                label = { Text("Unidad") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = cantidad,
                onValueChange = { cantidad = it },
                label = { Text("Cantidad") },
                modifier = Modifier.weight(1f)
            )
        }

        Button(
            onClick = {
                if (nombreIngrediente.isNotBlank() && unidad.isNotBlank() && cantidad.isNotBlank()) {
                    listaIngredientes.add(
                        IngredienteEntity(
                            formulaId = 0, // Se asignará en el ViewModel
                            nombre = nombreIngrediente,
                            unidad = unidad,
                            cantidad = cantidad
                        )
                    )
                    nombreIngrediente = ""
                    unidad = ""
                    cantidad = ""
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF944D2E))
        ) {
            Text("Agregar ingrediente", color = Color.White)
        }

        if (listaIngredientes.isNotEmpty()) {
            Text(
                text = "Ingredientes añadidos:",
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
                        text = "${ingrediente.nombre} - ${ingrediente.cantidad} ${ingrediente.unidad}",
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { listaIngredientes.removeAt(index) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                    }
                }
            }
        }

        Button(
            onClick = {
                if (nombreFormula.isNotBlank() && listaIngredientes.isNotEmpty()) {
                    viewModel.guardarFormulaConIngredientes(nombreFormula, listaIngredientes)
                    navController.popBackStack() // Regresa a la pantalla anterior
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFE8DC))
        ) {
            Text("Guardar fórmula", color = Color(0xFF944D2E))
        }
    }
}
