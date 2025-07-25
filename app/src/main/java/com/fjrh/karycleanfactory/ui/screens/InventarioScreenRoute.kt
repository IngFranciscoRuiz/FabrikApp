package com.fjrh.karycleanfactory.ui.screens

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.fjrh.karycleanfactory.ui.viewmodel.InventarioViewModel


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
