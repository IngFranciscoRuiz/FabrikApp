package com.fjrh.FabrikApp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.fjrh.FabrikApp.domain.model.ConfiguracionStock
import com.fjrh.FabrikApp.ui.viewmodel.ConfiguracionViewModel
import com.fjrh.FabrikApp.ui.utils.validarPrecio
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracionScreen(
    navController: NavController,
    viewModel: ConfiguracionViewModel = hiltViewModel()
) {

    var stockAltoProductos by remember { mutableStateOf("") }
    var stockMedioProductos by remember { mutableStateOf("") }
        var stockBajoProductos by remember { mutableStateOf("") }
    var stockAltoInsumos by remember { mutableStateOf("") }
    var stockMedioInsumos by remember { mutableStateOf("") }
    var stockBajoInsumos by remember { mutableStateOf("") }
    
    // Configuraciones adicionales
    var temaOscuro by remember { mutableStateOf(false) }
    var backupAutomatico by remember { mutableStateOf(true) }
    var frecuenciaBackup by remember { mutableStateOf("7") }
    
    var showSaveDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showLimpiarDatosDialog by remember { mutableStateOf(false) }

    // Estados del ViewModel
    val mensaje by viewModel.mensaje.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val archivosBackup by viewModel.archivosBackup.collectAsState()

    // Cargar configuración inicial
    LaunchedEffect(Unit) {
                        viewModel.configuracion.collect { config ->
                    stockAltoProductos = config.stockAltoProductos.toString()
                    stockMedioProductos = config.stockMedioProductos.toString()
                    stockBajoProductos = config.stockBajoProductos.toString()
                    stockAltoInsumos = config.stockAltoInsumos.toString()
                    stockMedioInsumos = config.stockMedioInsumos.toString()
                    stockBajoInsumos = config.stockBajoInsumos.toString()
            backupAutomatico = config.backupAutomatico
            frecuenciaBackup = config.frecuenciaBackup.toString()
            temaOscuro = config.temaOscuro
        }
    }

    // Aplicar tema automáticamente cuando cambie
    LaunchedEffect(temaOscuro) {
        val config = ConfiguracionStock(
            stockAltoProductos = stockAltoProductos.toFloatOrNull() ?: 100f,
            stockMedioProductos = stockMedioProductos.toFloatOrNull() ?: 50f,
            stockBajoProductos = stockBajoProductos.toFloatOrNull() ?: 25f,
            stockAltoInsumos = stockAltoInsumos.toFloatOrNull() ?: 200f,
            stockMedioInsumos = stockMedioInsumos.toFloatOrNull() ?: 100f,
            stockBajoInsumos = stockBajoInsumos.toFloatOrNull() ?: 50f,
            backupAutomatico = backupAutomatico,
            frecuenciaBackup = frecuenciaBackup.toIntOrNull() ?: 7,
            temaOscuro = temaOscuro
        )
        viewModel.guardarConfiguracion(config)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(top = 60.dp)
                .padding(bottom = 100.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header moderno
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { navController.popBackStack() }
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
            Text(
                    text = "Configuración",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )

                Spacer(modifier = Modifier.weight(1f))
                
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = Color(0xFF1976D2),
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Descripción
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Configuración del Sistema",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF1A1A1A),
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Personaliza FabrikApp según tus necesidades",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF666666),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Umbrales de Stock
            ModernConfigSection(
                title = "Umbrales de Stock",
                icon = Icons.Default.Inventory,
                color = Color(0xFF4CAF50)
            ) {
                // Productos Terminados
                ModernConfigSubsection(
                    title = "Productos Terminados",
                    subtitle = "Niveles de stock para productos terminados"
                ) {
                    ModernConfigField(
                        value = stockAltoProductos,
                        onValueChange = { if (validarPrecio(it)) stockAltoProductos = it },
                        label = "Stock Alto (L)",
                        icon = Icons.Default.TrendingUp,
                        color = Color(0xFF4CAF50)
                    )
                    
                    ModernConfigField(
                        value = stockMedioProductos,
                        onValueChange = { if (validarPrecio(it)) stockMedioProductos = it },
                        label = "Stock Medio (L)",
                        icon = Icons.Default.TrendingFlat,
                        color = Color(0xFFFF9800)
                    )
                    
                    ModernConfigField(
                        value = stockBajoProductos,
                        onValueChange = { if (validarPrecio(it)) stockBajoProductos = it },
                        label = "Stock Bajo (L)",
                        icon = Icons.Default.TrendingDown,
                        color = Color(0xFFF44336)
                    )
                }
                
                // Insumos
                ModernConfigSubsection(
                    title = "Insumos",
                    subtitle = "Niveles de stock para materias primas"
                ) {
                    ModernConfigField(
                        value = stockAltoInsumos,
                        onValueChange = { if (validarPrecio(it)) stockAltoInsumos = it },
                        label = "Stock Alto (kg)",
                        icon = Icons.Default.TrendingUp,
                        color = Color(0xFF4CAF50)
                    )
                    
                    ModernConfigField(
                        value = stockMedioInsumos,
                        onValueChange = { if (validarPrecio(it)) stockMedioInsumos = it },
                        label = "Stock Medio (kg)",
                        icon = Icons.Default.TrendingFlat,
                        color = Color(0xFFFF9800)
                    )
                    
                    ModernConfigField(
                        value = stockBajoInsumos,
                        onValueChange = { if (validarPrecio(it)) stockBajoInsumos = it },
                        label = "Stock Bajo (kg)",
                        icon = Icons.Default.TrendingDown,
                        color = Color(0xFFF44336)
                    )
                }
                
                // Botón Guardar Cambios
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val config = ConfiguracionStock(
                            stockAltoProductos = stockAltoProductos.toFloatOrNull() ?: 100f,
                            stockMedioProductos = stockMedioProductos.toFloatOrNull() ?: 50f,
                            stockBajoProductos = stockBajoProductos.toFloatOrNull() ?: 25f,
                            stockAltoInsumos = stockAltoInsumos.toFloatOrNull() ?: 200f,
                            stockMedioInsumos = stockMedioInsumos.toFloatOrNull() ?: 100f,
                            stockBajoInsumos = stockBajoInsumos.toFloatOrNull() ?: 50f,
                            backupAutomatico = backupAutomatico,
                            frecuenciaBackup = frecuenciaBackup.toIntOrNull() ?: 7,
                            temaOscuro = temaOscuro
                        )
                        viewModel.guardarConfiguracion(config)
                    },
                        modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Guardar Cambios",
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Backup - OCULTO TEMPORALMENTE
            /*
            ModernConfigSection(
                title = "Backup",
                icon = Icons.Default.Backup,
                color = Color(0xFF2196F3)
            ) {
                ModernConfigSwitch(
                    title = "Backup Automático",
                    subtitle = "Realiza copias de seguridad automáticamente",
                    checked = backupAutomatico,
                    onCheckedChange = { backupAutomatico = it },
                    icon = Icons.Default.CloudUpload
                )
                
                if (backupAutomatico) {
                    ModernConfigField(
                        value = frecuenciaBackup,
                        onValueChange = { if (it.matches(Regex("^\\d{0,2}$"))) frecuenciaBackup = it },
                        label = "Frecuencia (días)",
                        icon = Icons.Default.Schedule,
                        color = Color(0xFF2196F3)
                    )
                }
            }
            */

            // Tema
            ModernConfigSection(
                title = "Tema",
                icon = Icons.Default.Palette,
                color = Color(0xFF9C27B0)
            ) {
                ModernConfigSwitch(
                    title = "Tema Oscuro",
                    subtitle = "Activa el modo oscuro para la aplicación",
                    checked = temaOscuro,
                    onCheckedChange = { temaOscuro = it },
                    icon = Icons.Default.DarkMode
                )
            }

            // Datos
            ModernConfigSection(
                title = "Datos",
                icon = Icons.Default.Storage,
                color = Color(0xFFFF5722)
            ) {
                ModernConfigButton(
                    title = "Respaldo de Datos",
                    subtitle = "Exportar e importar respaldos completos",
                    onClick = { navController.navigate("backup") },
                    icon = Icons.Default.Backup,
                    color = Color(0xFF1976D2)
                )
                
                ModernConfigButton(
                    title = "Restablecer Configuración",
                    subtitle = "Vuelve a la configuración por defecto",
                    onClick = { showResetDialog = true },
                    icon = Icons.Default.Restore,
                    color = Color(0xFFFF9800)
                )
                
                ModernConfigButton(
                    title = "Limpiar Todos los Datos",
                    subtitle = "Elimina todos los datos de la aplicación",
                    onClick = { showLimpiarDatosDialog = true },
                    icon = Icons.Default.DeleteForever,
                    color = Color(0xFFD32F2F)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            // Indicador de carga
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF1976D2)
                    )
                }
            }
            
            // Mensajes de estado
            mensaje?.let { msg ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (msg.contains("Error")) 
                            Color(0xFFFFEBEE) 
                        else 
                            Color(0xFFE8F5E8)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (msg.contains("Error")) 
                                Icons.Default.Error 
                            else 
                                Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (msg.contains("Error")) 
                                Color(0xFFD32F2F) 
                            else 
                                Color(0xFF388E3C)
                        )
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                     Text(
                            text = msg,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (msg.contains("Error")) 
                                Color(0xFFD32F2F) 
                            else 
                                Color(0xFF388E3C)
                        )
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
                        IconButton(
                            onClick = { viewModel.limpiarMensaje() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar",
                                tint = if (msg.contains("Error")) 
                                    Color(0xFFD32F2F) 
                                else 
                                    Color(0xFF388E3C)
                            )
                        }
                    }
                }
            }
        }

        // Diálogos
        if (showSaveDialog) {
            ModernConfigSaveDialog(
                onConfirm = {
                                                                                     val config = ConfiguracionStock(
                                stockAltoProductos = stockAltoProductos.toFloatOrNull() ?: 100f,
                                stockMedioProductos = stockMedioProductos.toFloatOrNull() ?: 50f,
                                stockBajoProductos = stockBajoProductos.toFloatOrNull() ?: 25f,
                                stockAltoInsumos = stockAltoInsumos.toFloatOrNull() ?: 200f,
                                stockMedioInsumos = stockMedioInsumos.toFloatOrNull() ?: 100f,
                        stockBajoInsumos = stockBajoInsumos.toFloatOrNull() ?: 50f,
                        backupAutomatico = backupAutomatico,
                        frecuenciaBackup = frecuenciaBackup.toIntOrNull() ?: 7,
                        temaOscuro = temaOscuro
                            )
                            viewModel.guardarConfiguracion(config)
                            showSaveDialog = false
                },
                onDismiss = { showSaveDialog = false }
            )
        }
        
        if (showExportDialog) {
            ModernConfigExportDialog(
                onConfirm = {
                    viewModel.exportarDatos()
                    showExportDialog = false
                },
                onDismiss = { showExportDialog = false }
            )
        }
        
        if (showImportDialog) {
            ModernConfigImportDialog(
                archivosBackup = archivosBackup,
                onImportarArchivo = { archivo ->
                    viewModel.importarDatos(archivo)
                    showImportDialog = false
                },
                onDismiss = { showImportDialog = false }
            )
        }
        
        if (showResetDialog) {
            ModernConfigResetDialog(
                onConfirm = {
                    viewModel.resetearConfiguracion()
                    showResetDialog = false
                },
                onDismiss = { showResetDialog = false }
            )
        }
        
        if (showLimpiarDatosDialog) {
            ModernConfigLimpiarDatosDialog(
                onConfirm = {
                    viewModel.limpiarTodosLosDatos()
                    showLimpiarDatosDialog = false
                },
                onDismiss = { showLimpiarDatosDialog = false }
            )
        }
    }
}

@Composable
fun ModernConfigSection(
    title: String,
    icon: ImageVector,
    color: Color,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            content()
        }
    }
}

@Composable
fun ModernConfigSubsection(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )
        
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF666666)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        content()
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ModernConfigField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    color: Color
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color(0xFF1A1A1A)) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color
            )
        },
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
            keyboardType = KeyboardType.Decimal
        ),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = color,
            unfocusedBorderColor = Color(0xFFE0E0E0),
            focusedTextColor = Color(0xFF1A1A1A),
            unfocusedTextColor = Color(0xFF1A1A1A),
            focusedLabelColor = color,
            unfocusedLabelColor = Color(0xFF666666)
        ),
        shape = RoundedCornerShape(12.dp)
    )
    
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun ModernConfigSwitch(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF1976D2),
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1A1A1A)
            )
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF666666)
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF1976D2)
            )
        )
    }
}

@Composable
fun ModernConfigButton(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    icon: ImageVector,
    color: Color
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun ModernConfigSaveDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Guardar Configuración") },
        text = { Text("¿Estás seguro de que quieres guardar estos cambios?") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun ModernConfigExportDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedLocation by remember { mutableStateOf("Interno") }
    val locations = listOf("Interno", "Descargas", "Documentos")
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                "Exportar Datos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Selecciona dónde guardar el archivo:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF666666)
                )
                
                // Selector de ubicación
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "Ubicación de guardado:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        locations.forEach { location ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedLocation = location }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedLocation == location,
                                    onClick = { selectedLocation = location },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = Color(0xFF1976D2)
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        location,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        when (location) {
                                            "Interno" -> "Almacenamiento interno de la app"
                                            "Descargas" -> "Carpeta de Descargas"
                                            "Documentos" -> "Carpeta de Documentos"
                                            else -> ""
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF666666)
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Información adicional
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF1976D2),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "El archivo se guardará con fecha y hora actual",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF1976D2)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Exportar")
                    }
                },
                dismissButton = {
            TextButton(onClick = onDismiss) {
                        Text("Cancelar")
            }
        }
    )
}

@Composable
fun ModernConfigImportDialog(
    archivosBackup: List<File>,
    onImportarArchivo: (File) -> Unit,
    onDismiss: () -> Unit
) {
    var showFilePicker by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                "Importar Datos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Selecciona el origen de los datos:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF666666)
                )
                
                // Opción 1: Archivos locales
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = null,
                                tint = Color(0xFF1976D2),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Archivos locales",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        if (archivosBackup.isEmpty()) {
                            Text(
                                "No se encontraron archivos de backup",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF666666)
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.heightIn(max = 200.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(archivosBackup) { archivo ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { onImportarArchivo(archivo) }
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.InsertDriveFile,
                                                contentDescription = null,
                                                tint = Color(0xFF1976D2),
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    archivo.name,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.Medium
                                                )
                                                Text(
                                                    "Toca para importar",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = Color(0xFF666666)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                
                // Opción 2: Seleccionar archivo externo
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Storage,
                                contentDescription = null,
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Seleccionar archivo externo",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            "Busca un archivo .json en tu dispositivo",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF666666)
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Button(
                            onClick = { showFilePicker = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Buscar archivo")
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
    
    // Aquí se manejaría la selección de archivo externo
    // Por ahora solo mostramos un mensaje
    if (showFilePicker) {
        AlertDialog(
            onDismissRequest = { showFilePicker = false },
            title = { Text("Selección de archivo") },
            text = { Text("Esta funcionalidad requiere permisos de almacenamiento. Se implementará en una versión futura.") },
            confirmButton = {
                TextButton(onClick = { showFilePicker = false }) {
                    Text("Entendido")
                }
            }
        )
    }
}

@Composable
fun ModernConfigResetDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Restablecer Configuración") },
        text = { Text("¿Estás seguro de que quieres restablecer toda la configuración? Esta acción no se puede deshacer.") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF5722)
                )
            ) {
                Text("Restablecer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun ModernConfigLimpiarDatosDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFFF5722),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("⚠️ LIMPIAR TODOS LOS DATOS")
            }
        },
        text = { 
            Column {
                Text(
                    "Esta acción eliminará PERMANENTEMENTE:",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF5722)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("• Todos los ingredientes del inventario")
                Text("• Todas las fórmulas y recetas")
                Text("• Todo el historial de producción")
                Text("• Todas las ventas registradas")
                Text("• Todas las notas y recordatorios")
                Text("• Toda la configuración")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Esta acción NO se puede deshacer. Asegúrate de hacer un backup antes.",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF5722)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD32F2F)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteForever,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("ELIMINAR TODO")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
} 