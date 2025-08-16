package com.fjrh.FabrikApp.domain.validator

import com.fjrh.FabrikApp.TestConfig
import com.fjrh.FabrikApp.data.local.entity.IngredienteInventarioEntity
import com.fjrh.FabrikApp.data.local.entity.FormulaEntity
import com.fjrh.FabrikApp.data.local.entity.VentaEntity
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class ValidatorsPerformanceTest {

    @get:Rule
    val timeoutRule = TestConfig.TimeoutRule()

    @Test
    fun `validateIngrediente should handle large batch efficiently`() {
        // Given
        val largeBatch = (1..1000).map { index ->
            IngredienteInventarioEntity(
                nombre = "Ingrediente $index",
                unidad = TestConfig.TestUtils.randomUnit(),
                cantidadDisponible = TestConfig.TestUtils.randomPositiveQuantity(),
                costoPorUnidad = TestConfig.TestUtils.randomPositiveCost(),
                proveedor = "Proveedor $index",
                fechaIngreso = System.currentTimeMillis()
            )
        }

        // When
        val startTime = System.currentTimeMillis()
        val results = largeBatch.map { Validators.validateIngrediente(it) }
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime

        // Then
        assertTrue("Validación de 1000 ingredientes debería tomar menos de 1 segundo", duration < 1000)
        assertEquals("Todos los ingredientes deberían ser válidos", 1000, results.count { it.isSuccess() })
    }

    @Test
    fun `validateFormula should handle large batch efficiently`() {
        // Given
        val largeBatch = (1..500).map { index ->
            FormulaEntity(
                nombre = "Fórmula $index",
                descripcion = "Descripción de la fórmula $index",
                rendimiento = TestConfig.TestUtils.randomPositiveYield(),
                costoTotal = TestConfig.TestUtils.randomPositiveCost(),
                fechaCreacion = System.currentTimeMillis()
            )
        }

        // When
        val startTime = System.currentTimeMillis()
        val results = largeBatch.map { Validators.validateFormula(it) }
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime

        // Then
        assertTrue("Validación de 500 fórmulas debería tomar menos de 1 segundo", duration < 1000)
        assertEquals("Todas las fórmulas deberían ser válidas", 500, results.count { it.isSuccess() })
    }

    @Test
    fun `validateVenta should handle large batch efficiently`() {
        // Given
        val largeBatch = (1..1000).map { index ->
            VentaEntity(
                nombreProducto = "Producto $index",
                litrosVendidos = TestConfig.TestUtils.randomPositiveQuantity().toDouble(),
                precioPorLitro = TestConfig.TestUtils.randomPositivePrice(),
                fecha = System.currentTimeMillis()
            )
        }

        // When
        val startTime = System.currentTimeMillis()
        val results = largeBatch.map { Validators.validateVenta(it) }
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime

        // Then
        assertTrue("Validación de 1000 ventas debería tomar menos de 1 segundo", duration < 1000)
        assertEquals("Todas las ventas deberían ser válidas", 1000, results.count { it.isSuccess() })
    }

    @Test
    fun `validateEmail should handle large batch efficiently`() {
        // Given
        val largeBatch = (1..1000).map { index ->
            TestConfig.TestUtils.randomValidEmail()
        }

        // When
        val startTime = System.currentTimeMillis()
        val results = largeBatch.map { Validators.validateEmail(it) }
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime

        // Then
        assertTrue("Validación de 1000 emails debería tomar menos de 500ms", duration < 500)
        assertEquals("Todos los emails deberían ser válidos", 1000, results.count { it.isSuccess() })
    }

    @Test
    fun `validatePhone should handle large batch efficiently`() {
        // Given
        val largeBatch = (1..1000).map { index ->
            TestConfig.TestUtils.randomValidPhone()
        }

        // When
        val startTime = System.currentTimeMillis()
        val results = largeBatch.map { Validators.validatePhone(it) }
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime

        // Then
        assertTrue("Validación de 1000 teléfonos debería tomar menos de 500ms", duration < 500)
        assertEquals("Todos los teléfonos deberían ser válidos", 1000, results.count { it.isSuccess() })
    }

    @Test
    fun `validateIngrediente should handle very long names efficiently`() {
        // Given
        val longNameIngredient = IngredienteInventarioEntity(
            nombre = "A".repeat(1000), // Nombre muy largo
            unidad = "L",
            cantidadDisponible = 10.0f,
            costoPorUnidad = 5.0,
            proveedor = "Test",
            fechaIngreso = System.currentTimeMillis()
        )

        // When
        val startTime = System.currentTimeMillis()
        repeat(100) { // Validar 100 veces
            Validators.validateIngrediente(longNameIngredient)
        }
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime

        // Then
        assertTrue("Validación de nombres largos debería ser eficiente", duration < 100)
    }

    @Test
    fun `validateFormula should handle very long descriptions efficiently`() {
        // Given
        val longDescFormula = FormulaEntity(
            nombre = "Test",
            descripcion = "A".repeat(1000), // Descripción muy larga
            rendimiento = 10.0,
            costoTotal = 25.0,
            fechaCreacion = System.currentTimeMillis()
        )

        // When
        val startTime = System.currentTimeMillis()
        repeat(100) { // Validar 100 veces
            Validators.validateFormula(longDescFormula)
        }
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime

        // Then
        assertTrue("Validación de descripciones largas debería ser eficiente", duration < 100)
    }

    @Test
    fun `concurrent validation should work correctly`() {
        // Given
        val ingredients = (1..100).map { index ->
            IngredienteInventarioEntity(
                nombre = "Ingrediente $index",
                unidad = "L",
                cantidadDisponible = 10.0f,
                costoPorUnidad = 5.0,
                proveedor = "Test",
                fechaIngreso = System.currentTimeMillis()
            )
        }

        // When
        val startTime = System.currentTimeMillis()
        val results = ingredients.parallelStream().map { ingredient ->
            Validators.validateIngrediente(ingredient)
        }.toList()
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime

        // Then
        assertTrue("Validación concurrente debería ser eficiente", duration < 500)
        assertEquals("Todos los ingredientes deberían ser válidos", 100, results.count { it.isSuccess() })
    }

    @Test
    fun `memory usage should be reasonable for large batches`() {
        // Given
        val runtime = Runtime.getRuntime()
        val initialMemory = runtime.totalMemory() - runtime.freeMemory()

        // When
        val largeBatch = (1..10000).map { index ->
            IngredienteInventarioEntity(
                nombre = "Ingrediente $index",
                unidad = "L",
                cantidadDisponible = 10.0f,
                costoPorUnidad = 5.0,
                proveedor = "Test",
                fechaIngreso = System.currentTimeMillis()
            )
        }

        val results = largeBatch.map { Validators.validateIngrediente(it) }
        
        // Forzar garbage collection para medir memoria real
        System.gc()
        val finalMemory = runtime.totalMemory() - runtime.freeMemory()
        val memoryUsed = finalMemory - initialMemory

        // Then
        assertTrue("Uso de memoria debería ser razonable (< 10MB)", memoryUsed < 10 * 1024 * 1024)
        assertEquals("Todos los ingredientes deberían ser válidos", 10000, results.count { it.isSuccess() })
    }
}

