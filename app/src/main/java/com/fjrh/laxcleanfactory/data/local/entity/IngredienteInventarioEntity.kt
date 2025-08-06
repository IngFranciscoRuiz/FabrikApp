package com.fjrh.laxcleanfactory.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ingredientes_inventario")
data class IngredienteInventarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val unidad: String,
    val cantidadDisponible: Float,
    val costoPorUnidad: Double = 0.0,
    val proveedor: String?,
    val fechaIngreso: Long
)

