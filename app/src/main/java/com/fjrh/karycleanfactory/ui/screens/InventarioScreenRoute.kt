package com.fjrh.karycleanfactory.ui.screens

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.fjrh.karycleanfactory.ui.viewmodel.InventarioViewModel


@Composable
fun InventarioScreenRoute() {
    val viewModel: InventarioViewModel = hiltViewModel()
    InventarioScreen(viewModel = viewModel)
}
