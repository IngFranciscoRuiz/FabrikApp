package com.fjrh.FabrikApp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.navigation.NavController

@Composable
fun FinanzasHubScreen(
    navController: NavController
) {
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
                .padding(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header moderno
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color(0xFF1A1A1A),
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { navController.popBackStack() }
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = "Ventas y Finanzas",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFF1A1A1A),
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Icono de finanzas
                Icon(
                    imageVector = Icons.Default.AttachMoney,
                    contentDescription = null,
                    tint = Color(0xFF1976D2),
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Descripción
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Gestión Financiera",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF1A1A1A),
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Administra tus ventas, pedidos y balance financiero",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF666666),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Grid de opciones financieras
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Fila 1: Ventas y Pedidos
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                                         // Tarjeta de Ventas
                     Card(
                         modifier = Modifier
                             .weight(1f)
                             .height(140.dp)
                             .clickable { navController.navigate("ventas") },
                         colors = CardDefaults.cardColors(containerColor = Color.White),
                         shape = RoundedCornerShape(16.dp),
                         elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                     ) {
                         Column(
                             modifier = Modifier
                                 .fillMaxSize()
                                 .padding(16.dp),
                             horizontalAlignment = Alignment.CenterHorizontally,
                             verticalArrangement = Arrangement.Center
                         ) {
                             Icon(
                                 imageVector = Icons.Default.AttachMoney,
                                 contentDescription = null,
                                 tint = Color(0xFF4CAF50),
                                 modifier = Modifier.size(28.dp)
                             )
                             
                             Spacer(modifier = Modifier.height(6.dp))
                             
                             Text(
                                 text = "Ventas",
                                 style = MaterialTheme.typography.titleMedium,
                                 color = Color(0xFF1A1A1A),
                                 fontWeight = FontWeight.Bold,
                                 textAlign = TextAlign.Center
                             )
                             
                             Spacer(modifier = Modifier.height(4.dp))
                             
                             Text(
                                 text = "Registrar y gestionar ventas",
                                 style = MaterialTheme.typography.bodySmall,
                                 color = Color(0xFF666666),
                                 textAlign = TextAlign.Center,
                                 maxLines = 2
                             )
                         }
                     }
                    
                                         // Tarjeta de Pedidos
                     Card(
                         modifier = Modifier
                             .weight(1f)
                             .height(140.dp)
                             .clickable { navController.navigate("pedidos_proveedor") },
                         colors = CardDefaults.cardColors(containerColor = Color.White),
                         shape = RoundedCornerShape(16.dp),
                         elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                     ) {
                         Column(
                             modifier = Modifier
                                 .fillMaxSize()
                                 .padding(16.dp),
                             horizontalAlignment = Alignment.CenterHorizontally,
                             verticalArrangement = Arrangement.Center
                         ) {
                             Icon(
                                 imageVector = Icons.Default.ShoppingCart,
                                 contentDescription = null,
                                 tint = Color(0xFF1976D2),
                                 modifier = Modifier.size(28.dp)
                             )
                             
                             Spacer(modifier = Modifier.height(6.dp))
                             
                             Text(
                                 text = "Pedidos",
                                 style = MaterialTheme.typography.titleMedium,
                                 color = Color(0xFF1A1A1A),
                                 fontWeight = FontWeight.Bold,
                                 textAlign = TextAlign.Center
                             )
                             
                             Spacer(modifier = Modifier.height(4.dp))
                             
                             Text(
                                 text = "Gestionar pedidos a proveedores",
                                 style = MaterialTheme.typography.bodySmall,
                                 color = Color(0xFF666666),
                                 textAlign = TextAlign.Center,
                                 maxLines = 2
                             )
                         }
                     }
                }
                
                // Fila 2: Balance (tarjeta completa)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clickable { navController.navigate("balance") },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = null,
                            tint = Color(0xFF9C27B0),
                            modifier = Modifier.size(40.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Balance Financiero",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color(0xFF1A1A1A),
                                fontWeight = FontWeight.Bold
                            )
                            
                            Text(
                                text = "Ver ingresos, gastos y balance general",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF666666)
                            )
                        }
                        
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color(0xFFCCCCCC),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Información adicional
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFF1976D2),
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Text(
                        text = "Todas las transacciones se registran automáticamente en el balance",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF1976D2),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
