package com.fjrh.FabrikApp.domain.usecase

import android.content.Context
import android.util.Log
import com.fjrh.FabrikApp.domain.exception.*
import com.fjrh.FabrikApp.domain.result.Result
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * Manejador centralizado de errores
 */
class ErrorHandler(private val context: Context) {

    companion object {
        private const val TAG = "ErrorHandler"
    }

    /**
     * Procesa una excepción y retorna un mensaje de error amigable
     */
    fun processException(exception: Throwable): String {
        return when (exception) {
            is ValidationException -> exception.message ?: "Error de validación"
            is InsufficientStockException -> exception.message ?: "Stock insuficiente"
            is ProductNotFoundException -> exception.message ?: "Producto no encontrado"
            is FormulaNotFoundException -> exception.message ?: "Fórmula no encontrada"
            is IngredientNotFoundException -> exception.message ?: "Ingrediente no encontrado"
            is DatabaseException -> "Error en la base de datos: ${exception.message}"
            is FileOperationException -> "Error al procesar archivo: ${exception.message}"
            is NetworkException -> "Error de conexión: ${exception.message}"
            is ConfigurationException -> "Error de configuración: ${exception.message}"
            else -> {
                Log.e(TAG, "Error no manejado", exception)
                "Ha ocurrido un error inesperado"
            }
        }
    }

    /**
     * Handler para corrutinas
     */
    val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.e(TAG, "Error en corrutina", exception)
        // Aquí podrías enviar el error a un servicio de analytics
    }

    /**
     * Wrapper para operaciones que pueden fallar
     */
    suspend fun <T> safeCall(operation: suspend () -> T): Result<T> {
        return try {
            Result.Success(operation())
        } catch (e: AppException) {
            Log.w(TAG, "Error de aplicación", e)
            Result.Error(e)
        } catch (e: Exception) {
            Log.e(TAG, "Error inesperado", e)
            Result.Error(DatabaseException("Error interno: ${e.message}", e))
        }
    }

    /**
     * Extension para Flow que maneja errores
     */
    fun <T> Flow<T>.handleErrors(): Flow<Result<T>> {
        return this
            .map<T, Result<T>> { Result.Success(it) }
            .catch { exception ->
                emit(Result.Error(processExceptionToAppException(exception)))
            }
    }

    /**
     * Convierte cualquier excepción a AppException
     */
    private fun processExceptionToAppException(exception: Throwable): AppException {
        return when (exception) {
            is AppException -> exception
            else -> DatabaseException("Error interno: ${exception.message}", exception)
        }
    }

    /**
     * Registra un error para analytics (para futuro)
     */
    fun logError(exception: Throwable, context: String = "") {
        Log.e(TAG, "Error en $context", exception)
        // Aquí podrías enviar a Crashlytics, Firebase Analytics, etc.
    }

    /**
     * Registra un evento de usuario (para futuro)
     */
    fun logEvent(eventName: String, parameters: Map<String, Any> = emptyMap()) {
        Log.d(TAG, "Evento: $eventName, Parámetros: $parameters")
        // Aquí podrías enviar a Firebase Analytics, etc.
    }
}

