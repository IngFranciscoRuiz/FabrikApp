package com.fjrh.FabrikApp.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.fjrh.FabrikApp.ui.viewmodel.StockProductosViewModel
import com.fjrh.FabrikApp.domain.service.StockAlertService
import com.fjrh.FabrikApp.domain.model.ConfiguracionStock

import com.fjrh.FabrikApp.domain.model.StockProducto

@Composable
fun StockProductosScreen(
    navController: NavController,
    viewModel: StockProductosViewModel = hiltViewModel()
) {
    val stockList by viewModel.stockProductos.collectAsState(initial = emptyList())
    val configuracion by viewModel.configuracion.collectAsState()
    val stockAlertService = remember { StockAlertService() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(top = 60.dp)
                .padding(bottom = 100.dp)
        ) {
            // Header moderno
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(12.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color(0xFF1976D2)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Text(
                        text = "Stock de Productos",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color(0xFF1A1A1A),
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1976D2)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "${stockList.size}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Estadísticas rápidas
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StockStatCard(
                    title = "Stock OK",
                    value = stockList.count { stockAlertService.getStockColorProducto(it.stock, configuracion) == Color(0xFF4CAF50) }.toString(),
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f)
                )
                StockStatCard(
                    title = "Stock Bajo",
                    value = stockList.count { stockAlertService.getStockColorProducto(it.stock, configuracion) == Color(0xFFFF9800) }.toString(),
                    color = Color(0xFFFF9800),
                    modifier = Modifier.weight(1f)
                )
                StockStatCard(
                    title = "Sin Stock",
                    value = stockList.count { stockAlertService.getStockColorProducto(it.stock, configuracion) == Color(0xFFF44336) }.toString(),
                    color = Color(0xFFF44336),
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Lista de productos
            if (stockList.isEmpty()) {
                StockEmptyState()
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(stockList) { stock ->
                        ModernStockCard(
                            stock = stock,
                            configuracion = configuracion,
                            stockAlertService = stockAlertService
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StockStatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                color = color,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF666666),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ModernStockCard(
    stock: StockProducto,
    configuracion: ConfiguracionStock,
    stockAlertService: StockAlertService
) {
    val color = stockAlertService.getStockColorProducto(stock.stock, configuracion)
    val stockText = stockAlertService.getStockTextProducto(stock.stock, configuracion)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                    text = stock.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF1A1A1A),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stockText,
                    style = MaterialTheme.typography.bodySmall,
                    color = color,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Card(
                colors = CardDefaults.cardColors(containerColor = color),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "${stock.stock} L",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun StockEmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Inventory,
                contentDescription = null,
                tint = Color(0xFFCCCCCC),
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No hay productos terminados",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF666666),
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Los productos aparecerán aquí después de producir lotes",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF999999),
                textAlign = TextAlign.Center
            )
        }
    }
} 
