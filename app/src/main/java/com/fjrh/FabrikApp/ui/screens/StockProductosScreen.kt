package com.fjrh.FabrikApp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fjrh.FabrikApp.ui.viewmodel.StockProductosViewModel
import com.fjrh.FabrikApp.domain.service.StockAlertService
import com.fjrh.FabrikApp.domain.model.ConfiguracionStock

// Definición local para evitar errores de símbolo no encontrado
data class StockProducto(
    val nombre: String,
    val stock: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockProductosScreen(
    viewModel: StockProductosViewModel = hiltViewModel()
) {
    val stockList by viewModel.stockProductos.collectAsState(initial = emptyList())
    val configuracion by viewModel.configuracion.collectAsState()
    val stockAlertService = remember { StockAlertService() }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Stock de productos terminados") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (stockList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay productos terminados registrados.")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(stockList) { stock ->
                        val color = stockAlertService.getStockColorProducto(stock.stock, configuracion)
                        val stockText = stockAlertService.getStockTextProducto(stock.stock, configuracion)
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(color.copy(alpha = 0.08f))
                                    .padding(20.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = stock.nombre,
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = stockText,
                                        style = MaterialTheme.typography.bodySmall.copy(color = color)
                                    )
                                }
                                Badge(
                                    containerColor = color,
                                    contentColor = Color.White
                                ) {
                                    Text(
                                        text = stock.stock.toString(),
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
} 
