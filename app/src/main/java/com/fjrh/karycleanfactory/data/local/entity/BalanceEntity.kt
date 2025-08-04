package com.fjrh.karycleanfactory.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "balance")
data class BalanceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fecha: Long,
    val tipo: String, // "INGRESO" o "EGRESO"
    val concepto: String,
    val monto: Double,
    val descripcion: String? = null
) 