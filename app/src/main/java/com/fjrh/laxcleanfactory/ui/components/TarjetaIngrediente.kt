package com.fjrh.laxcleanfactory.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fjrh.laxcleanfactory.data.local.entity.IngredienteInventarioEntity

@Composable
fun TarjetaIngrediente(
    ingrediente: IngredienteInventarioEntity,
    onDelete: () -> Unit,
    onEdit: (IngredienteInventarioEntity) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editedIngrediente by remember { mutableStateOf(ingrediente) }
    
    val colorSemaforo = when {
        ingrediente.cantidadDisponible <= 0 -> Color(0xFFE57373) // rojo - sin stock
        ingrediente.cantidadDisponible < 10 -> Color(0xFFFFB74D) // naranja - stock bajo
        ingrediente.cantidadDisponible < 50 -> Color(0xFFFFF176) // amarillo - stock medio
        else -> Color(0xFF81C784) // verde - stock bueno
    }
    
    val textoStock = when {
        ingrediente.cantidadDisponible <= 0 -> "SIN STOCK"
        ingrediente.cantidadDisponible < 10 -> "STOCK BAJO"
        ingrediente.cantidadDisponible < 50 -> "STOCK MEDIO"
        else -> "STOCK OK"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = 4.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Encabezado compacto
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ING.",
                    style = MaterialTheme.typography.caption,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.primary
                )
                
                // Indicador de stock
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(colorSemaforo, CircleShape)
                    )
                    Text(
                        text = textoStock,
                        style = MaterialTheme.typography.caption,
                        color = colorSemaforo,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Botones de acción
                Row {
                    if (isEditing) {
                        IconButton(
                            onClick = {
                                onEdit(editedIngrediente)
                                isEditing = false
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = "Guardar",
                                tint = Color.Green
                            )
                        }
                        IconButton(
                            onClick = {
                                editedIngrediente = ingrediente
                                isEditing = false
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cancel,
                                contentDescription = "Cancelar",
                                tint = Color.Red
                            )
                        }
                    } else {
                        IconButton(onClick = { isEditing = true }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar"
                            )
                        }
                        IconButton(onClick = onDelete) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Contenido principal - Layout vertical
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Nombre del ingrediente
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Nombre:",
                        style = MaterialTheme.typography.body2,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(0.3f)
                    )
                    if (isEditing) {
                        OutlinedTextField(
                            value = editedIngrediente.nombre,
                            onValueChange = { editedIngrediente = editedIngrediente.copy(nombre = it) },
                            modifier = Modifier.weight(0.7f),
                            textStyle = MaterialTheme.typography.body2
                        )
                    } else {
                        Text(
                            text = ingrediente.nombre,
                            style = MaterialTheme.typography.body2,
                            modifier = Modifier.weight(0.7f)
                        )
                    }
                }

                // Unidad
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Unidad:",
                        style = MaterialTheme.typography.body2,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(0.3f)
                    )
                    if (isEditing) {
                        OutlinedTextField(
                            value = editedIngrediente.unidad,
                            onValueChange = { editedIngrediente = editedIngrediente.copy(unidad = it) },
                            modifier = Modifier.weight(0.7f),
                            textStyle = MaterialTheme.typography.body2
                        )
                    } else {
                        Text(
                            text = ingrediente.unidad,
                            style = MaterialTheme.typography.body2,
                            modifier = Modifier.weight(0.7f)
                        )
                    }
                }

                // Cantidad disponible
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Cantidad:",
                        style = MaterialTheme.typography.body2,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(0.3f)
                    )
                    if (isEditing) {
                        OutlinedTextField(
                            value = editedIngrediente.cantidadDisponible.toString(),
                            onValueChange = { 
                                val cantidad = it.toFloatOrNull() ?: 0f
                                editedIngrediente = editedIngrediente.copy(cantidadDisponible = cantidad)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(0.7f),
                            textStyle = MaterialTheme.typography.body2
                        )
                    } else {
                        Text(
                            text = ingrediente.cantidadDisponible.toString(),
                            style = MaterialTheme.typography.body2,
                            modifier = Modifier.weight(0.7f)
                        )
                    }
                }

                // Costo por unidad
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Costo:",
                        style = MaterialTheme.typography.body2,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(0.3f)
                    )
                    if (isEditing) {
                        OutlinedTextField(
                            value = editedIngrediente.costoPorUnidad.toString(),
                            onValueChange = { 
                                val costo = it.toDoubleOrNull() ?: 0.0
                                editedIngrediente = editedIngrediente.copy(costoPorUnidad = costo)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(0.7f),
                            textStyle = MaterialTheme.typography.body2
                        )
                    } else {
                        Text(
                            text = "$${String.format("%.2f", ingrediente.costoPorUnidad)}",
                            style = MaterialTheme.typography.body2,
                            modifier = Modifier.weight(0.7f)
                        )
                    }
                }

                // Proveedor
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Proveedor:",
                        style = MaterialTheme.typography.body2,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(0.3f)
                    )
                    if (isEditing) {
                        OutlinedTextField(
                            value = editedIngrediente.proveedor ?: "",
                            onValueChange = { editedIngrediente = editedIngrediente.copy(proveedor = it) },
                            modifier = Modifier.weight(0.7f),
                            textStyle = MaterialTheme.typography.body2
                        )
                    } else {
                        Text(
                            text = ingrediente.proveedor ?: "No especificado",
                            style = MaterialTheme.typography.body2,
                            modifier = Modifier.weight(0.7f)
                        )
                    }
                }
            }
        }
    }
}
