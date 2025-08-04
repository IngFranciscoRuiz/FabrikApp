package com.fjrh.karycleanfactory.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ventas")
data class VentaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombreProducto: String,
    val litrosVendidos: Float,
    val precioPorLitro: Double,
    val fecha: Long,
    val cliente: String? = null
) 