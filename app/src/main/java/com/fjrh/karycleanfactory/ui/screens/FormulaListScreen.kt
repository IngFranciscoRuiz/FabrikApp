package com.fjrh.karycleanfactory.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fjrh.karycleanfactory.data.local.entity.FormulaEntity
import com.fjrh.karycleanfactory.ui.viewmodel.FormulaViewModel



@Composable
fun FormulaListScreen(viewModel: FormulaViewModel = viewModel()) {
    val formulaList by viewModel.formulas.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.insertarFormula() }) {
                Text("+")
            }
        }
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .padding(16.dp)
        ) {
            Text("Fórmulas registradas", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn {
                items(formulaList) { formula ->
                    FormulaItem(formula)
                }
            }
        }
    }
}

@Composable
fun FormulaItem(formula: FormulaEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text("Nombre: ${formula.nombre}")
            Text("Litros: ${formula.litros}")
        }
    }
}
