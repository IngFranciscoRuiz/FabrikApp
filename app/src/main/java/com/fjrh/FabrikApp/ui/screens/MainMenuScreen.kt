package com.fjrh.FabrikApp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.fjrh.FabrikApp.ui.viewmodel.MainMenuViewModel
import com.fjrh.FabrikApp.ui.components.FabrikBottomNavigation

@Composable
fun MainMenuScreen(
    navController: NavController,
    viewModel: MainMenuViewModel = hiltViewModel()
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(top = 60.dp)
                .padding(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { 
                WelcomeHeader(
                    onSettingsClick = { navController.navigate("configuracion") }
                )
            }
            item { MainTrialBanner() }
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
            item { MainKPIsSection() }
            item { MainQuickAccessSection(navController) }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
        
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        ) {
            FabrikBottomNavigation(navController)
        }
    }
}

@Composable
fun WelcomeHeader(
    onSettingsClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Hola, Francisco",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF1A1A1A),
            fontWeight = FontWeight.Bold
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
            color = Color(0xFF1A1A1A),
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
fun MainKPIsSection() {
    Column {
        Text(
            text = "KPIs",
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFF1A1A1A),
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
                MainKPICard("Stock OK", "8", Color(0xFF4CAF50), Modifier.weight(1f))
                MainKPICard("Stock bajo", "3", Color(0xFFFF9800), Modifier.weight(1f))
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MainKPICard("Producción hoy", "2", Color(0xFF2196F3), Modifier.weight(1f))
                MainKPICard("Pedidos pendientes", "1", Color(0xFFFF5722), Modifier.weight(1f))
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
            color = Color(0xFF1A1A1A),
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
                color = Color(0xFF1A1A1A),
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
