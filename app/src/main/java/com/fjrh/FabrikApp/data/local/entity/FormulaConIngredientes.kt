package com.fjrh.FabrikApp.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class FormulaConIngredientes(
    @Embedded val formula: FormulaEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "formulaId"
    )
    val ingredientes: List<IngredienteEntity>
) {
    // Función para convertir a formato serializable simple
    fun toSerializable(): FormulaSerializable {
        return FormulaSerializable(
            id = formula.id,
            nombre = formula.nombre,
            ingredientes = ingredientes.map { ingrediente ->
                IngredienteSerializable(
                    nombre = ingrediente.nombre,
                    unidad = ingrediente.unidad,
                    cantidad = ingrediente.cantidad,
                    costoPorUnidad = ingrediente.costoPorUnidad
                )
            }
        )
    }
}

// Clases serializables simples para navegación
data class FormulaSerializable(
    val id: Long,
    val nombre: String,
    val ingredientes: List<IngredienteSerializable>
)

data class IngredienteSerializable(
    val nombre: String,
    val unidad: String,
    val cantidad: String,
    val costoPorUnidad: Double
)
