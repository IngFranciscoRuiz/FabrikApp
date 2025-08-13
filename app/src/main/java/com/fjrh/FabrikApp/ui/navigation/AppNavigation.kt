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

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "splash") {

        composable("splash") {
            SplashScreen(navController)
        }

        composable("app_selector") {
            AppSelectorScreen(navController)
        }

        composable("onboarding") {
            OnboardingScreen(navController)
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
            println("DEBUG: === NAVEGACIÓN A nueva_formula ===")
            NuevaFormulaScreen(
                navController = navController,
                viewModel = hiltViewModel()
            )
        }

        composable(
            route = "editar_formula/{formulaJson}",
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
            HistorialScreen(navController = navController, viewModel = hiltViewModel())
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
            StockProductosScreen(navController = navController)
        }

        composable("ventas") {
            VentasScreen()
        }

        composable("finanzas_hub") {
            FinanzasHubScreen(navController)
        }

        composable("balance") {
            BalanceScreen(navController = navController)
        }

        composable("notas") {
            NotasScreen(navController = navController)
        }

        composable("unidades") {
            UnidadesScreen(navController = navController)
        }

        composable("pedidos_proveedor") {
            PedidosProveedorScreen()
        }

        composable("configuracion") {
            ConfiguracionScreen(navController)
        }

        // ===== RUTAS DEL MOCKUP MODERNO =====
        
        composable("dashboard") {
            DashboardScreen(navController)
        }

        composable("inventario_moderno") {
            InventarioModernScreen(navController)
        }

        composable("produccion_moderno") {
            ProduccionModernScreen(navController)
        }

        composable("ventas_moderno") {
            VentasModernScreen(navController)
        }

        composable("mockup_demo") {
            MockupDemoScreen(navController)
        }

        // Rutas adicionales para accesos rápidos
        composable("nueva_produccion") {
            ProduccionScreen(
                formula = null,
                viewModel = hiltViewModel()
            )
        }

        composable("nueva_venta") {
            VentasScreen()
        }
    }
}
