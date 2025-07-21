package com.fjrh.karycleanfactory.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.fjrh.karycleanfactory.data.local.entity.FormulaConIngredientes
import com.fjrh.karycleanfactory.ui.viewmodel.FormulaViewModel
import kotlinx.coroutines.launch

@Composable
fun ListaFormulasScreen(
    navController: NavController,
    viewModel: FormulaViewModel
) {
    val scope = rememberCoroutineScope()
    var lista by remember { mutableStateOf<List<FormulaConIngredientes>>(emptyList()) }

    LaunchedEffect(Unit) {
        scope.launch {
            lista = viewModel.obtenerFormulas()
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("nueva_formula") },
                containerColor = Color(0xFF944D2E),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar fórmula")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFFDF4F2))
                .padding(16.dp)
        ) {
            Text(
                text = "Fórmulas registradas",
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF944D2E)
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (lista.isEmpty()) {
                Text("No hay fórmulas registradas.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(lista) { formula ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE8DC))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = formula.formula.nombre,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color(0xFF944D2E)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                formula.ingredientes.forEach {
                                    Text("- ${it.nombre}: ${it.cantidad} ${it.unidad}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
