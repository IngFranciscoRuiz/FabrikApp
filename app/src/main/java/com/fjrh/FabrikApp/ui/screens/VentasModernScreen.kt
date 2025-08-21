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
fun VentasModernScreen(
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
            VentasHeader(navController)
            
            // Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
                
                items(getMockPedidos()) { pedido ->
                    PedidoCard(pedido = pedido)
                }
                
                item {
                    Spacer(modifier = Modifier.height(16.dp)) // Reduced space for bottom nav
                }
            }
        }
        
        // Floating Action Button
        FloatingActionButton(
            onClick = { navController.navigate("nueva_venta") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp), // Extra padding for bottom nav
            containerColor = FabrikAppBlue,
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Nuevo pedido",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun VentasHeader(navController: NavController) {
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
                    text = "Ventas y Finanzas",
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
fun PedidoCard(pedido: Pedido) {
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
            // Checkbox indicator
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(
                        if (pedido.completado) FabrikAppGreen 
                        else Color(0xFFE5E7EB)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (pedido.completado) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Pedido info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = pedido.cliente,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = FabrikAppBlack
                )
                
                Text(
                    text = pedido.fecha,
                    style = MaterialTheme.typography.bodySmall,
                    color = FabrikAppGray
                )
            }
            
            // Status indicator
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        if (pedido.completado) FabrikAppGreen 
                        else FabrikAppOrange
                    )
            )
        }
    }
}

// Data class for pedidos
data class Pedido(
    val cliente: String,
    val fecha: String,
    val completado: Boolean
)

// Mock data
fun getMockPedidos(): List<Pedido> {
    return listOf(
        Pedido(
            cliente = "Compañía ABC",
            fecha = "27 de enero de 2024",
            completado = true
        ),
        Pedido(
            cliente = "Distribuidora LME",
            fecha = "26 de enero de 2024",
            completado = false
        ),
        Pedido(
            cliente = "Cliente Particular",
            fecha = "25 de enero de 2024",
            completado = true
        ),
        Pedido(
            cliente = "Corporativo OPI",
            fecha = "24 de enero de 2024",
            completado = false
        )
    )
}


