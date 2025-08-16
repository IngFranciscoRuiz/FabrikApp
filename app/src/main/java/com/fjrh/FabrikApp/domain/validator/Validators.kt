package com.fjrh.FabrikApp.domain.validator

import com.fjrh.FabrikApp.domain.exception.ValidationException
import com.fjrh.FabrikApp.data.local.entity.IngredienteInventarioEntity
import com.fjrh.FabrikApp.data.local.entity.FormulaEntity
import com.fjrh.FabrikApp.data.local.entity.VentaEntity

/**
 * Validadores para los datos de la aplicación
 */
object Validators {

    /**
     * Valida un ingrediente del inventario
     */
    fun validateIngrediente(ingrediente: IngredienteInventarioEntity): ValidationResult {
        val errors = mutableListOf<String>()

        // Validar nombre
        if (ingrediente.nombre.trim().isBlank()) {
            errors.add("El nombre del ingrediente no puede estar vacío")
        } else if (ingrediente.nombre.length < 2) {
            errors.add("El nombre debe tener al menos 2 caracteres")
        } else if (ingrediente.nombre.length > 50) {
            errors.add("El nombre no puede exceder 50 caracteres")
        }

        // Validar cantidad
        if (ingrediente.cantidadDisponible < 0) {
            errors.add("La cantidad no puede ser negativa")
        }

        // Validar costo
        if (ingrediente.costoPorUnidad < 0) {
            errors.add("El costo no puede ser negativo")
        }

        // Validar unidad
        if (ingrediente.unidad.trim().isBlank()) {
            errors.add("La unidad de medida es requerida")
        }

        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Error(errors)
        }
    }

    /**
     * Valida una fórmula
     */
    fun validateFormula(formula: FormulaEntity): ValidationResult {
        val errors = mutableListOf<String>()

        // Validar nombre
        if (formula.nombre.trim().isBlank()) {
            errors.add("El nombre de la fórmula no puede estar vacío")
        } else if (formula.nombre.length < 2) {
            errors.add("El nombre debe tener al menos 2 caracteres")
        } else if (formula.nombre.length > 100) {
            errors.add("El nombre no puede exceder 100 caracteres")
        }

        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Error(errors)
        }
    }

    /**
     * Valida una venta
     */
    fun validateVenta(venta: VentaEntity): ValidationResult {
        val errors = mutableListOf<String>()

        // Validar nombre del producto
        if (venta.nombreProducto.trim().isBlank()) {
            errors.add("El nombre del producto no puede estar vacío")
        }

        // Validar litros vendidos
        if (venta.litrosVendidos <= 0) {
            errors.add("Los litros vendidos deben ser mayores a 0")
        }

        // Validar precio por litro
        if (venta.precioPorLitro <= 0) {
            errors.add("El precio por litro debe ser mayor a 0")
        }

        // Validar fecha
        if (venta.fecha <= 0) {
            errors.add("La fecha de venta es requerida")
        }

        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Error(errors)
        }
    }

    /**
     * Valida un email (para futuras funcionalidades)
     */
    fun validateEmail(email: String): ValidationResult {
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")
        
        return if (email.matches(emailRegex)) {
            ValidationResult.Success
        } else {
            ValidationResult.Error(listOf("El formato del email no es válido"))
        }
    }

    /**
     * Valida un número de teléfono (para futuras funcionalidades)
     */
    fun validatePhone(phone: String): ValidationResult {
        val phoneRegex = Regex("^[+]?[0-9]{10,15}\$")
        
        return if (phone.matches(phoneRegex)) {
            ValidationResult.Success
        } else {
            ValidationResult.Error(listOf("El formato del teléfono no es válido"))
        }
    }
}

/**
 * Resultado de validación
 */
sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val errors: List<String>) : ValidationResult()

    fun isSuccess(): Boolean = this is Success
    fun isError(): Boolean = this is Error

    fun throwIfError() {
        if (this is Error) {
            throw ValidationException(errors.joinToString(", "))
        }
    }
}
