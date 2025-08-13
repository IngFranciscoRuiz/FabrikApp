package com.fjrh.FabrikApp.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.fjrh.FabrikApp.ui.theme.*

@Composable
fun AppSelectorScreen(
    navController: NavController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FabrikAppGrayLight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Header
            Text(
                text = "🎨 FabrikApp",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = FabrikAppBlack,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Selecciona la versión que quieres ver",
                style = MaterialTheme.typography.bodyLarge,
                color = FabrikAppGray,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // App Actual
            AppOptionCard(
                icon = Icons.Filled.Apps,
                title = "App Actual",
                description = "Versión funcional con todas las características implementadas",
                onClick = { 
                    navController.navigate("menu") {
                        popUpTo("app_selector") { inclusive = true }
                    }
                },
                color = FabrikAppBlue
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Mockup Nuevo
            AppOptionCard(
                icon = Icons.Filled.DesignServices,
                title = "Mockup Nuevo",
                description = "Diseño minimalista y moderno (solo visual)",
                onClick = { 
                    navController.navigate("mockup_demo") {
                        popUpTo("app_selector") { inclusive = true }
                    }
                },
                color = FabrikAppGreen
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Info
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "💡 Información",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = FabrikAppBlack
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "• App Actual: Funcionalidad completa con datos reales\n• Mockup Nuevo: Diseño visual sin funcionalidad",
                        style = MaterialTheme.typography.bodyMedium,
                        color = FabrikAppGray
                    )
                }
            }
        }
    }
}

@Composable
fun AppOptionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit,
    color: Color
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Card(
                modifier = Modifier.size(56.dp),
                colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = FabrikAppBlack
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = FabrikAppGray
                )
            }
            
            // Arrow
            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}


