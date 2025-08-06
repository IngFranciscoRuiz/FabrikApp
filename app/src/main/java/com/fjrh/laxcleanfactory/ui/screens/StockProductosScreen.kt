package com.fjrh.laxcleanfactory.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fjrh.laxcleanfactory.ui.viewmodel.FormulaViewModel

// Definición local para evitar errores de símbolo no encontrado
data class StockProducto(
    val nombre: String,
    val stock: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockProductosScreen(
    viewModel: FormulaViewModel = hiltViewModel()
) {
    val stockList = viewModel.stockProductos.collectAsState().value

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
                        val color = when {
                            stock.stock <= 5 -> Color(0xFFD32F2F) // Rojo: bajo
                            stock.stock <= 20 -> Color(0xFFFFA000) // Amarillo: medio
                            else -> Color(0xFF388E3C) // Verde: suficiente
                        }
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
                                        text = "Stock disponible",
                                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
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
