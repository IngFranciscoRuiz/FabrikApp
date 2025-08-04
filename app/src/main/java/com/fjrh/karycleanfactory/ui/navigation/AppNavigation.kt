package com.fjrh.karycleanfactory.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.fjrh.karycleanfactory.ui.screens.*
import com.fjrh.karycleanfactory.data.local.entity.FormulaConIngredientes
import com.google.gson.Gson
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "splash") {

        composable("splash") {
            SplashScreen(navController)
        }

        composable("menu") {
            MainMenuScreen(navController)
        }

        composable("formulas") {
            ListaFormulasScreen(
                navController = navController,
                viewModel = hiltViewModel()
            )
        }

        composable("nueva_formula") {
            NuevaFormulaScreen(
                navController = navController,
                viewModel = hiltViewModel()
            )
        }

        composable("inventario") {
            InventarioScreenRoute(navController) // Ya inyecta su ViewModel adentro
        }
        composable("agregar_ingrediente") {
            AgregarIngredienteScreen(
                viewModel = hiltViewModel(),
                onGuardarExitoso = {
                    navController.popBackStack() // Regresa a inventario después de guardar
                }
            )
        }
        composable("historial") {
            HistorialScreen(viewModel = hiltViewModel())
        }

        // Producción sin fórmula
        composable("produccion") {
            ProduccionScreen(
                formula = null,
                viewModel = hiltViewModel()
            )
        }

        // Producción con fórmula precargada
        composable(
            route = "produccion/{formulaJson}",
            arguments = listOf(navArgument("formulaJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val json = backStackEntry.arguments?.getString("formulaJson")
            val formula = try {
                val decoded = URLDecoder.decode(json, StandardCharsets.UTF_8.toString())
                Gson().fromJson(decoded, FormulaConIngredientes::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

            ProduccionScreen(
                formula = formula,
                viewModel = hiltViewModel()
            )
        }

        composable("stock_productos") {
            StockProductosScreen()
        }

        composable("ventas") {
            VentasScreen()
        }

        composable("balance") {
            BalanceScreen()
        }
    }
}
