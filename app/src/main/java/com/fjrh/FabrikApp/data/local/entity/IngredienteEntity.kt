package com.fjrh.FabrikApp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ingredientes",
    foreignKeys = [
        ForeignKey(
            entity = FormulaEntity::class,
            parentColumns = ["id"],
            childColumns = ["formulaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("formulaId")]
)
data class IngredienteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val formulaId: Long,
    val nombre: String,
    val unidad: String,
    val cantidad: String,
    val costoPorUnidad: Double = 0.0
)
