package com.fjrh.FabrikApp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fjrh.FabrikApp.data.local.entity.IngredienteInventarioEntity
import com.fjrh.FabrikApp.domain.model.ConfiguracionStock
import com.fjrh.FabrikApp.domain.service.StockAlertService

@Composable
fun TarjetaIngrediente(
    ingrediente: IngredienteInventarioEntity,
    onDelete: () -> Unit,
    onEdit: (IngredienteInventarioEntity) -> Unit,
    configuracion: ConfiguracionStock? = null
) {
    var isEditing by remember { mutableStateOf(false) }
    var editedIngrediente by remember { mutableStateOf(ingrediente) }
    
    val stockAlertService = remember { StockAlertService() }
    
    val colorSemaforo = if (configuracion != null) {
        stockAlertService.getStockColorInsumo(ingrediente.cantidadDisponible, configuracion)
    } else {
        when {
            ingrediente.cantidadDisponible <= 0 -> Color(0xFFE57373) // rojo - sin stock
            ingrediente.cantidadDisponible < 10 -> Color(0xFFFFB74D) // naranja - stock bajo
            ingrediente.cantidadDisponible < 50 -> Color(0xFFFFF176) // amarillo - stock medio
            else -> Color(0xFF81C784) // verde - stock bueno
        }
    }
    
    val textoStock = if (configuracion != null) {
        stockAlertService.getStockTextInsumo(ingrediente.cantidadDisponible, configuracion)
    } else {
        when {
            ingrediente.cantidadDisponible <= 0 -> "SIN STOCK"
            ingrediente.cantidadDisponible < 10 -> "STOCK BAJO"
            ingrediente.cantidadDisponible < 50 -> "STOCK MEDIO"
            else -> "STOCK OK"
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
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
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
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
                        style = MaterialTheme.typography.labelSmall,
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
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(0.3f)
                    )
                    if (isEditing) {
                        OutlinedTextField(
                            value = editedIngrediente.nombre,
                            onValueChange = { editedIngrediente = editedIngrediente.copy(nombre = it) },
                            modifier = Modifier.weight(0.7f),
                            textStyle = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        Text(
                            text = ingrediente.nombre,
                            style = MaterialTheme.typography.bodyMedium,
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
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(0.3f)
                    )
                    if (isEditing) {
                        var expanded by remember { mutableStateOf(false) }
                        val unidades = listOf("Kg", "gr", "L", "ml", "Pzas")
                        
                        Box(modifier = Modifier.weight(0.7f)) {
                            OutlinedTextField(
                                value = editedIngrediente.unidad,
                                onValueChange = { },
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = MaterialTheme.typography.bodyMedium,
                                trailingIcon = {
                                    IconButton(onClick = { expanded = true }) {
                                        Icon(
                                            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                            contentDescription = "Desplegar unidades"
                                        )
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                                )
                            )
                            
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                            ) {
                                unidades.forEach { unidad ->
                                    DropdownMenuItem(
                                        text = { 
                                            Text(
                                                text = unidad,
                                                color = MaterialTheme.colorScheme.onSurface
                                            ) 
                                        },
                                        onClick = {
                                            editedIngrediente = editedIngrediente.copy(unidad = unidad)
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        Text(
                            text = ingrediente.unidad,
                            style = MaterialTheme.typography.bodyMedium,
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
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(0.3f)
                    )
                    if (isEditing) {
                        var cantidadText by remember { mutableStateOf(editedIngrediente.cantidadDisponible.toString()) }
                        
                        OutlinedTextField(
                            value = cantidadText,
                            onValueChange = { newValue ->
                                // Solo permitir números y un punto decimal
                                if (newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                                    cantidadText = newValue
                                    val cantidad = newValue.toFloatOrNull() ?: 0f
                                    editedIngrediente = editedIngrediente.copy(cantidadDisponible = cantidad)
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(0.7f),
                            textStyle = MaterialTheme.typography.bodyMedium,
                            singleLine = true
                        )
                    } else {
                        Text(
                            text = ingrediente.cantidadDisponible.toString(),
                            style = MaterialTheme.typography.bodyMedium,
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
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(0.3f)
                    )
                    if (isEditing) {
                        var costoText by remember { mutableStateOf(editedIngrediente.costoPorUnidad.toString()) }
                        
                        OutlinedTextField(
                            value = costoText,
                            onValueChange = { newValue ->
                                // Solo permitir números y un punto decimal
                                if (newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                                    costoText = newValue
                                    val costo = newValue.toDoubleOrNull() ?: 0.0
                                    editedIngrediente = editedIngrediente.copy(costoPorUnidad = costo)
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(0.7f),
                            textStyle = MaterialTheme.typography.bodyMedium,
                            singleLine = true
                        )
                    } else {
                        Text(
                            text = "$${String.format("%.2f", ingrediente.costoPorUnidad)}",
                            style = MaterialTheme.typography.bodyMedium,
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
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(0.3f)
                    )
                    if (isEditing) {
                        OutlinedTextField(
                            value = editedIngrediente.proveedor ?: "",
                            onValueChange = { editedIngrediente = editedIngrediente.copy(proveedor = it) },
                            modifier = Modifier.weight(0.7f),
                            textStyle = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        Text(
                            text = ingrediente.proveedor ?: "No especificado",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(0.7f)
                        )
                    }
                }
            }
        }
    }
}
