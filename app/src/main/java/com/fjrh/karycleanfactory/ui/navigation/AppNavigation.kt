package com.fjrh.karycleanfactory.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.fjrh.karycleanfactory.ui.screens.*
import com.fjrh.karycleanfactory.ui.viewmodel.FormulaViewModel
import com.fjrh.karycleanfactory.data.local.entity.FormulaConIngredientes
import com.google.gson.Gson
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

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
                navController = navController
            )
        }

        composable("nueva_formula") {
            NuevaFormulaScreen(
                viewModel = viewModel,
                navController = navController
            )
        }

        composable("inventario") {
            InventarioScreenRoute() // Esta pantalla ya usa su propio ViewModel
        }


        composable("historial") {
            TextScreen("Historial")
        }

        // 🔹 Producción sin fórmula: muestra dropdown
        composable("produccion") {
            ProduccionScreen(
                formula = null,
                viewModel = viewModel
            )
        }

        // 🔸 Producción con fórmula precargada
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
                viewModel = viewModel
            )
        }
    }
}
