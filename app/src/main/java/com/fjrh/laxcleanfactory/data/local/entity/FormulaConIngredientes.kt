package com.fjrh.laxcleanfactory.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class FormulaConIngredientes(
    @Embedded val formula: FormulaEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "formulaId"
    )
    val ingredientes: List<IngredienteEntity>
)
