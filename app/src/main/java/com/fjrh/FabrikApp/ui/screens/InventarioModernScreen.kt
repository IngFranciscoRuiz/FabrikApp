package com.fjrh.FabrikApp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fjrh.FabrikApp.ui.theme.*

@Composable
fun InventarioModernScreen(
    navController: NavController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FabrikAppGrayLight)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            InventarioHeader(navController)
            
            // Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                items(getMockInventarioItems()) { item ->
                    InventarioItemCard(item = item)
                }
                
                item {
                    Spacer(modifier = Modifier.height(16.dp)) // Reduced space for bottom nav
                }
            }
        }
        
        // Floating Action Button
        FloatingActionButton(
            onClick = { navController.navigate("agregar_ingrediente") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .padding(bottom = 80.dp), // Extra padding for bottom nav
            containerColor = FabrikAppBlue,
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Agregar insumo",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun InventarioHeader(navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "FabrikApp",
                    style = MaterialTheme.typography.titleSmall,
                    color = FabrikAppGray,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Gestión de Inventario",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = FabrikAppBlack
                )
            }
            
            IconButton(
                onClick = { navController.navigateUp() }
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = FabrikAppGray
                )
            }
        }
    }
}

@Composable
fun InventarioItemCard(item: InventarioItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status indicator
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(item.statusColor)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Item info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.nombre,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = FabrikAppBlack
                )
                
                Text(
                    text = "${item.cantidad} ${item.unidad}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = FabrikAppBlue,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = item.proveedor,
                    style = MaterialTheme.typography.bodySmall,
                    color = FabrikAppGray
                )
                
                Text(
                    text = item.lote,
                    style = MaterialTheme.typography.bodySmall,
                    color = FabrikAppGray
                )
            }
            
            // Arrow indicator
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = FabrikAppGray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// Data class for inventario items
data class InventarioItem(
    val nombre: String,
    val cantidad: String,
    val unidad: String,
    val proveedor: String,
    val lote: String,
    val statusColor: Color
)

// Mock data
fun getMockInventarioItems(): List<InventarioItem> {
    return listOf(
        InventarioItem(
            nombre = "Alcohol etílico",
            cantidad = "150",
            unidad = "L",
            proveedor = "Girasol Pura",
            lote = "Quanbzo V/A",
            statusColor = FabrikAppGreen
        ),
        InventarioItem(
            nombre = "Ácido cítrico",
            cantidad = "20",
            unidad = "kg",
            proveedor = "Promosco V/2",
            lote = "Pronssito V/2",
            statusColor = FabrikAppOrange
        ),
        InventarioItem(
            nombre = "Cloruro de sodio",
            cantidad = "300",
            unidad = "kg",
            proveedor = "Guany/sco 6 971703",
            lote = "Galucioniu 8",
            statusColor = FabrikAppBlue
        ),
        InventarioItem(
            nombre = "Agua destilada",
            cantidad = "50",
            unidad = "L",
            proveedor = "Rejua Pera",
            lote = "Assa Puša",
            statusColor = FabrikAppOrange
        )
    )
}


