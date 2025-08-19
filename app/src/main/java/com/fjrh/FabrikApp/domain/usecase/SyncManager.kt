package com.fjrh.FabrikApp.domain.usecase

import com.fjrh.FabrikApp.data.local.dao.FormulaDao
import com.fjrh.FabrikApp.data.remote.FirebaseService
import com.fjrh.FabrikApp.data.local.entity.FormulaEntity
import com.fjrh.FabrikApp.data.local.entity.IngredienteEntity
import com.fjrh.FabrikApp.data.local.entity.IngredienteInventarioEntity
import com.fjrh.FabrikApp.data.local.entity.VentaEntity
import com.fjrh.FabrikApp.data.local.entity.BalanceEntity
import com.fjrh.FabrikApp.data.local.entity.NotaEntity
import com.fjrh.FabrikApp.domain.result.Result
import com.fjrh.FabrikApp.domain.exception.SubscriptionException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncManager @Inject constructor(
    private val firebaseService: FirebaseService,
    private val formulaDao: FormulaDao
) {
    
    /**
     * Sincronizar automáticamente cuando se agrega una nueva fórmula
     */
    suspend fun syncNewFormula(formula: FormulaEntity) {
        try {
            withContext(Dispatchers.IO) {
                // 1) Subir la fórmula
                val formulaResult = firebaseService.syncFormulas(listOf(formula))
                when (formulaResult) {
                    is Result.Success -> println("Fórmula sincronizada exitosamente")
                    is Result.Error -> println("Error al sincronizar fórmula: ${formulaResult.exception.message}")
                    is Result.Loading -> println("Sincronizando fórmula...")
                }

                // 2) Subir sus ingredientes asociados
                val ingredientes = formulaDao.getIngredientesByFormulaId(formula.id)
                if (ingredientes.isNotEmpty()) {
                    val ingResult = firebaseService.syncIngredientes(ingredientes)
                    when (ingResult) {
                        is Result.Success -> println("Ingredientes de la fórmula sincronizados exitosamente (${ingredientes.size})")
                        is Result.Error -> println("Error al sincronizar ingredientes: ${ingResult.exception.message}")
                        is Result.Loading -> println("Sincronizando ingredientes de la fórmula...")
                    }
                } else {
                    println("SyncManager: La fórmula ${formula.nombre} no tiene ingredientes para sincronizar")
                }
            }
        } catch (e: Exception) {
            println("Error en sincronización automática: ${e.message}")
        }
    }
    
    /**
     * Sincronizar automáticamente cuando se agrega un nuevo ingrediente
     */
    suspend fun syncNewIngrediente(ingrediente: IngredienteEntity) {
        try {
            withContext(Dispatchers.IO) {
                val result = firebaseService.syncIngredientes(listOf(ingrediente))
                when (result) {
                    is Result.Success -> println("Ingrediente sincronizado exitosamente")
                    is Result.Error -> println("Error al sincronizar ingrediente: ${result.exception.message}")
                    is Result.Loading -> println("Sincronizando ingrediente...")
                }
            }
        } catch (e: Exception) {
            println("Error en sincronización automática: ${e.message}")
        }
    }
    
    /**
     * Sincronizar automáticamente cuando se agrega una nueva venta
     */
    suspend fun syncNewVenta(venta: VentaEntity) {
        try {
            withContext(Dispatchers.IO) {
                val result = firebaseService.syncVentas(listOf(venta))
                when (result) {
                    is Result.Success -> println("Venta sincronizada exitosamente")
                    is Result.Error -> println("Error al sincronizar venta: ${result.exception.message}")
                    is Result.Loading -> println("Sincronizando venta...")
                }
            }
        } catch (e: Exception) {
            println("Error en sincronización automática: ${e.message}")
        }
    }
    
    /**
     * Sincronizar automáticamente cuando se agrega un nuevo movimiento de balance
     */
    suspend fun syncNewBalance(balance: BalanceEntity) {
        try {
            withContext(Dispatchers.IO) {
                val result = firebaseService.syncBalance(listOf(balance))
                when (result) {
                    is Result.Success -> println("Balance sincronizado exitosamente")
                    is Result.Error -> println("Error al sincronizar balance: ${result.exception.message}")
                    is Result.Loading -> println("Sincronizando balance...")
                }
            }
        } catch (e: Exception) {
            println("Error en sincronización automática de balance: ${e.message}")
        }
    }
    
    /**
     * Sincronizar automáticamente cuando se agrega una nueva nota
     */
    suspend fun syncNewNota(nota: NotaEntity) {
        try {
            withContext(Dispatchers.IO) {
                val result = firebaseService.syncNotas(listOf(nota))
                when (result) {
                    is Result.Success -> println("Nota sincronizada exitosamente")
                    is Result.Error -> println("Error al sincronizar nota: ${result.exception.message}")
                    is Result.Loading -> println("Sincronizando nota...")
                }
            }
        } catch (e: Exception) {
            println("Error en sincronización automática de nota: ${e.message}")
        }
    }

    /**
     * Sincronizar automáticamente cuando se agrega un nuevo ingrediente de inventario
     */
    suspend fun syncNewIngredienteInventario(ingrediente: com.fjrh.FabrikApp.data.local.entity.IngredienteInventarioEntity) {
        try {
            withContext(Dispatchers.IO) {
                val result = firebaseService.syncIngredientesInventario(listOf(ingrediente))
                when (result) {
                    is Result.Success -> println("Ingrediente de inventario sincronizado exitosamente")
                    is Result.Error -> println("Error al sincronizar ingrediente de inventario: ${result.exception.message}")
                    is Result.Loading -> println("Sincronizando ingrediente de inventario...")
                }
            }
        } catch (e: Exception) {
            println("Error en sincronización automática de ingrediente de inventario: ${e.message}")
        }
    }
    
    /**
     * Sincroniza todos los datos locales con Firebase
     */
    suspend fun syncToCloud(): Result<Unit> {
        return try {
            // Obtener todos los datos locales
            val formulas = formulaDao.getAllFormulas()
            
            // Sincronizar con Firebase (solo fórmulas por ahora)
            firebaseService.syncFormulas(formulas)
        } catch (e: Exception) {
            Result.Error(SubscriptionException("Error al sincronizar con la nube: ${e.message}"))
        }
    }
    
    /**
     * Descarga todos los datos de Firebase y los guarda localmente
     */
    suspend fun syncFromCloud(): Result<Unit> {
        return try {
            println("SyncManager: Iniciando descarga completa de datos desde Firebase...")
            
            // Descargar TODOS los datos de Firebase
            val formulasConIngredientesResult = firebaseService.downloadFormulasConIngredientes()
            val ingredientesInventarioResult = firebaseService.downloadIngredientesInventario()
            val ventasResult = firebaseService.downloadVentas()
            val balanceResult = firebaseService.downloadBalance()
            val notasResult = firebaseService.downloadNotas()
            val pedidosResult = firebaseService.downloadPedidosProveedor()
            val historialResult = firebaseService.downloadHistorial()
            val unidadesResult = firebaseService.downloadUnidadesMedida()
            
            // Procesar resultados y guardar datos localmente
            if (formulasConIngredientesResult is Result.Success) {
                val (formulas, ingredientes) = formulasConIngredientesResult.data
                println("SyncManager: Descargadas ${formulas.size} fórmulas con ${ingredientes.size} ingredientes")
                
                // Limpiar datos existentes
                formulaDao.limpiarFormulas()
                formulaDao.limpiarIngredientes()
                
                // Insertar fórmulas
                formulas.forEach { formula ->
                    formulaDao.insertFormula(formula)
                }
                
                // Insertar ingredientes
                ingredientes.forEach { ingrediente ->
                    formulaDao.insertIngredientes(listOf(ingrediente))
                }
            }
            
            if (ingredientesInventarioResult is Result.Success) {
                val ingredientes = ingredientesInventarioResult.data
                println("SyncManager: Descargados ${ingredientes.size} ingredientes de inventario")
                formulaDao.limpiarIngredientesInventario()
                ingredientes.forEach { ingrediente ->
                    formulaDao.insertarIngredienteInventario(ingrediente)
                }
            }
            
            if (ventasResult is Result.Success) {
                val ventas = ventasResult.data
                println("SyncManager: Descargadas ${ventas.size} ventas")
                formulaDao.limpiarVentas()
                ventas.forEach { venta ->
                    formulaDao.insertarVenta(venta)
                }
            }
            
            if (balanceResult is Result.Success) {
                val balance = balanceResult.data
                println("SyncManager: Descargado ${balance.size} balance")
                formulaDao.limpiarBalance()
                balance.forEach { registro ->
                    formulaDao.insertarBalance(registro)
                }
            }
            
            if (notasResult is Result.Success) {
                val notas = notasResult.data
                println("SyncManager: Descargadas ${notas.size} notas")
                formulaDao.limpiarNotas()
                notas.forEach { nota ->
                    formulaDao.insertarNota(nota)
                }
            }
            
            if (pedidosResult is Result.Success) {
                val pedidos = pedidosResult.data
                println("SyncManager: Descargados ${pedidos.size} pedidos a proveedor")
                formulaDao.limpiarPedidosProveedor()
                pedidos.forEach { pedido ->
                    formulaDao.insertarPedidoProveedor(pedido)
                }
            }
            
            if (historialResult is Result.Success) {
                val historial = historialResult.data
                println("SyncManager: Descargado ${historial.size} historial")
                formulaDao.limpiarHistorial()
                historial.forEach { registro ->
                    formulaDao.insertarHistorial(registro)
                }
            }
            
            if (unidadesResult is Result.Success) {
                val unidades = unidadesResult.data
                println("SyncManager: Descargadas ${unidades.size} unidades de medida")
                // No limpiar unidades por defecto, solo agregar las que no existen
                unidades.forEach { unidad ->
                    try {
                        formulaDao.insertarUnidadMedida(unidad)
                    } catch (e: Exception) {
                        // Si ya existe, ignorar
                        println("SyncManager: Unidad ${unidad.nombre} ya existe, ignorando")
                    }
                }
            }
            
            println("SyncManager: ✅ Descarga completa finalizada exitosamente")
            Result.Success(Unit)
        } catch (e: Exception) {
            println("SyncManager: ❌ Error al descargar de la nube: ${e.message}")
            Result.Error(SubscriptionException("Error al descargar de la nube: ${e.message}"))
        }
    }
    
    /**
     * Sincronización bidireccional (merge de datos)
     */
    suspend fun syncBidirectional(): Result<Unit> {
        return try {
            // Primero subir datos locales
            val uploadResult = syncToCloud()
            if (uploadResult is Result.Error) {
                return uploadResult
            }
            
            // Luego descargar datos actualizados
            val downloadResult = syncFromCloud()
            if (downloadResult is Result.Error) {
                return downloadResult
            }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(SubscriptionException("Error en sincronización bidireccional: ${e.message}"))
        }
    }
    
    /**
     * Verifica si hay conexión a Firebase
     */
    suspend fun checkConnection(): Result<Boolean> {
        return try {
            // Intentar una operación simple para verificar conexión
            val testResult = firebaseService.getCurrentUser()
            Result.Success(testResult != null)
        } catch (e: Exception) {
            Result.Error(SubscriptionException("Sin conexión a Firebase: ${e.message}"))
        }
    }
}
