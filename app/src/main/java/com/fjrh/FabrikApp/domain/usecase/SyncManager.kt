package com.fjrh.FabrikApp.domain.usecase

import com.fjrh.FabrikApp.data.local.dao.FormulaDao
import com.fjrh.FabrikApp.data.remote.FirebaseService
import com.fjrh.FabrikApp.data.local.entity.FormulaEntity
import com.fjrh.FabrikApp.data.local.entity.IngredienteEntity
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
                val result = firebaseService.syncFormulas(listOf(formula))
                when (result) {
                    is Result.Success -> println("Fórmula sincronizada exitosamente")
                    is Result.Error -> println("Error al sincronizar fórmula: ${result.exception.message}")
                    is Result.Loading -> println("Sincronizando fórmula...")
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
            // Descargar datos de Firebase
            val formulasResult = firebaseService.downloadFormulas()
            
            // Procesar resultados
            if (formulasResult is Result.Error) {
                return formulasResult
            }
            
            // Guardar datos localmente
            val formulas = (formulasResult as Result.Success).data
            
            // Limpiar datos existentes y guardar nuevos
            formulaDao.limpiarFormulas()
            
            // Insertar fórmulas una por una
            formulas.forEach { formula ->
                formulaDao.insertFormula(formula)
            }
            
            Result.Success(Unit)
        } catch (e: Exception) {
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
