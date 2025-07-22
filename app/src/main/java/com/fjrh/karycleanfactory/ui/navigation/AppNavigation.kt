package com.fjrh.karycleanfactory.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fjrh.karycleanfactory.ui.screens.*
import com.fjrh.karycleanfactory.ui.viewmodel.FormulaViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    viewModel: FormulaViewModel
) {
    NavHost(navController = navController, startDestination = "splash") {

        composable("splash") {
            SplashScreen(navController)
        }

        composable("menu") {
            MainMenuScreen(navController)
        }

        composable("formulas") {
            ListaFormulasScreen(
                viewModel = viewModel,
                onEdit = { formula ->
                    navController.navigate("nueva_formula")
                    // Más adelante puedes pasar datos por argumentos
                },
                onProduccion = { formula ->
                    navController.navigate("produccion")
                    // También puedes pasar datos de fórmula si lo deseas
                }
            )
        }
        composable("nueva_formula") {
            NuevaFormulaScreen(viewModel, navController)
        }

        composable("inventario") {
            TextScreen("Inventario")
        }

        composable("produccion") {
            TextScreen("Producción")
        }

        composable("historial") {
            TextScreen("Historial")
        }
    }
}
