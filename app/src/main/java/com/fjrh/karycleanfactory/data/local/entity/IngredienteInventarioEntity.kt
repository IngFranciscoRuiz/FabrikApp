package com.fjrh.karycleanfactory.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ingredientes_inventario")
data class IngredienteInventarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val unidad: String,
    val cantidadDisponible: Float,
    val proveedor: String?,
    val fechaIngreso: Long
)

