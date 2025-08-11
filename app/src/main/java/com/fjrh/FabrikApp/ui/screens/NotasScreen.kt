package com.fjrh.FabrikApp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Note
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fjrh.FabrikApp.data.local.entity.NotaEntity
import com.fjrh.FabrikApp.ui.viewmodel.NotasViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotasScreen(
    viewModel: NotasViewModel = hiltViewModel()
) {
    var showAddDialog by remember { mutableStateOf(false) }
    val notas by viewModel.notas.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notas y Recordatorios") },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar nota")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5DC)) // Color papel
                .padding(16.dp)
        ) {
            if (notas.isEmpty()) {
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
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No hay notas registradas",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(notas) { nota ->
                        NotaCard(
                            nota = nota,
                            onDelete = { viewModel.eliminarNota(nota) },
                            onEdit = { notaEditada -> viewModel.actualizarNota(notaEditada) }
                        )
                    }
                }
            }
        }

        if (showAddDialog) {
            AgregarNotaDialog(
                onDismiss = { showAddDialog = false },
                onNotaAgregada = { nota ->
                    viewModel.agregarNota(nota)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun NotaCard(
    nota: NotaEntity,
    onDelete: () -> Unit,
    onEdit: (NotaEntity) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editedNota by remember { mutableStateOf(nota) }
    
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val colorNota = when {
        nota.esRecordatorio -> Color(0xFFFFE0B2) // Naranja claro para recordatorios
        nota.esCompletada -> Color(0xFFE8F5E8) // Verde claro para completadas
        else -> Color(0xFFFFF8E1) // Amarillo claro para notas normales
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colorNota)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Encabezado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isEditing) "Editando..." else nota.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Row {
                    if (isEditing) {
                        IconButton(
                            onClick = {
                                onEdit(editedNota)
                                isEditing = false
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Guardar",
                                tint = Color.Green
                            )
                        }
                        IconButton(
                            onClick = {
                                editedNota = nota
                                isEditing = false
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Cancelar",
                                tint = Color.Red
                            )
                        }
                    } else {
                        IconButton(onClick = { isEditing = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                        }
                        IconButton(onClick = onDelete) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Eliminar",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Contenido
            if (isEditing) {
                OutlinedTextField(
                    value = editedNota.titulo,
                    onValueChange = { editedNota = editedNota.copy(titulo = it) },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = editedNota.contenido,
                    onValueChange = { editedNota = editedNota.copy(contenido = it) },
                    label = { Text("Contenido") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            } else {
                Text(
                    text = nota.contenido,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Información adicional
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = dateFormat.format(Date(nota.fecha)),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                
                if (nota.esRecordatorio) {
                    Text(
                        text = "📅 Recordatorio",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFE65100)
                    )
                }
                
                if (nota.esCompletada) {
                    Text(
                        text = "✅ Completada",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Green
                    )
                }
            }
        }
    }
}

@Composable
fun AgregarNotaDialog(
    onDismiss: () -> Unit,
    onNotaAgregada: (NotaEntity) -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var contenido by remember { mutableStateOf("") }
    var esRecordatorio by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Nota") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = contenido,
                    onValueChange = { contenido = it },
                    label = { Text("Contenido") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = esRecordatorio,
                        onCheckedChange = { esRecordatorio = it }
                    )
                    Text("Es recordatorio")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (titulo.isNotBlank() && contenido.isNotBlank()) {
                        val nota = NotaEntity(
                            titulo = titulo,
                            contenido = contenido,
                            esRecordatorio = esRecordatorio
                        )
                        onNotaAgregada(nota)
                    }
                },
                enabled = titulo.isNotBlank() && contenido.isNotBlank()
            ) {
                Text("Guardar Nota")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
} 
