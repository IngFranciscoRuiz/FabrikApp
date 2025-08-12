package com.fjrh.FabrikApp.domain.model

data class ConfiguracionStock(
    // Productos terminados
    val stockAltoProductos: Float = 100f,
    val stockMedioProductos: Float = 50f,
    val stockBajoProductos: Float = 25f,
    // Insumos
    val stockAltoInsumos: Float = 200f,
    val stockMedioInsumos: Float = 100f,
    val stockBajoInsumos: Float = 50f,
    // Alertas
    val alertasStockBajo: Boolean = true,
    val alertasStockAlto: Boolean = false,
    // Backup
    val backupAutomatico: Boolean = true,
    val frecuenciaBackup: Int = 7,
    // Tema
    val temaOscuro: Boolean = false,

) 