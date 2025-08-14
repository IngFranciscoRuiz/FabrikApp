package com.fjrh.FabrikApp.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.fjrh.FabrikApp.ui.viewmodel.InventarioViewModel
import com.fjrh.FabrikApp.data.local.ConfiguracionDataStore
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController


@Composable
fun InventarioScreenRoute(
    navController: NavController,
    viewModel: InventarioViewModel = hiltViewModel()
) {
    InventarioScreen(
        viewModel = viewModel,
        onAgregarClicked = { navController.navigate("agregar_ingrediente") }
    )
}
