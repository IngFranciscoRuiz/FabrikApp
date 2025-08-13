package com.fjrh.FabrikApp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fjrh.FabrikApp.ui.theme.*

@Composable
fun MockupDemoScreen(
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FabrikAppGrayLight)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        DemoHeader()
        
        // Mockup Preview
        MockupPreview()
        
        // Features List
        FeaturesList()
        
        // Implementation Plan
        ImplementationPlan()
        
        // Navigation Buttons
        NavigationButtons(navController)
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun DemoHeader() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🎨 Mockup del Nuevo Diseño",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = FabrikAppBlack,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Diseño minimalista y moderno para FabrikApp",
                style = MaterialTheme.typography.bodyLarge,
                color = FabrikAppGray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun MockupPreview() {
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
                text = "📱 Pantallas Creadas",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = FabrikAppBlack
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Dashboard
            MockupItem(
                icon = Icons.Filled.Dashboard,
                title = "Dashboard Principal",
                description = "Panel de control con KPIs, accesos rápidos y funciones principales",
                color = FabrikAppBlue
            )
            
            // Inventario
            MockupItem(
                icon = Icons.Filled.Inventory,
                title = "Gestión de Inventario",
                description = "Lista de insumos con indicadores de estado y FAB para agregar",
                color = FabrikAppGreen
            )
            
            // Producción
            MockupItem(
                icon = Icons.Filled.Science,
                title = "Control de Producción",
                description = "Fórmulas y lotes de producción con precios y estados",
                color = FabrikAppOrange
            )
            
            // Ventas
            MockupItem(
                icon = Icons.Filled.ShoppingCart,
                title = "Ventas y Finanzas",
                description = "Pedidos con checkboxes de estado y fechas",
                color = FabrikAppRed
            )
            
            // Bottom Navigation
            MockupItem(
                icon = Icons.Filled.Navigation,
                title = "Bottom Navigation",
                description = "Navegación moderna con 5 secciones principales",
                color = FabrikAppBlue
            )
        }
    }
}

@Composable
fun MockupItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = FabrikAppBlack
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = FabrikAppGray
            )
        }
    }
}

@Composable
fun FeaturesList() {
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
                text = "✨ Características del Nuevo Diseño",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = FabrikAppBlack
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            FeatureItem("🎨 Diseño minimalista y limpio")
            FeatureItem("📱 Bottom navigation moderno")
            FeatureItem("🎯 KPIs destacados en dashboard")
            FeatureItem("⚡ Accesos rápidos con FAB")
            FeatureItem("📊 Indicadores de estado visuales")
            FeatureItem("🎪 Animaciones suaves")
            FeatureItem("📱 Responsive y adaptable")
            FeatureItem("🎨 Paleta de colores consistente")
        }
    }
}

@Composable
fun FeatureItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "•",
            style = MaterialTheme.typography.bodyLarge,
            color = FabrikAppBlue,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = FabrikAppBlack
        )
    }
}

@Composable
fun ImplementationPlan() {
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
                text = "📋 Plan de Implementación",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = FabrikAppBlack
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            PlanPhase(
                phase = "Fase 1",
                title = "Dashboard + Navegación",
                description = "Implementar dashboard principal y bottom navigation",
                duration = "1-2 días"
            )
            
            PlanPhase(
                phase = "Fase 2",
                title = "Pantallas Principales",
                description = "Rediseñar inventario, producción y ventas",
                duration = "2-3 días"
            )
            
            PlanPhase(
                phase = "Fase 3",
                title = "Integración y Pulido",
                description = "Conectar con datos reales y ajustes finales",
                duration = "1-2 días"
            )
        }
    }
}

@Composable
fun PlanPhase(
    phase: String,
    title: String,
    description: String,
    duration: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = FabrikAppGrayLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = phase,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = FabrikAppBlue
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = FabrikAppBlack
                    )
                }
                
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = FabrikAppGray
                )
            }
            
            Text(
                text = duration,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = FabrikAppOrange
            )
        }
    }
}

@Composable
fun NavigationButtons(navController: NavController) {
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
                text = "🚀 Ver Pantallas del Mockup",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = FabrikAppBlack
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Dashboard Button
            Button(
                onClick = { navController.navigate("dashboard") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = FabrikAppBlue)
            ) {
                Icon(
                    imageVector = Icons.Filled.Dashboard,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ver Dashboard")
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Inventario Button
            Button(
                onClick = { navController.navigate("inventario_moderno") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = FabrikAppGreen)
            ) {
                Icon(
                    imageVector = Icons.Filled.Inventory,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ver Inventario")
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Producción Button
            Button(
                onClick = { navController.navigate("produccion_moderno") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = FabrikAppOrange)
            ) {
                Icon(
                    imageVector = Icons.Filled.Science,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ver Producción")
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Ventas Button
            Button(
                onClick = { navController.navigate("ventas_moderno") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = FabrikAppRed)
            ) {
                Icon(
                    imageVector = Icons.Filled.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ver Ventas")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Back to App Selector
            OutlinedButton(
                onClick = { 
                    navController.navigate("app_selector") {
                        popUpTo("mockup_demo") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Volver al Selector")
            }
        }
    }
}
