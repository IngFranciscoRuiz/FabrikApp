package com.fjrh.FabrikApp.domain.validator

import com.fjrh.FabrikApp.data.local.entity.IngredienteInventarioEntity
import com.fjrh.FabrikApp.data.local.entity.FormulaEntity
import com.fjrh.FabrikApp.data.local.entity.VentaEntity
import org.junit.Assert.*
import org.junit.Test

class ValidatorsTest {

    @Test
    fun `validateIngrediente should return success for valid ingrediente`() {
        // Given
        val ingrediente = IngredienteInventarioEntity(
            nombre = "Aceite de Oliva",
            unidad = "L",
            cantidadDisponible = 10.0f,
            costoPorUnidad = 5.0,
            proveedor = "Proveedor A",
            fechaIngreso = System.currentTimeMillis()
        )

        // When
        val result = Validators.validateIngrediente(ingrediente)

        // Then
        assertTrue(result.isSuccess())
        assertFalse(result.isError())
        assertTrue(result.getErrors().isEmpty())
    }

    @Test
    fun `validateIngrediente should return error for empty name`() {
        // Given
        val ingrediente = IngredienteInventarioEntity(
            nombre = "",
            unidad = "L",
            cantidadDisponible = 10.0f,
            costoPorUnidad = 5.0,
            proveedor = "Proveedor A",
            fechaIngreso = System.currentTimeMillis()
        )

        // When
        val result = Validators.validateIngrediente(ingrediente)

        // Then
        assertFalse(result.isSuccess())
        assertTrue(result.isError())
        assertTrue(result.getErrors().contains("El nombre del ingrediente no puede estar vacío"))
    }

    @Test
    fun `validateIngrediente should return error for negative quantity`() {
        // Given
        val ingrediente = IngredienteInventarioEntity(
            nombre = "Aceite de Oliva",
            unidad = "L",
            cantidadDisponible = -5.0f,
            costoPorUnidad = 5.0,
            proveedor = "Proveedor A",
            fechaIngreso = System.currentTimeMillis()
        )

        // When
        val result = Validators.validateIngrediente(ingrediente)

        // Then
        assertFalse(result.isSuccess())
        assertTrue(result.isError())
        assertTrue(result.getErrors().contains("La cantidad no puede ser negativa"))
    }

    @Test
    fun `validateIngrediente should return error for negative cost`() {
        // Given
        val ingrediente = IngredienteInventarioEntity(
            nombre = "Aceite de Oliva",
            unidad = "L",
            cantidadDisponible = 10.0f,
            costoPorUnidad = -5.0,
            proveedor = "Proveedor A",
            fechaIngreso = System.currentTimeMillis()
        )

        // When
        val result = Validators.validateIngrediente(ingrediente)

        // Then
        assertFalse(result.isSuccess())
        assertTrue(result.isError())
        assertTrue(result.getErrors().contains("El costo no puede ser negativo"))
    }

    @Test
    fun `validateFormula should return success for valid formula`() {
        // Given
        val formula = FormulaEntity(
            nombre = "Jabón de Lavanda",
            descripcion = "Jabón artesanal con aceite de lavanda",
            rendimiento = 10.0,
            costoTotal = 25.0,
            fechaCreacion = System.currentTimeMillis()
        )

        // When
        val result = Validators.validateFormula(formula)

        // Then
        assertTrue(result.isSuccess())
        assertFalse(result.isError())
        assertTrue(result.getErrors().isEmpty())
    }

    @Test
    fun `validateFormula should return error for empty name`() {
        // Given
        val formula = FormulaEntity(
            nombre = "",
            descripcion = "Jabón artesanal con aceite de lavanda",
            rendimiento = 10.0,
            costoTotal = 25.0,
            fechaCreacion = System.currentTimeMillis()
        )

        // When
        val result = Validators.validateFormula(formula)

        // Then
        assertFalse(result.isSuccess())
        assertTrue(result.isError())
        assertTrue(result.getErrors().contains("El nombre de la fórmula no puede estar vacío"))
    }

    @Test
    fun `validateFormula should return error for zero yield`() {
        // Given
        val formula = FormulaEntity(
            nombre = "Jabón de Lavanda",
            descripcion = "Jabón artesanal con aceite de lavanda",
            rendimiento = 0.0,
            costoTotal = 25.0,
            fechaCreacion = System.currentTimeMillis()
        )

        // When
        val result = Validators.validateFormula(formula)

        // Then
        assertFalse(result.isSuccess())
        assertTrue(result.isError())
        assertTrue(result.getErrors().contains("El rendimiento debe ser mayor a 0"))
    }

    @Test
    fun `validateVenta should return success for valid venta`() {
        // Given
        val venta = VentaEntity(
            nombreProducto = "Jabón de Lavanda",
            litrosVendidos = 5.0,
            precioPorLitro = 3.0,
            fecha = System.currentTimeMillis()
        )

        // When
        val result = Validators.validateVenta(venta)

        // Then
        assertTrue(result.isSuccess())
        assertFalse(result.isError())
        assertTrue(result.getErrors().isEmpty())
    }

    @Test
    fun `validateVenta should return error for empty product name`() {
        // Given
        val venta = VentaEntity(
            nombreProducto = "",
            litrosVendidos = 5.0,
            precioPorLitro = 3.0,
            fecha = System.currentTimeMillis()
        )

        // When
        val result = Validators.validateVenta(venta)

        // Then
        assertFalse(result.isSuccess())
        assertTrue(result.isError())
        assertTrue(result.getErrors().contains("El nombre del producto no puede estar vacío"))
    }

    @Test
    fun `validateVenta should return error for zero liters`() {
        // Given
        val venta = VentaEntity(
            nombreProducto = "Jabón de Lavanda",
            litrosVendidos = 0.0,
            precioPorLitro = 3.0,
            fecha = System.currentTimeMillis()
        )

        // When
        val result = Validators.validateVenta(venta)

        // Then
        assertFalse(result.isSuccess())
        assertTrue(result.isError())
        assertTrue(result.getErrors().contains("Los litros vendidos deben ser mayores a 0"))
    }

    @Test
    fun `validateEmail should return success for valid email`() {
        // Given
        val email = "test@example.com"

        // When
        val result = Validators.validateEmail(email)

        // Then
        assertTrue(result.isSuccess())
        assertFalse(result.isError())
    }

    @Test
    fun `validateEmail should return error for invalid email`() {
        // Given
        val email = "invalid-email"

        // When
        val result = Validators.validateEmail(email)

        // Then
        assertFalse(result.isSuccess())
        assertTrue(result.isError())
        assertTrue(result.getErrors().contains("El formato del email no es válido"))
    }

    @Test
    fun `validatePhone should return success for valid phone`() {
        // Given
        val phone = "+1234567890"

        // When
        val result = Validators.validatePhone(phone)

        // Then
        assertTrue(result.isSuccess())
        assertFalse(result.isError())
    }

    @Test
    fun `validatePhone should return error for invalid phone`() {
        // Given
        val phone = "123"

        // When
        val result = Validators.validatePhone(phone)

        // Then
        assertFalse(result.isSuccess())
        assertTrue(result.isError())
        assertTrue(result.getErrors().contains("El formato del teléfono no es válido"))
    }
}

