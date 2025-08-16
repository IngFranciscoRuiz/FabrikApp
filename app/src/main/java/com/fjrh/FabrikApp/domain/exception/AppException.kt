package com.fjrh.FabrikApp.domain.exception

/**
 * Excepciones personalizadas para la aplicación
 */
sealed class AppException(message: String) : Exception(message)

// Excepciones de validación
class ValidationException(message: String) : AppException(message)
class InvalidDataException(message: String) : AppException(message)

// Excepciones de negocio
class InsufficientStockException(productName: String, available: Double, requested: Double) : 
    AppException("Stock insuficiente para $productName. Disponible: $available, Solicitado: $requested")

class ProductNotFoundException(productName: String) : 
    AppException("Producto no encontrado: $productName")

class FormulaNotFoundException(formulaName: String) : 
    AppException("Fórmula no encontrada: $formulaName")

class IngredientNotFoundException(ingredientName: String) : 
    AppException("Ingrediente no encontrado: $ingredientName")

// Excepciones de base de datos
class DatabaseException(message: String, cause: Throwable? = null) : AppException(message) {
    init {
        cause?.let { initCause(it) }
    }
}

// Excepciones de archivos
class FileOperationException(message: String, cause: Throwable? = null) : AppException(message) {
    init {
        cause?.let { initCause(it) }
    }
}

// Excepciones de red (para futuro)
class NetworkException(message: String, cause: Throwable? = null) : AppException(message) {
    init {
        cause?.let { initCause(it) }
    }
}

// Excepciones de configuración
class ConfigurationException(message: String) : AppException(message)

