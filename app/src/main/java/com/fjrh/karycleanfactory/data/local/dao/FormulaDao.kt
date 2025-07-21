package com.fjrh.karycleanfactory.data.local.dao

import androidx.room.*
import com.fjrh.karycleanfactory.data.local.entity.FormulaConIngredientes
import com.fjrh.karycleanfactory.data.local.entity.FormulaEntity
import com.fjrh.karycleanfactory.data.local.entity.IngredienteEntity

@Dao
interface FormulaDao {

    @Transaction
    @Query("SELECT * FROM formulas")
    suspend fun getAllFormulasConIngredientes(): List<FormulaConIngredientes>

    @Insert
    suspend fun insertFormula(formula: FormulaEntity): Long

    @Insert
    suspend fun insertIngredientes(ingredientes: List<IngredienteEntity>)

    @Transaction
    suspend fun insertarFormulaConIngredientes(
        formula: FormulaEntity,
        ingredientes: List<IngredienteEntity>
    ) {
        val formulaId = insertFormula(formula)
        val ingredientesConId = ingredientes.map {
            it.copy(formulaId = formulaId)
        }
        insertIngredientes(ingredientesConId)
    }

    @Delete
    suspend fun deleteFormula(formula: FormulaEntity)

    @Query("DELETE FROM ingredientes WHERE formulaId = :formulaId")
    suspend fun deleteIngredientesByFormulaId(formulaId: Long)
}
