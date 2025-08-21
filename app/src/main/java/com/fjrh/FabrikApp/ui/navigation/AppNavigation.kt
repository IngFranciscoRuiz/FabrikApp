package com.fjrh.FabrikApp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.fjrh.FabrikApp.ui.screens.*
import com.fjrh.FabrikApp.data.local.entity.FormulaConIngredientes
import com.fjrh.FabrikApp.data.local.entity.FormulaSerializable
import com.fjrh.FabrikApp.data.local.entity.FormulaEntity
import com.fjrh.FabrikApp.data.local.entity.IngredienteEntity
import com.fjrh.FabrikApp.data.local.ConfiguracionDataStore
import com.google.gson.Gson
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import com.fjrh.FabrikApp.ui.screens.MainMenuScreen
import com.fjrh.FabrikApp.ui.screens.SubscriptionScreen
import com.fjrh.FabrikApp.ui.screens.LoginScreen
import com.fjrh.FabrikApp.ui.screens.BackupScreen
import com.fjrh.FabrikApp.ui.viewmodel.SubscriptionViewModel
import com.fjrh.FabrikApp.ui.viewmodel.LoginViewModel

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.Splash) {

        composable(Routes.Splash) {
            SplashScreen(navController)
        }

        composable("onboarding") {
            OnboardingScreen(navController)
        }

        composable(Routes.Menu) {
            MainMenuScreen(navController)
        }

        composable(Routes.Formulas) {
            ListaFormulasScreen(
                navController = navController,
                viewModel = hiltViewModel()
            )
        }

        composable(Routes.NuevaFormula) {
            println("DEBUG: === NAVEGACIÓN A nueva_formula ===")
            NuevaFormulaScreen(
                navController = navController,
                viewModel = hiltViewModel()
            )
        }

        composable(
            route = "${Routes.EditarFormula}/{formulaJson}",
            arguments = listOf(navArgument("formulaJson") { type = NavType.StringType })
        ) { backStackEntry ->
            println("DEBUG: === NAVEGACIÓN A editar_formula ===")
            println("DEBUG: Ruta actual: ${backStackEntry.destination.route}")
            println("DEBUG: Argumentos: ${backStackEntry.arguments}")
            val json = backStackEntry.arguments?.getString("formulaJson")
            println("DEBUG: Recibido JSON en navegación: $json")
            
            val formula = try {
                println("DEBUG: === INICIO DECODIFICACIÓN ===")
                println("DEBUG: JSON recibido: $json")
                
                // El JSON ya viene decodificado desde la navegación, no necesitamos URLDecoder
                val formulaSerializable = Gson().fromJson(json, FormulaSerializable::class.java)
                println("DEBUG: Fórmula serializable parseada: ${formulaSerializable?.nombre}")
                println("DEBUG: Cantidad de ingredientes: ${formulaSerializable?.ingredientes?.size}")
                
                // Convertir de vuelta a FormulaConIngredientes
                val formulaObj = if (formulaSerializable != null) {
                    val ingredientes = formulaSerializable.ingredientes.map { ingredienteSerializable ->
                        IngredienteEntity(
                            formulaId = formulaSerializable.id,
                            nombre = ingredienteSerializable.nombre,
                            unidad = ingredienteSerializable.unidad,
                            cantidad = ingredienteSerializable.cantidad,
                            costoPorUnidad = ingredienteSerializable.costoPorUnidad
                        )
                    }
                    
                    val result = FormulaConIngredientes(
                        formula = FormulaEntity(
                            id = formulaSerializable.id,
                            nombre = formulaSerializable.nombre
                        ),
                        ingredientes = ingredientes
                    )
                    
                    println("DEBUG: Fórmula reconstruida - ID: ${result.formula.id}, Nombre: ${result.formula.nombre}")
                    println("DEBUG: Ingredientes reconstruidos: ${result.ingredientes.size}")
                    result.ingredientes.forEach { ingrediente ->
                        println("DEBUG: Ingrediente reconstruido: ${ingrediente.nombre} - ${ingrediente.cantidad} ${ingrediente.unidad} - $${ingrediente.costoPorUnidad}")
                    }
                    
                    result
                } else {
                    println("DEBUG: formulaSerializable es null")
                    null
                }
                
                println("DEBUG: === FIN DECODIFICACIÓN ===")
                formulaObj
            } catch (e: Exception) {
                println("DEBUG: Error al parsear fórmula: ${e.message}")
                e.printStackTrace()
                null
            }

            NuevaFormulaScreen(
                navController = navController,
                viewModel = hiltViewModel(),
                formulaParaEditar = formula
            )
        }

        composable(Routes.Inventario) {
            InventarioScreenRoute(navController) // Ya inyecta su ViewModel adentro
        }
        composable(Routes.AgregarIngrediente) {
            AgregarIngredienteScreen(
                navController = navController,
                viewModel = hiltViewModel(),
                onGuardarExitoso = {
                    navController.popBackStack() // Regresa a inventario después de guardar
                }
            )
        }
        composable(Routes.Historial) {
            HistorialScreen(navController = navController, viewModel = hiltViewModel())
        }

        // Producción sin fórmula
        composable(Routes.Produccion) {
            ProduccionScreen(
                navController = navController,
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
                navController = navController,
                formula = formula,
                viewModel = hiltViewModel()
            )
        }

        composable(Routes.StockProductos) {
            StockProductosScreen(navController)
        }

        composable(Routes.Ventas) {
            VentasScreen(navController)
        }

        composable("finanzas_hub") {
            FinanzasHubScreen(navController)
        }

        composable(Routes.Balance) {
            BalanceScreen(navController)
        }

        composable(Routes.Notas) {
            NotasScreen(navController)
        }

        composable("unidades") {
            UnidadesScreen(navController)
        }

        composable(Routes.PedidosProveedor) {
            PedidosProveedorScreen(navController)
        }

        composable(Routes.Configuracion) {
            ConfiguracionScreen(navController)
        }
        
        composable("backup") {
            BackupScreen(navController)
        }

        // Rutas adicionales para accesos rápidos
        composable("nueva_produccion") {
            ProduccionScreen(
                navController = navController,
                formula = null,
                viewModel = hiltViewModel()
            )
        }

        composable("nueva_venta") {
            VentasScreen(navController)
        }

        composable(Routes.Subscription) {
            SubscriptionScreen(
                onNavigateBack = { navController.popBackStack() },
                onSubscribe = {
                    // En producción, esto se manejará desde la pantalla de suscripción
                    // con los botones específicos para mensual/anual
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.Login) {
            LoginScreen(
                navController = navController, 
                loginViewModel = hiltViewModel()
            )
        }

        composable(Routes.WorkspaceGate) {
            WorkspaceGateScreen(navController)
        }
    }
}
