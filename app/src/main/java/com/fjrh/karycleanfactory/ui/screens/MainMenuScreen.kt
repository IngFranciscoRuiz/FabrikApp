package com.fjrh.karycleanfactory.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fjrh.karycleanfactory.R

@Composable
fun MainMenuScreen(navController: NavController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF000000), // Negro profundo
                        Color(0xFF0D1A2F)  // Azul metálico oscuro
                    )
                )
            )
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // HEADER CON MÉTRICAS
        item {
            HeaderSection()
        }

        // SECCIÓN PREPARACIÓN
        item {
            WorkflowSection(
                title = "📦 PREPARACIÓN",
                icon = Icons.Default.Inventory,
                color = Color(0xFF4CAF50),
                items = listOf(
                    MenuItem("Inventario", Icons.Default.Inventory, "Gestionar ingredientes") { navController.navigate("inventario") },
                    MenuItem("Fórmulas", Icons.Default.Science, "Ver fórmulas existentes") { navController.navigate("formulas") },
                    MenuItem("Nueva Fórmula", Icons.Default.Add, "Crear nueva fórmula") { navController.navigate("nueva_formula") }
                )
            )
        }

        // SECCIÓN PRODUCCIÓN
        item {
            WorkflowSection(
                title = "🏭 PRODUCCIÓN",
                icon = Icons.Default.Factory,
                color = Color(0xFF2196F3),
                items = listOf(
                    MenuItem("Producción", Icons.Default.Factory, "Iniciar producción") { navController.navigate("produccion") },
                    MenuItem("Stock Productos", Icons.Default.Inventory, "Ver stock terminado") { navController.navigate("stock_productos") }
                )
            )
        }

        // SECCIÓN GESTIÓN COMERCIAL
        item {
            WorkflowSection(
                title = "💼 GESTIÓN COMERCIAL",
                icon = Icons.Default.ShoppingCart,
                color = Color(0xFFFF9800),
                items = listOf(
                    MenuItem("Ventas", Icons.Default.ShoppingCart, "Registrar ventas") { navController.navigate("ventas") },
                    MenuItem("Pedidos Proveedor", Icons.Default.LocalShipping, "Gestionar pedidos") { navController.navigate("pedidos_proveedor") },
                    MenuItem("Balance", Icons.Default.AccountBalance, "Ver balance financiero") { navController.navigate("balance") }
                )
            )
        }

        // SECCIÓN ANALÍTICAS
        item {
            WorkflowSection(
                title = "📊 ANALÍTICAS",
                icon = Icons.Default.History,
                color = Color(0xFF9C27B0),
                items = listOf(
                    MenuItem("Historial", Icons.Default.History, "Ver historial de producción") { navController.navigate("historial") }
                )
            )
        }

        // SECCIÓN HERRAMIENTAS
        item {
            WorkflowSection(
                title = "🛠️ HERRAMIENTAS",
                icon = Icons.Default.Note,
                color = Color(0xFF607D8B),
                items = listOf(
                    MenuItem("Notas", Icons.Default.Note, "Notas y recordatorios") { navController.navigate("notas") }
                )
            )
        }

        // ESPACIO FINAL
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun HeaderSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo
        Image(
            painter = painterResource(id = R.drawable.karyclean_logo),
            contentDescription = "Logo KaryClean",
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Título
        Text(
            text = "KaryClean Factory",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Subtítulo
        Text(
            text = "Sistema de Gestión Industrial",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Métricas rápidas (placeholder - se pueden conectar con datos reales)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MetricCard("Ventas Hoy", "$1,250", Color(0xFF4CAF50))
            MetricCard("Stock Bajo", "3 items", Color(0xFFFF5722))
            MetricCard("Producción", "85%", Color(0xFF2196F3))
        }
    }
}

@Composable
fun MetricCard(title: String, value: String, color: Color) {
    Card(
        modifier = Modifier
            .padding(horizontal = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun WorkflowSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    items: List<MenuItem>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.05f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header de la sección
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
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Items de la sección
            items.forEach { item ->
                WorkflowMenuItem(item = item)
                if (item != items.last()) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun WorkflowMenuItem(item: MenuItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable { item.onClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

data class MenuItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val description: String,
    val onClick: () -> Unit
)
