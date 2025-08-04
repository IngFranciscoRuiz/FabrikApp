package com.fjrh.karycleanfactory.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "unidades_medida")
data class UnidadMedidaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String, // L, ml, gr, Kg, Pzas
    val descripcion: String?, // Litros, Mililitros, Gramos, Kilogramos, Piezas
    val esActiva: Boolean = true
) 