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
fun ProduccionModernScreen(
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
            ProduccionHeader(navController)
            
            // Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
                
                // Fórmulas Section
                item {
                    Text(
                        text = "Fórmulas",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = FabrikAppBlack
                    )
                }
                
                items(getMockFormulas()) { formula ->
                    FormulaCard(formula = formula)
                }
                
                // Lotes Section
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Lotes de Producción",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = FabrikAppBlack
                    )
                }
                
                items(getMockLotes()) { lote ->
                    LoteCard(lote = lote)
                }
                
                item {
                    Spacer(modifier = Modifier.height(16.dp)) // Reduced space for bottom nav
                }
            }
        }
        
        // Floating Action Button
        FloatingActionButton(
            onClick = { navController.navigate("nueva_produccion") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp), // Extra padding for bottom nav
            containerColor = FabrikAppBlue,
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Nuevo lote",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun ProduccionHeader(navController: NavController) {
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
                    text = "Control de Producción",
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
fun FormulaCard(formula: Formula) {
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = formula.nombre,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = FabrikAppBlack
                )
                
                Text(
                    text = formula.descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = FabrikAppGray
                )
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "$${formula.precio}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = FabrikAppBlue
                )
                
                Text(
                    text = "por unidad",
                    style = MaterialTheme.typography.bodySmall,
                    color = FabrikAppGray
                )
            }
        }
    }
}

@Composable
fun LoteCard(lote: Lote) {
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = lote.numero,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = FabrikAppBlack
                )
                
                Text(
                    text = lote.fecha,
                    style = MaterialTheme.typography.bodySmall,
                    color = FabrikAppGray
                )
            }
            
            // Status indicator
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(lote.statusColor)
            )
        }
    }
}

// Data classes
data class Formula(
    val nombre: String,
    val descripcion: String,
    val precio: String
)

data class Lote(
    val numero: String,
    val fecha: String,
    val statusColor: Color
)

// Mock data
fun getMockFormulas(): List<Formula> {
    return listOf(
        Formula(
            nombre = "Limpiador Multiusos",
            descripcion = "Goufu par Pearctonu",
            precio = "25,00"
        ),
        Formula(
            nombre = "Desinfectante",
            descripcion = "Goufu par Pinu",
            precio = "20,50"
        ),
        Formula(
            nombre = "Limpiavidrios",
            descripcion = "Goutu par Fearctonu",
            precio = "18,75"
        )
    )
}

fun getMockLotes(): List<Lote> {
    return listOf(
        Lote(
            numero = "Batch #42",
            fecha = "27 de enero de 2024",
            statusColor = FabrikAppGreen
        )
    )
}


