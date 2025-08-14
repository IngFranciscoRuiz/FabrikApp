package com.fjrh.FabrikApp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.fjrh.FabrikApp.data.local.entity.NotaEntity
import com.fjrh.FabrikApp.ui.viewmodel.NotasViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotasScreen(
    navController: NavController,
    viewModel: NotasViewModel = hiltViewModel()
) {
    var showAddDialog by remember { mutableStateOf(false) }
    val notas by viewModel.notas.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(top = 60.dp)
                .padding(bottom = 100.dp)
        ) {
            // Header moderno
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(12.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color(0xFF1976D2)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Text(
                        text = "Notas",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFF1A1A1A),
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1976D2)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "${notas.size}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Estadísticas rápidas
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                NotasStatCard(
                    title = "Total",
                    value = notas.size.toString(),
                    color = Color(0xFF2196F3),
                    modifier = Modifier.weight(1f)
                )
                NotasStatCard(
                    title = "Completadas",
                    value = notas.count { it.esCompletada }.toString(),
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Lista de notas
            if (notas.isEmpty()) {
                NotasEmptyState()
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(notas) { nota ->
                        ModernNotaCard(
                            nota = nota,
                            onDelete = { viewModel.eliminarNota(nota) },
                            onEdit = { notaEditada -> viewModel.actualizarNota(notaEditada) }
                        )
                    }
                }
            }
        }
        
        // FAB para agregar nota
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(horizontal = 20.dp, vertical = 60.dp),
            containerColor = Color(0xFF1976D2),
            contentColor = Color.White
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Agregar nota",
                modifier = Modifier.size(24.dp)
            )
        }
        
        // Snackbar
        SnackbarHost(
            hostState = remember { SnackbarHostState() },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    if (showAddDialog) {
        ModernAgregarNotaDialog(
            onDismiss = { showAddDialog = false },
            onNotaAgregada = { nota ->
                viewModel.agregarNota(nota)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun NotasStatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = color,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF666666),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ModernNotaCard(
    nota: NotaEntity,
    onDelete: () -> Unit,
    onEdit: (NotaEntity) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editedNota by remember { mutableStateOf(nota) }
    
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val colorNota = when {
        nota.esCompletada -> Color(0xFFE8F5E8) // Verde claro para completadas
        else -> Color(0xFFFFF8E1) // Amarillo claro para notas normales
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Encabezado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Note,
                        contentDescription = null,
                        tint = Color(0xFF1976D2),
                        modifier = Modifier.size(20.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = if (isEditing) "Editando..." else nota.titulo,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF1A1A1A),
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Row {
                    if (isEditing) {
                        IconButton(
                            onClick = {
                                onEdit(editedNota)
                                isEditing = false
                            },
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    color = Color(0xFFE8F5E8),
                                    shape = RoundedCornerShape(8.dp)
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Guardar",
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        IconButton(
                            onClick = {
                                editedNota = nota
                                isEditing = false
                            },
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    color = Color(0xFFFFEBEE),
                                    shape = RoundedCornerShape(8.dp)
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancelar",
                                tint = Color(0xFFF44336),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    } else {
                        IconButton(
                            onClick = { isEditing = true },
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    color = Color(0xFFE3F2FD),
                                    shape = RoundedCornerShape(8.dp)
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar",
                                tint = Color(0xFF1976D2),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    color = Color(0xFFFFEBEE),
                                    shape = RoundedCornerShape(8.dp)
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar",
                                tint = Color(0xFFF44336),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Contenido
            if (isEditing) {
                NotasFormField(
                    value = editedNota.titulo,
                    onValueChange = { editedNota = editedNota.copy(titulo = it) },
                    label = "Título",
                    icon = Icons.Default.Title,
                    color = Color(0xFF1976D2)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = editedNota.contenido,
                    onValueChange = { editedNota = editedNota.copy(contenido = it) },
                    label = { Text("Contenido") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1976D2),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            } else {
                Text(
                    text = nota.contenido,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF666666)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Información adicional
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateFormat.format(Date(nota.fecha)),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF999999)
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (nota.esCompletada) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "✅ Completada",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF4CAF50),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotasEmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Note,
                contentDescription = null,
                tint = Color(0xFFCCCCCC),
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No hay notas registradas",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF666666),
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Toca el botón + para crear tu primera nota",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF999999),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun NotasFormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color
            )
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = color,
            unfocusedBorderColor = Color(0xFFE0E0E0)
        ),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun ModernAgregarNotaDialog(
    onDismiss: () -> Unit,
    onNotaAgregada: (NotaEntity) -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var contenido by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                text = "Nueva Nota",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                NotasFormField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = "Título",
                    icon = Icons.Default.Title,
                    color = Color(0xFF1976D2)
                )

                OutlinedTextField(
                    value = contenido,
                    onValueChange = { contenido = it },
                    label = { Text("Contenido") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1976D2),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (titulo.isNotBlank() && contenido.isNotBlank()) {
                        val nota = NotaEntity(
                            titulo = titulo,
                            contenido = contenido,
                            esRecordatorio = false
                        )
                        onNotaAgregada(nota)
                    }
                },
                enabled = titulo.isNotBlank() && contenido.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Guardar Nota", color = Color.White, fontWeight = FontWeight.Medium)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF666666))
            ) {
                Text("Cancelar")
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
} 
