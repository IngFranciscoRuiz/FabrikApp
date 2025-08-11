package com.fjrh.FabrikApp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notas")
data class NotaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val titulo: String,
    val contenido: String,
    val fecha: Long = System.currentTimeMillis(),
    val esRecordatorio: Boolean = false,
    val fechaRecordatorio: Long? = null,
    val esCompletada: Boolean = false
) 
