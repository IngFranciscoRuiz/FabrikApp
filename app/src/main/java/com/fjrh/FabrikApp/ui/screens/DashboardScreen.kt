package com.fjrh.FabrikApp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fjrh.FabrikApp.ui.theme.*

@Composable
fun DashboardScreen(
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FabrikAppGrayLight)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        DashboardHeader()
        
        // Trial Banner
        TrialBanner()
        
        // Main Features Grid
        MainFeaturesGrid(navController)
        
        // KPIs Section
        KPIsSection()
        
        // Quick Access
        QuickAccessSection(navController)
        
        Spacer(modifier = Modifier.height(100.dp)) // Space for bottom navigation
    }
}

@Composable
fun DashboardHeader() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "FabrikApp",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = FabrikAppBlack
            )
            
            Text(
                text = "Bienvenido de vuelta a tu panel de control",
                style = MaterialTheme.typography.bodyMedium,
                color = FabrikAppGray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun TrialBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Período de prueba de 7 días",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = FabrikAppBlack
                )
                Text(
                    text = "Restan 7 días",
                    style = MaterialTheme.typography.bodySmall,
                    color = FabrikAppGray
                )
            }
            
            Button(
                onClick = { /* TODO: Activar suscripción */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = FabrikAppBlueLight
                ),
                modifier = Modifier.height(36.dp)
            ) {
                Text(
                    text = "Activar",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun MainFeaturesGrid(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Funciones principales",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = FabrikAppBlack,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Row 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FeatureCard(
                icon = Icons.Outlined.Inventory,
                title = "Gestión de inventario",
                onClick = { navController.navigate("inventario") },
                modifier = Modifier.weight(1f)
            )
            FeatureCard(
                icon = Icons.Outlined.Science,
                title = "Control de producción",
                onClick = { navController.navigate("produccion") },
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Row 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FeatureCard(
                icon = Icons.Outlined.Description,
                title = "Fórmulas de producción",
                onClick = { navController.navigate("formulas") },
                modifier = Modifier.weight(1f)
            )
            FeatureCard(
                icon = Icons.Outlined.AttachMoney,
                title = "Ventas y Finanzas",
                onClick = { navController.navigate("ventas") },
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Row 3 (single wide card)
        FeatureCard(
            icon = Icons.Outlined.Settings,
            title = "Backup y ajustes",
            onClick = { navController.navigate("configuracion") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun FeatureCard(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = FabrikAppBlue
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = FabrikAppBlack
            )
        }
    }
}

@Composable
fun KPIsSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "KPIs",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = FabrikAppBlack,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            KPICard(
                title = "Stock OK",
                value = "8",
                color = FabrikAppGreen,
                modifier = Modifier.weight(1f)
            )
            KPICard(
                title = "Stock bajo",
                value = "3",
                color = FabrikAppOrange,
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            KPICard(
                title = "Producción hoy",
                value = "2",
                color = FabrikAppBlue,
                modifier = Modifier.weight(1f)
            )
            KPICard(
                title = "Pedidos pendientes",
                value = "1",
                color = FabrikAppRed,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun KPICard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
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
                fontWeight = FontWeight.Bold,
                color = color
            )
            
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = FabrikAppGray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun QuickAccessSection(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Accesos rápidos",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = FabrikAppBlack,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            QuickAccessButton(
                icon = Icons.Filled.Add,
                label = "Nuevo lote",
                onClick = { navController.navigate("nueva_produccion") }
            )
            QuickAccessButton(
                icon = Icons.Filled.WaterDrop,
                label = "Añadir insumo",
                onClick = { navController.navigate("agregar_ingrediente") }
            )
            QuickAccessButton(
                icon = Icons.Filled.ShoppingCart,
                label = "Registrar venta",
                onClick = { navController.navigate("nueva_venta") }
            )
        }
    }
}

@Composable
fun QuickAccessButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FloatingActionButton(
            onClick = onClick,
            containerColor = FabrikAppBlue,
            modifier = Modifier.size(56.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = FabrikAppBlack,
            textAlign = TextAlign.Center
        )
    }
}


