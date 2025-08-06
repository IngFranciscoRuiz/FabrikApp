package com.fjrh.laxcleanfactory.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "historial_produccion")
data class HistorialProduccionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombreFormula: String,
    val litrosProducidos: Float,
    val fecha: Long // se guarda como timestamp
)
