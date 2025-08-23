package com.fjrh.FabrikApp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import android.widget.Toast
import com.fjrh.FabrikApp.ui.viewmodel.MainMenuViewModel

import com.fjrh.FabrikApp.ui.components.SubscriptionStatusIndicator
import com.fjrh.FabrikApp.ui.components.MultiUserStatusIndicator
import com.fjrh.FabrikApp.ui.components.SubscriptionGuard
import com.fjrh.FabrikApp.ui.viewmodel.SubscriptionViewModel
import com.fjrh.FabrikApp.ui.viewmodel.LoginViewModel
import com.fjrh.FabrikApp.domain.usecase.SyncManager
import com.fjrh.FabrikApp.domain.model.SubscriptionStatus
import com.fjrh.FabrikApp.data.firebase.WorkspaceHolder
import com.fjrh.FabrikApp.data.firebase.DataMigrationService
import com.fjrh.FabrikApp.ui.viewmodel.MultiUserViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MainMenuScreen(
    navController: NavController,
    subscriptionViewModel: SubscriptionViewModel = hiltViewModel()
) {
    // Inyectar ViewModels
    val multiUserViewModel: MultiUserViewModel = hiltViewModel()
    val mainMenuViewModel: MainMenuViewModel = hiltViewModel()
    // Inyectar FirebaseService
    val firebaseService: com.fjrh.FabrikApp.data.remote.FirebaseService = remember { 
        com.fjrh.FabrikApp.data.remote.FirebaseService() 
    }
    val subscriptionInfo by subscriptionViewModel.subscriptionInfo.collectAsState()
    val isLoading by subscriptionViewModel.isLoading.collectAsState()
    val errorMessage by subscriptionViewModel.errorMessage.collectAsState()
    val successMessage by subscriptionViewModel.successMessage.collectAsState()
    
    // Estados para sincronización
    var isSyncing by remember { mutableStateOf(false) }
    var syncMessage by remember { mutableStateOf<String?>(null) }
    var isLoggedIn by remember { mutableStateOf(false) }
    
    val isMultiUserActive by multiUserViewModel.isMultiUserActive.collectAsState()
    var tapCount by remember { mutableStateOf(0) }
    var lastTapTime by remember { mutableStateOf(0L) }
    var showMultiUserMessage by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    

    
    // Mostrar mensaje de confirmación por 3 segundos
    LaunchedEffect(isMultiUserActive) {
        if (isMultiUserActive) {
            showMultiUserMessage = true
            delay(3000)
            showMultiUserMessage = false
        }
    }
    
    // Bloquear toda la app si el trial expiró
    SubscriptionGuard(
        subscriptionInfo = subscriptionInfo,
        featureName = "main_app",
        onSubscribe = { 
            // Ir directo al Paywall para que pueda usar Google Play Billing
            navController.navigate("paywall") {
                // No agregar a la pila de navegación si ya estamos en paywall
                launchSingleTop = true
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .padding(top = 40.dp)
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { 
                    WelcomeHeader(
                        onSettingsClick = { navController.navigate("configuracion") },
                        onMultiUserActivated = {
                            multiUserViewModel.setMultiUserActive(true)
                            // Navegar automáticamente a login cuando se active multiusuario
                            navController.navigate("login")
                        },
                        tapCount = tapCount,
                        onTapCountChange = { tapCount = it },
                        lastTapTime = lastTapTime,
                        onLastTapTimeChange = { lastTapTime = it }
                    )
                }
                item { 
                    SubscriptionStatusIndicator(
                        subscriptionInfo = subscriptionInfo,
                        onSubscribe = { navController.navigate("paywall") }
                    )
                }
                item { 
                    if (isMultiUserActive) {
                        MultiUserStatusIndicator(
                            isMultiUserActive = isMultiUserActive,
                            currentUser = "Usuario Principal",
                            onSwitchUser = { 
                                // Cerrar sesión y volver a login
                                firebaseService.signOut()
                                multiUserViewModel.clearMultiUserState()
                                navController.navigate("login") {
                                    popUpTo("menu") { inclusive = true }
                                }
                            }
                        )
                    }
                }
                
                // Botón para copiar WID del workspace
                item {
                    // Debug logs
                    println("MainMenuScreen: isMultiUserActive = $isMultiUserActive")
                    println("MainMenuScreen: WorkspaceHolder.isInitialized() = ${WorkspaceHolder.isInitialized()}")
                    
                    if (isMultiUserActive && WorkspaceHolder.isInitialized()) {
                        val clipboard = LocalClipboardManager.current
                        val context = LocalContext.current
                        val wid = remember { WorkspaceHolder.get() }
                        
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Código de Workspace",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Text(
                                        text = wid,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        clipboard.setText(AnnotatedString(wid))
                                        Toast.makeText(context, "Código copiado al portapapeles", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.ContentCopy,
                                        contentDescription = "Copiar código",
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Removida tarjeta de debug "Estado de Workspace" para producción
                // Removido botón de testing temporal para producción
                item { MainFeaturesSection(navController) }
                item { 
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MainFeatureCard(
                            icon = Icons.Default.Inventory,
                            title = "Stock Productos",
                            onClick = { navController.navigate("stock_productos") },
                            modifier = Modifier.weight(1f)
                        )
                        MainFeatureCard(
                            icon = Icons.Default.History,
                            title = "Historial",
                            onClick = { navController.navigate("historial") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item { MainKPIsSection(mainMenuViewModel) }
                item { MainQuickAccessSection(navController) }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
            
            // Bottom navigation ahora está en GlobalLayout
            
            // Indicador de toques para multiusuario - OCULTO (funcionalidad mantenida)
            // if (tapCount > 0 && tapCount < 3) {
            //     Card(
            //         modifier = Modifier
            //             .align(Alignment.TopCenter)
            //             .padding(top = 100.dp)
            //             .padding(horizontal = 20.dp),
            //         colors = CardDefaults.cardColors(
            //             containerColor = Color(0xFF2196F3)
            //         ),
            //         elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            //     ) {
            //         Row(
            //             modifier = Modifier
            //                 .fillMaxWidth()
            //                 .padding(16.dp),
            //             verticalAlignment = Alignment.CenterVertically
            //         ) {
            //             Icon(
            //                 imageVector = Icons.Default.TouchApp,
            //                 contentDescription = null,
            //                 tint = Color.White,
            //                 modifier = Modifier.size(24.dp)
            //         )
            //             Spacer(modifier = Modifier.width(12.dp))
            //             Text(
            //                 text = "Toques: $tapCount/3",
            //                 style = MaterialTheme.typography.bodyLarge,
            //                 color = Color.White,
            //                 fontWeight = FontWeight.Bold
            //             )
            //         }
            //     }
            // }
            
            // Mensaje de confirmación de multiusuario
            if (showMultiUserMessage) {
                Card(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 100.dp)
                        .padding(horizontal = 20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF4CAF50)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "¡Modo Multiusuario Activado!",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            // Indicador de sincronización
            if (isSyncing) {
                Card(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 160.dp)
                        .padding(horizontal = 20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE3F2FD)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color(0xFF1976D2)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = syncMessage ?: "Sincronizando...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF1976D2),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WelcomeHeader(
    onSettingsClick: () -> Unit,
    onMultiUserActivated: () -> Unit,
    tapCount: Int,
    onTapCountChange: (Int) -> Unit,
    lastTapTime: Long,
    onLastTapTimeChange: (Long) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "FabrikApp",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .weight(1f)
                .clickable {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastTapTime < 2000) { // 2 segundos para contar toques
                        val newTapCount = tapCount + 1
                        onTapCountChange(newTapCount)
                        onLastTapTimeChange(currentTime)
                        
                        if (newTapCount >= 3) {
                            onMultiUserActivated()
                            onTapCountChange(0) // Reset contador
                        }
                    } else {
                        onTapCountChange(1)
                        onLastTapTimeChange(currentTime)
                    }
                }
        )
        
        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = Color.White,
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Configuración",
                tint = Color(0xFF1976D2),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun MainTrialBanner() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Período de prueba de 7 días",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF1976D2),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Restan 7 días",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF1976D2)
                )
            }
            
            Button(
                onClick = { /* Activar suscripción */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Activar",
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun MainFeaturesSection(navController: NavController) {
    Column {
        Text(
            text = "Funciones principales",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        // Grid 2x2
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MainFeatureCard(
                    icon = Icons.Default.Inventory,
                    title = "Gestión de inventario",
                    onClick = { navController.navigate("inventario") },
                    modifier = Modifier.weight(1f)
                )
                MainFeatureCard(
                    icon = Icons.Default.Science,
                    title = "Control de producción",
                    onClick = { navController.navigate("produccion") },
                    modifier = Modifier.weight(1f)
                )
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MainFeatureCard(
                    icon = Icons.Default.Description,
                    title = "Fórmulas de producción",
                    onClick = { navController.navigate("formulas") },
                    modifier = Modifier.weight(1f)
                )
                MainFeatureCard(
                    icon = Icons.Default.AttachMoney,
                    title = "Ventas y Finanzas",
                    onClick = { navController.navigate("finanzas_hub") },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun MainKPIsSection(viewModel: MainMenuViewModel) {
    val ventasHoy by viewModel.ventasHoy.collectAsState()
    val stockBajo by viewModel.stockBajo.collectAsState()
    val produccionHoy by viewModel.produccionHoy.collectAsState()
    
    Column {
        Text(
            text = "KPIs",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        // Grid 2x2 de KPIs
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MainKPICard("Ventas hoy", viewModel.formatearVentasHoy(), Color(0xFF4CAF50), Modifier.weight(1f))
                MainKPICard("Stock bajo", viewModel.formatearStockBajo(), Color(0xFFFF9800), Modifier.weight(1f))
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MainKPICard("Producción hoy", viewModel.formatearProduccionHoy(), Color(0xFF2196F3), Modifier.weight(1f))
                MainKPICard("Pedidos pendientes", viewModel.formatearPedidosPendientes(), Color(0xFFFF5722), Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun MainQuickAccessSection(navController: NavController) {
    Column {
        Text(
            text = "Accesos rápidos",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickAccessButton(
                icon = Icons.Default.Add,
                label = "Nuevo lote",
                onClick = { navController.navigate("produccion") },
                modifier = Modifier.weight(1f)
            )
            QuickAccessButton(
                icon = Icons.Default.WaterDrop,
                label = "Añadir insumo",
                onClick = { navController.navigate("agregar_ingrediente") },
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(6.dp))
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickAccessButton(
                icon = Icons.Default.ShoppingCart,
                label = "Registrar venta",
                onClick = { navController.navigate("ventas") },
                modifier = Modifier.weight(1f)
            )
            QuickAccessButton(
                icon = Icons.Default.ShoppingCart,
                label = "Nuevo pedido",
                onClick = { navController.navigate("pedidos_proveedor") },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun MainFeatureCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(60.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF1976D2),
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun MainKPICard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(60.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
                Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = color,
                    fontWeight = FontWeight.Bold
                )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF666666),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun QuickAccessButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(55.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF1976D2),
                modifier = Modifier.size(18.dp)
            )
            
            Spacer(modifier = Modifier.width(6.dp))
            
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF1A1A1A),
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
