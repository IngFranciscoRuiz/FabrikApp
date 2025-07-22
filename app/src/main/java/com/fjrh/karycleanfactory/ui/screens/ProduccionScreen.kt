package com.fjrh.karycleanfactory.ui.screens

import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.fjrh.karycleanfactory.data.local.entity.HistorialProduccionEntity

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fjrh.karycleanfactory.data.local.entity.FormulaConIngredientes
import com.fjrh.karycleanfactory.ui.viewmodel.FormulaViewModel
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardOptions


fun String.toFloatOrZero(): Float = this.toFloatOrNull() ?: 0f

@Composable
fun ProduccionScreen(
    formula: FormulaConIngredientes?,
    viewModel: FormulaViewModel
) {
    var litrosDeseados by remember { mutableStateOf("") }
    var selectedFormula by remember { mutableStateOf<FormulaConIngredientes?>(formula) }
    val listaFormulas by viewModel.formulas.collectAsState()
    val checkedItems = remember { mutableStateMapOf<Long, Boolean>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (selectedFormula == null) {
            if (listaFormulas.isEmpty()) {
                Text("⚠️ No hay fórmulas registradas.")
            } else {
                Text("Selecciona una fórmula para calcular producción:")
                var expanded by remember { mutableStateOf(false) }

                Box {
                    OutlinedButton(onClick = { expanded = true }) {
                        Text("Seleccionar fórmula")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        listaFormulas.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item.formula.nombre) },
                                onClick = {
                                    selectedFormula = item
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        selectedFormula?.let { formula ->
            Text(
                text = "Producción: ${formula.formula.nombre}",
                style = MaterialTheme.typography.headlineSmall
            )

            OutlinedTextField(
                value = litrosDeseados,
                onValueChange = { litrosDeseados = it },
                label = { Text("Litros a producir") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            val litros = litrosDeseados.toFloatOrZero()

            if (litros > 0f) {
                Text("Ingredientes necesarios:", style = MaterialTheme.typography.titleMedium)

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Ingrediente", modifier = Modifier.weight(1f))
                            Text("Unidad", modifier = Modifier.weight(1f))
                            Text("Cantidad", modifier = Modifier.weight(1f))
                            Text("✔️", modifier = Modifier.weight(0.5f))
                        }
                    }

                    items(formula.ingredientes) { ingrediente ->
                        val cantidadCalculada = litros * ingrediente.cantidad.toFloatOrZero()
                        val isChecked = checkedItems[ingrediente.id] ?: false

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(ingrediente.nombre, modifier = Modifier.weight(1f))
                            Text(ingrediente.unidad, modifier = Modifier.weight(1f))
                            Text("%.2f".format(cantidadCalculada), modifier = Modifier.weight(1f))
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { checkedItems[ingrediente.id] = it },
                                modifier = Modifier.weight(0.5f)
                            )
                        }
                    }
                }

                val allChecked = formula.ingredientes.all { checkedItems[it.id] == true }

                Button(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            val historial = HistorialProduccionEntity(
                                nombreFormula = formula.formula.nombre,
                                litrosProducidos = litros,
                                fecha = System.currentTimeMillis()
                            )
                            viewModel.insertarHistorial(historial)
                        }
                    },
                    enabled = allChecked,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Lote realizado")
                }

            } else {
                Text(
                    text = "Ingresa los litros deseados para calcular cantidades.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
