package com.fjrh.FabrikApp

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * Configuración para tests unitarios
 */
object TestConfig {
    
    /**
     * Timeout para tests (en milisegundos)
     */
    const val TEST_TIMEOUT = 5000L
    
    /**
     * Datos de prueba para ingredientes
     */
    object TestData {
        val VALID_INGREDIENT_NAMES = listOf(
            "Aceite de Coco",
            "Sosa Cáustica",
            "Aceite Esencial de Lavanda",
            "Glicerina",
            "Colorante Natural"
        )
        
        val VALID_UNITS = listOf(
            "L",
            "kg",
            "ml",
            "g",
            "unidades"
        )
        
        val VALID_FORMULA_NAMES = listOf(
            "Jabón de Lavanda Clásico",
            "Jabón de Coco y Miel",
            "Jabón Exfoliante de Avena",
            "Jabón de Aloe Vera",
            "Jabón de Caléndula"
        )
        
        val VALID_PRODUCT_NAMES = listOf(
            "Jabón de Lavanda Clásico",
            "Jabón de Coco y Miel",
            "Jabón Exfoliante de Avena",
            "Jabón de Aloe Vera",
            "Jabón de Caléndula"
        )
    }
    
    /**
     * Regla para tests que requieren timeout
     */
    class TimeoutRule(private val timeoutMs: Long = TEST_TIMEOUT) : TestRule {
        override fun apply(base: Statement, description: Description): Statement {
            return object : Statement() {
                override fun evaluate() {
                    val startTime = System.currentTimeMillis()
                    try {
                        base.evaluate()
                    } finally {
                        val endTime = System.currentTimeMillis()
                        val duration = endTime - startTime
                        if (duration > timeoutMs) {
                            throw RuntimeException("Test exceeded timeout of ${timeoutMs}ms (took ${duration}ms)")
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Utilidades para tests
     */
    object TestUtils {
        
        /**
         * Genera un nombre de ingrediente aleatorio
         */
        fun randomIngredientName(): String {
            return TestData.VALID_INGREDIENT_NAMES.random()
        }
        
        /**
         * Genera una unidad aleatoria
         */
        fun randomUnit(): String {
            return TestData.VALID_UNITS.random()
        }
        
        /**
         * Genera un nombre de fórmula aleatorio
         */
        fun randomFormulaName(): String {
            return TestData.VALID_FORMULA_NAMES.random()
        }
        
        /**
         * Genera un nombre de producto aleatorio
         */
        fun randomProductName(): String {
            return TestData.VALID_PRODUCT_NAMES.random()
        }
        
        /**
         * Genera una cantidad aleatoria positiva
         */
        fun randomPositiveQuantity(): Float {
            return (1..100).random().toFloat()
        }
        
        /**
         * Genera un costo aleatorio positivo
         */
        fun randomPositiveCost(): Double {
            return (1.0..50.0).random()
        }
        
        /**
         * Genera un precio aleatorio positivo
         */
        fun randomPositivePrice(): Double {
            return (1.0..20.0).random()
        }
        
        /**
         * Genera un rendimiento aleatorio positivo
         */
        fun randomPositiveYield(): Double {
            return (1.0..50.0).random()
        }
        
        /**
         * Genera un email válido aleatorio
         */
        fun randomValidEmail(): String {
            val domains = listOf("example.com", "test.org", "demo.net", "sample.co.uk")
            val names = listOf("user", "test", "demo", "sample", "admin")
            val name = names.random()
            val domain = domains.random()
            return "$name@$domain"
        }
        
        /**
         * Genera un teléfono válido aleatorio
         */
        fun randomValidPhone(): String {
            val countryCodes = listOf("+1", "+44", "+34", "+52", "")
            val countryCode = countryCodes.random()
            val number = (100000000..999999999).random().toString()
            return "$countryCode$number"
        }
    }
}

