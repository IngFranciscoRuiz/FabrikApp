package com.fjrh.laxcleanfactory.domain.service

import com.fjrh.laxcleanfactory.domain.model.ConfiguracionStock
import com.fjrh.laxcleanfactory.domain.model.StockProducto
import com.fjrh.laxcleanfactory.data.local.entity.IngredienteInventarioEntity
import javax.inject.Inject

enum class StockLevel {
    ALTO, MEDIO, BAJO
}

class StockAlertService @Inject constructor() {
    
    fun getStockLevelProducto(stock: Float, config: ConfiguracionStock): StockLevel {
        return when {
            stock >= config.stockAltoProductos -> StockLevel.ALTO
            stock >= config.stockMedioProductos -> StockLevel.MEDIO
            else -> StockLevel.BAJO
        }
    }
    
    fun getStockLevelInsumo(stock: Float, config: ConfiguracionStock): StockLevel {
        return when {
            stock >= config.stockAltoInsumos -> StockLevel.ALTO
            stock >= config.stockMedioInsumos -> StockLevel.MEDIO
            else -> StockLevel.BAJO
        }
    }
    
    fun getStockColorProducto(stock: Float, config: ConfiguracionStock): androidx.compose.ui.graphics.Color {
        return when (getStockLevelProducto(stock, config)) {
            StockLevel.ALTO -> androidx.compose.ui.graphics.Color(0xFF4CAF50) // Verde
            StockLevel.MEDIO -> androidx.compose.ui.graphics.Color(0xFFFF9800) // Naranja
            StockLevel.BAJO -> androidx.compose.ui.graphics.Color(0xFFF44336) // Rojo
        }
    }
    
    fun getStockColorInsumo(stock: Float, config: ConfiguracionStock): androidx.compose.ui.graphics.Color {
        return when (getStockLevelInsumo(stock, config)) {
            StockLevel.ALTO -> androidx.compose.ui.graphics.Color(0xFF4CAF50) // Verde
            StockLevel.MEDIO -> androidx.compose.ui.graphics.Color(0xFFFF9800) // Naranja
            StockLevel.BAJO -> androidx.compose.ui.graphics.Color(0xFFF44336) // Rojo
        }
    }
    
    fun getStockTextProducto(stock: Float, config: ConfiguracionStock): String {
        return when (getStockLevelProducto(stock, config)) {
            StockLevel.ALTO -> "Stock Alto"
            StockLevel.MEDIO -> "Stock Medio"
            StockLevel.BAJO -> "Stock Bajo"
        }
    }
    
    fun getStockTextInsumo(stock: Float, config: ConfiguracionStock): String {
        return when (getStockLevelInsumo(stock, config)) {
            StockLevel.ALTO -> "Stock Alto"
            StockLevel.MEDIO -> "Stock Medio"
            StockLevel.BAJO -> "Stock Bajo"
        }
    }
    

} 