package com.fjrh.FabrikApp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.fjrh.FabrikApp.domain.model.ConfiguracionStock
import com.fjrh.FabrikApp.ui.viewmodel.ConfiguracionViewModel
import com.fjrh.FabrikApp.ui.utils.validarPrecio

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
    var notificacionesActivas by remember { mutableStateOf(true) }
    var alertasStockBajo by remember { mutableStateOf(true) }
    var alertasStockAlto by remember { mutableStateOf(false) }
    var backupAutomatico by remember { mutableStateOf(true) }
    var frecuenciaBackup by remember { mutableStateOf("7") }
    
    var showSaveDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    // Cargar configuración actual
    LaunchedEffect(Unit) {
        viewModel.configuracion.collect { config ->
            stockAltoProductos = config.stockAltoProductos.toString()
            stockMedioProductos = config.stockMedioProductos.toString()
            stockBajoProductos = config.stockBajoProductos.toString()
            stockAltoInsumos = config.stockAltoInsumos.toString()
            stockMedioInsumos = config.stockMedioInsumos.toString()
            stockBajoInsumos = config.stockBajoInsumos.toString()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Configuración",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showSaveDialog = true }) {
                        Icon(Icons.Default.Save, contentDescription = "Guardar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header con estadísticas
            ConfiguracionHeader()
            
            // Secciones de configuración
            ConfiguracionStockSection(
                stockAltoProductos = stockAltoProductos,
                stockMedioProductos = stockMedioProductos,
                stockBajoProductos = stockBajoProductos,
                stockAltoInsumos = stockAltoInsumos,
                stockMedioInsumos = stockMedioInsumos,
                stockBajoInsumos = stockBajoInsumos,
                onStockAltoProductosChange = { stockAltoProductos = it },
                onStockMedioProductosChange = { stockMedioProductos = it },
                onStockBajoProductosChange = { stockBajoProductos = it },
                onStockAltoInsumosChange = { stockAltoInsumos = it },
                onStockMedioInsumosChange = { stockMedioInsumos = it },
                onStockBajoInsumosChange = { stockBajoInsumos = it }
            )
            
            ConfiguracionAlertasSection(
                alertasStockBajo = alertasStockBajo,
                alertasStockAlto = alertasStockAlto,
                onAlertasStockBajoChange = { alertasStockBajo = it },
                onAlertasStockAltoChange = { alertasStockAlto = it }
            )
            
            ConfiguracionBackupSection(
                backupAutomatico = backupAutomatico,
                frecuenciaBackup = frecuenciaBackup,
                onBackupAutomaticoChange = { backupAutomatico = it },
                onFrecuenciaBackupChange = { frecuenciaBackup = it }
            )
            
            ConfiguracionTemaSection(
                temaOscuro = temaOscuro,
                onTemaOscuroChange = { temaOscuro = it }
            )
            
            ConfiguracionNotificacionesSection(
                notificacionesActivas = notificacionesActivas,
                onNotificacionesActivasChange = { notificacionesActivas = it }
            )
            
            ConfiguracionDatosSection(
                onExportarClick = { showExportDialog = true },
                onImportarClick = { showImportDialog = true },
                onResetClick = { showResetDialog = true }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }

        // Diálogos
        if (showSaveDialog) {
            ConfiguracionSaveDialog(
                onConfirm = {
                    val config = ConfiguracionStock(
                        stockAltoProductos = stockAltoProductos.toFloatOrNull() ?: 100f,
                        stockMedioProductos = stockMedioProductos.toFloatOrNull() ?: 50f,
                        stockBajoProductos = stockBajoProductos.toFloatOrNull() ?: 25f,
                        stockAltoInsumos = stockAltoInsumos.toFloatOrNull() ?: 200f,
                        stockMedioInsumos = stockMedioInsumos.toFloatOrNull() ?: 100f,
                        stockBajoInsumos = stockBajoInsumos.toFloatOrNull() ?: 50f
                    )
                    viewModel.guardarConfiguracion(config)
                    showSaveDialog = false
                },
                onDismiss = { showSaveDialog = false }
            )
        }
        
        if (showExportDialog) {
            ConfiguracionExportDialog(
                onConfirm = {
                    // Implementar exportación
                    showExportDialog = false
                },
                onDismiss = { showExportDialog = false }
            )
        }
        
        if (showImportDialog) {
            ConfiguracionImportDialog(
                onConfirm = {
                    // Implementar importación
                    showImportDialog = false
                },
                onDismiss = { showImportDialog = false }
            )
        }
        
        if (showResetDialog) {
            ConfiguracionResetDialog(
                onConfirm = {
                    // Implementar reset
                    showResetDialog = false
                },
                onDismiss = { showResetDialog = false }
            )
        }
    }
}

@Composable
fun ConfiguracionHeader() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Configuración del Sistema",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Text(
                text = "Personaliza FabrikApp según tus necesidades",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ConfiguracionStockSection(
    stockAltoProductos: String,
    stockMedioProductos: String,
    stockBajoProductos: String,
    stockAltoInsumos: String,
    stockMedioInsumos: String,
    stockBajoInsumos: String,
    onStockAltoProductosChange: (String) -> Unit,
    onStockMedioProductosChange: (String) -> Unit,
    onStockBajoProductosChange: (String) -> Unit,
    onStockAltoInsumosChange: (String) -> Unit,
    onStockMedioInsumosChange: (String) -> Unit,
    onStockBajoInsumosChange: (String) -> Unit
) {
    ConfiguracionSection(
        title = "📊 Umbrales de Stock",
        icon = Icons.Default.Inventory
    ) {
        // Productos Terminados
        ConfiguracionSubsection(
            title = "🏭 Productos Terminados",
            subtitle = "Configura los niveles de stock para productos terminados"
        ) {
            ConfiguracionTextField(
                value = stockAltoProductos,
                onValueChange = { if (validarPrecio(it)) onStockAltoProductosChange(it) },
                label = "Stock Alto (L)",
                icon = Icons.Default.TrendingUp,
                color = Color(0xFF4CAF50)
            )
            
            ConfiguracionTextField(
                value = stockMedioProductos,
                onValueChange = { if (validarPrecio(it)) onStockMedioProductosChange(it) },
                label = "Stock Medio (L)",
                icon = Icons.Default.TrendingFlat,
                color = Color(0xFFFF9800)
            )
            
            ConfiguracionTextField(
                value = stockBajoProductos,
                onValueChange = { if (validarPrecio(it)) onStockBajoProductosChange(it) },
                label = "Stock Bajo (L)",
                icon = Icons.Default.TrendingDown,
                color = Color(0xFFF44336)
            )
        }
        
        // Insumos
        ConfiguracionSubsection(
            title = "🧪 Insumos",
            subtitle = "Configura los niveles de stock para materias primas"
        ) {
            ConfiguracionTextField(
                value = stockAltoInsumos,
                onValueChange = { if (validarPrecio(it)) onStockAltoInsumosChange(it) },
                label = "Stock Alto (kg)",
                icon = Icons.Default.TrendingUp,
                color = Color(0xFF4CAF50)
            )
            
            ConfiguracionTextField(
                value = stockMedioInsumos,
                onValueChange = { if (validarPrecio(it)) onStockMedioInsumosChange(it) },
                label = "Stock Medio (kg)",
                icon = Icons.Default.TrendingFlat,
                color = Color(0xFFFF9800)
            )
            
            ConfiguracionTextField(
                value = stockBajoInsumos,
                onValueChange = { if (validarPrecio(it)) onStockBajoInsumosChange(it) },
                label = "Stock Bajo (kg)",
                icon = Icons.Default.TrendingDown,
                color = Color(0xFFF44336)
            )
        }
    }
}

@Composable
fun ConfiguracionAlertasSection(
    alertasStockBajo: Boolean,
    alertasStockAlto: Boolean,
    onAlertasStockBajoChange: (Boolean) -> Unit,
    onAlertasStockAltoChange: (Boolean) -> Unit
) {
    ConfiguracionSection(
        title = "🔔 Alertas",
        icon = Icons.Default.Notifications
    ) {
        ConfiguracionSwitch(
            title = "Alertas de Stock Bajo",
            subtitle = "Recibe notificaciones cuando el stock esté bajo",
            checked = alertasStockBajo,
            onCheckedChange = onAlertasStockBajoChange,
            icon = Icons.Default.Warning
        )
        
        ConfiguracionSwitch(
            title = "Alertas de Stock Alto",
            subtitle = "Recibe notificaciones cuando el stock esté alto",
            checked = alertasStockAlto,
            onCheckedChange = onAlertasStockAltoChange,
            icon = Icons.Default.Info
        )
    }
}

@Composable
fun ConfiguracionBackupSection(
    backupAutomatico: Boolean,
    frecuenciaBackup: String,
    onBackupAutomaticoChange: (Boolean) -> Unit,
    onFrecuenciaBackupChange: (String) -> Unit
) {
    ConfiguracionSection(
        title = "💾 Backup",
        icon = Icons.Default.Backup
    ) {
        ConfiguracionSwitch(
            title = "Backup Automático",
            subtitle = "Realiza copias de seguridad automáticamente",
            checked = backupAutomatico,
            onCheckedChange = onBackupAutomaticoChange,
            icon = Icons.Default.CloudUpload
        )
        
        if (backupAutomatico) {
            ConfiguracionTextField(
                value = frecuenciaBackup,
                onValueChange = { if (it.matches(Regex("^\\d{0,2}$"))) onFrecuenciaBackupChange(it) },
                label = "Frecuencia (días)",
                icon = Icons.Default.Schedule,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun ConfiguracionTemaSection(
    temaOscuro: Boolean,
    onTemaOscuroChange: (Boolean) -> Unit
) {
    ConfiguracionSection(
        title = "🎨 Tema",
        icon = Icons.Default.Palette
    ) {
        ConfiguracionSwitch(
            title = "Tema Oscuro",
            subtitle = "Activa el modo oscuro para la aplicación",
            checked = temaOscuro,
            onCheckedChange = onTemaOscuroChange,
            icon = Icons.Default.DarkMode
        )
    }
}

@Composable
fun ConfiguracionNotificacionesSection(
    notificacionesActivas: Boolean,
    onNotificacionesActivasChange: (Boolean) -> Unit
) {
    ConfiguracionSection(
        title = "📱 Notificaciones",
        icon = Icons.Default.NotificationsActive
    ) {
        ConfiguracionSwitch(
            title = "Notificaciones Push",
            subtitle = "Recibe notificaciones en tiempo real",
            checked = notificacionesActivas,
            onCheckedChange = onNotificacionesActivasChange,
            icon = Icons.Default.NotificationsActive
        )
    }
}

@Composable
fun ConfiguracionDatosSection(
    onExportarClick: () -> Unit,
    onImportarClick: () -> Unit,
    onResetClick: () -> Unit
) {
    ConfiguracionSection(
        title = "📁 Datos",
        icon = Icons.Default.Storage
    ) {
        ConfiguracionButton(
            title = "Exportar Datos",
            subtitle = "Descarga una copia de todos los datos",
            onClick = onExportarClick,
            icon = Icons.Default.Download,
            color = Color(0xFF4CAF50)
        )
        
        ConfiguracionButton(
            title = "Importar Datos",
            subtitle = "Restaura datos desde un archivo",
            onClick = onImportarClick,
            icon = Icons.Default.Upload,
            color = Color(0xFF2196F3)
        )
        
        ConfiguracionButton(
            title = "Restablecer Configuración",
            subtitle = "Vuelve a la configuración por defecto",
            onClick = onResetClick,
            icon = Icons.Default.Restore,
            color = Color(0xFFFF5722)
        )
    }
}

@Composable
fun ConfiguracionSection(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            content()
        }
    }
}

@Composable
fun ConfiguracionSubsection(
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
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        content()
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ConfiguracionTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
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
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
            keyboardType = KeyboardType.Decimal
        ),
        modifier = Modifier.fillMaxWidth(),

        singleLine = true
    )
    
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun ConfiguracionSwitch(
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
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
        
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun ConfiguracionButton(
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
fun ConfiguracionSaveDialog(
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
fun ConfiguracionExportDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Exportar Datos") },
        text = { Text("¿Deseas exportar todos los datos de la aplicación?") },
        confirmButton = {
            Button(onClick = onConfirm) {
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
fun ConfiguracionImportDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Importar Datos") },
        text = { Text("¿Deseas importar datos desde un archivo? Esto sobrescribirá los datos actuales.") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Importar")
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
fun ConfiguracionResetDialog(
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