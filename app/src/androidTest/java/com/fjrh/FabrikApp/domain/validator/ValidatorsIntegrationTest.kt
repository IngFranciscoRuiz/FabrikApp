package com.fjrh.FabrikApp.domain.validator

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.fjrh.FabrikApp.data.local.entity.IngredienteInventarioEntity
import com.fjrh.FabrikApp.data.local.entity.FormulaEntity
import com.fjrh.FabrikApp.data.local.entity.VentaEntity
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ValidatorsIntegrationTest {

    @Before
    fun setUp() {
        // Setup para tests de integración
    }

    @Test
    fun `validateIngrediente should handle real world scenarios`() {
        // Test con datos reales de ingredientes
        val ingredientes = listOf(
            IngredienteInventarioEntity(
                nombre = "Aceite de Coco",
                unidad = "L",
                cantidadDisponible = 5.5f,
                costoPorUnidad = 12.50,
                proveedor = "Proveedor Natural",
                fechaIngreso = System.currentTimeMillis()
            ),
            IngredienteInventarioEntity(
                nombre = "Sosa Cáustica",
                unidad = "kg",
                cantidadDisponible = 2.0f,
                costoPorUnidad = 8.75,
                proveedor = "Química Industrial",
                fechaIngreso = System.currentTimeMillis()
            ),
            IngredienteInventarioEntity(
                nombre = "Aceite Esencial de Lavanda",
                unidad = "ml",
                cantidadDisponible = 100.0f,
                costoPorUnidad = 0.25,
                proveedor = "Aromas Naturales",
                fechaIngreso = System.currentTimeMillis()
            )
        )

        ingredientes.forEach { ingrediente ->
            val result = Validators.validateIngrediente(ingrediente)
            assertTrue("Ingrediente ${ingrediente.nombre} debería ser válido", result.isSuccess())
        }
    }

    @Test
    fun `validateFormula should handle real world scenarios`() {
        // Test con fórmulas reales
        val formulas = listOf(
            FormulaEntity(
                nombre = "Jabón de Lavanda Clásico",
                descripcion = "Jabón artesanal con aceite esencial de lavanda para piel sensible",
                rendimiento = 15.0,
                costoTotal = 45.75,
                fechaCreacion = System.currentTimeMillis()
            ),
            FormulaEntity(
                nombre = "Jabón de Coco y Miel",
                descripcion = "Jabón hidratante con aceite de coco y miel natural",
                rendimiento = 12.0,
                costoTotal = 38.90,
                fechaCreacion = System.currentTimeMillis()
            ),
            FormulaEntity(
                nombre = "Jabón Exfoliante de Avena",
                descripcion = "Jabón exfoliante con avena molida para renovación celular",
                rendimiento = 8.0,
                costoTotal = 28.50,
                fechaCreacion = System.currentTimeMillis()
            )
        )

        formulas.forEach { formula ->
            val result = Validators.validateFormula(formula)
            assertTrue("Fórmula ${formula.nombre} debería ser válida", result.isSuccess())
        }
    }

    @Test
    fun `validateVenta should handle real world scenarios`() {
        // Test con ventas reales
        val ventas = listOf(
            VentaEntity(
                nombreProducto = "Jabón de Lavanda Clásico",
                litrosVendidos = 3.5,
                precioPorLitro = 4.50,
                fecha = System.currentTimeMillis()
            ),
            VentaEntity(
                nombreProducto = "Jabón de Coco y Miel",
                litrosVendidos = 2.0,
                precioPorLitro = 5.25,
                fecha = System.currentTimeMillis()
            ),
            VentaEntity(
                nombreProducto = "Jabón Exfoliante de Avena",
                litrosVendidos = 1.5,
                precioPorLitro = 6.00,
                fecha = System.currentTimeMillis()
            )
        )

        ventas.forEach { venta ->
            val result = Validators.validateVenta(venta)
            assertTrue("Venta de ${venta.nombreProducto} debería ser válida", result.isSuccess())
        }
    }

    @Test
    fun `validateIngrediente should handle edge cases`() {
        // Test casos límite
        val edgeCases = listOf(
            // Nombre muy corto
            IngredienteInventarioEntity(
                nombre = "A",
                unidad = "L",
                cantidadDisponible = 10.0f,
                costoPorUnidad = 5.0,
                proveedor = "Test",
                fechaIngreso = System.currentTimeMillis()
            ),
            // Nombre muy largo
            IngredienteInventarioEntity(
                nombre = "A".repeat(51),
                unidad = "L",
                cantidadDisponible = 10.0f,
                costoPorUnidad = 5.0,
                proveedor = "Test",
                fechaIngreso = System.currentTimeMillis()
            ),
            // Cantidad cero
            IngredienteInventarioEntity(
                nombre = "Test",
                unidad = "L",
                cantidadDisponible = 0.0f,
                costoPorUnidad = 5.0,
                proveedor = "Test",
                fechaIngreso = System.currentTimeMillis()
            )
        )

        val results = edgeCases.map { Validators.validateIngrediente(it) }
        
        // El primer caso debería fallar por nombre muy corto
        assertFalse(results[0].isSuccess())
        assertTrue(results[0].getErrors().any { it.contains("al menos 2 caracteres") })
        
        // El segundo caso debería fallar por nombre muy largo
        assertFalse(results[1].isSuccess())
        assertTrue(results[1].getErrors().any { it.contains("50 caracteres") })
        
        // El tercer caso debería ser válido (cantidad cero está permitida)
        assertTrue(results[2].isSuccess())
    }

    @Test
    fun `validateFormula should handle edge cases`() {
        // Test casos límite para fórmulas
        val edgeCases = listOf(
            // Descripción muy larga
            FormulaEntity(
                nombre = "Test",
                descripcion = "A".repeat(501),
                rendimiento = 10.0,
                costoTotal = 25.0,
                fechaCreacion = System.currentTimeMillis()
            ),
            // Rendimiento negativo
            FormulaEntity(
                nombre = "Test",
                descripcion = "Test",
                rendimiento = -5.0,
                costoTotal = 25.0,
                fechaCreacion = System.currentTimeMillis()
            ),
            // Costo total negativo
            FormulaEntity(
                nombre = "Test",
                descripcion = "Test",
                rendimiento = 10.0,
                costoTotal = -25.0,
                fechaCreacion = System.currentTimeMillis()
            )
        )

        val results = edgeCases.map { Validators.validateFormula(it) }
        
        // El primer caso debería fallar por descripción muy larga
        assertFalse(results[0].isSuccess())
        assertTrue(results[0].getErrors().any { it.contains("500 caracteres") })
        
        // El segundo caso debería fallar por rendimiento negativo
        assertFalse(results[1].isSuccess())
        assertTrue(results[1].getErrors().any { it.contains("mayor a 0") })
        
        // El tercer caso debería fallar por costo negativo
        assertFalse(results[2].isSuccess())
        assertTrue(results[2].getErrors().any { it.contains("negativo") })
    }

    @Test
    fun `validateVenta should handle edge cases`() {
        // Test casos límite para ventas
        val edgeCases = listOf(
            // Litros negativos
            VentaEntity(
                nombreProducto = "Test",
                litrosVendidos = -5.0,
                precioPorLitro = 3.0,
                fecha = System.currentTimeMillis()
            ),
            // Precio negativo
            VentaEntity(
                nombreProducto = "Test",
                litrosVendidos = 5.0,
                precioPorLitro = -3.0,
                fecha = System.currentTimeMillis()
            ),
            // Fecha inválida
            VentaEntity(
                nombreProducto = "Test",
                litrosVendidos = 5.0,
                precioPorLitro = 3.0,
                fecha = 0L
            )
        )

        val results = edgeCases.map { Validators.validateVenta(it) }
        
        // Todos deberían fallar
        results.forEach { result ->
            assertFalse(result.isSuccess())
        }
    }

    @Test
    fun `validateEmail should handle various email formats`() {
        val validEmails = listOf(
            "test@example.com",
            "user.name@domain.co.uk",
            "user+tag@example.org",
            "123@test.com"
        )

        val invalidEmails = listOf(
            "invalid-email",
            "@example.com",
            "test@",
            "test@.com",
            "test..test@example.com"
        )

        validEmails.forEach { email ->
            val result = Validators.validateEmail(email)
            assertTrue("Email $email debería ser válido", result.isSuccess())
        }

        invalidEmails.forEach { email ->
            val result = Validators.validateEmail(email)
            assertFalse("Email $email debería ser inválido", result.isSuccess())
        }
    }

    @Test
    fun `validatePhone should handle various phone formats`() {
        val validPhones = listOf(
            "+1234567890",
            "1234567890",
            "+1-234-567-8900",
            "123456789012345"
        )

        val invalidPhones = listOf(
            "123",
            "abcdefghij",
            "+1234567890123456", // Demasiado largo
            ""
        )

        validPhones.forEach { phone ->
            val result = Validators.validatePhone(phone)
            assertTrue("Teléfono $phone debería ser válido", result.isSuccess())
        }

        invalidPhones.forEach { phone ->
            val result = Validators.validatePhone(phone)
            assertFalse("Teléfono $phone debería ser inválido", result.isSuccess())
        }
    }
}

