package com.fjrh.FabrikApp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.fjrh.FabrikApp.ui.theme.*

@Composable
fun FabrikBottomNavigation(
    navController: NavController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = Icons.Filled.Home,
                label = "Inicio",
                route = "menu",
                currentRoute = currentRoute,
                onClick = { 
                    if (currentRoute == "menu") {
                        // Si ya estamos en menu, no hacer nada
                    } else {
                        // Navegar al menú principal
                        navController.navigate("menu") {
                            // Evitar múltiples copias de la misma pantalla
                            launchSingleTop = true
                            // Restaurar estado cuando se reselecciona
                            restoreState = true
                        }
                    }
                }
            )
            
            BottomNavItem(
                icon = Icons.Filled.Inventory,
                label = "Inventario",
                route = "inventario",
                currentRoute = currentRoute,
                onClick = { navController.navigate("inventario") }
            )
            
            BottomNavItem(
                icon = Icons.Filled.BarChart,
                label = "Producción",
                route = "produccion",
                currentRoute = currentRoute,
                onClick = { navController.navigate("produccion") }
            )
            
            BottomNavItem(
                icon = Icons.Filled.ShoppingCart,
                label = "Ventas",
                route = "ventas",
                currentRoute = currentRoute,
                onClick = { navController.navigate("ventas") }
            )
            
            BottomNavItem(
                icon = Icons.Filled.Note,
                label = "Notas",
                route = "notas",
                currentRoute = currentRoute,
                onClick = { navController.navigate("notas") }
            )
        }
    }
}

@Composable
fun BottomNavItem(
    icon: ImageVector,
    label: String,
    route: String,
    currentRoute: String?,
    onClick: () -> Unit
) {
    val isSelected = currentRoute == route
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) FabrikAppBlue else FabrikAppGray,
                modifier = Modifier.size(22.dp)
            )
        }
        
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) FabrikAppBlue else FabrikAppGray
        )
    }
}
