package com.fjrh.laxcleanfactory.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.fjrh.laxcleanfactory.ui.viewmodel.InventarioViewModel
import com.fjrh.laxcleanfactory.data.local.ConfiguracionDataStore
import androidx.compose.ui.platform.LocalContext


@Composable
fun InventarioScreenRoute(navController: NavHostController) {
    val viewModel: InventarioViewModel = hiltViewModel()
    InventarioScreen(
        viewModel = viewModel,
        onAgregarClicked = {
            navController.navigate("agregar_ingrediente")
        }
    )
}
