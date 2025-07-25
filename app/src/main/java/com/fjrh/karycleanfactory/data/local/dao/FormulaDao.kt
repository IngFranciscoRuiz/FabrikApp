package com.fjrh.karycleanfactory.data.local.dao

import androidx.room.*
import com.fjrh.karycleanfactory.data.local.entity.*

import kotlinx.coroutines.flow.Flow

@Dao
interface FormulaDao {

    // 🧪 FORMULAS
    @Transaction
    @Query("SELECT * FROM formulas")
    fun getAllFormulasConIngredientes(): Flow<List<FormulaConIngredientes>>

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

    // 🕒 HISTORIAL DE PRODUCCIÓN
    @Insert
    suspend fun insertarHistorial(historial: HistorialProduccionEntity)

    @Query("SELECT * FROM historial_produccion ORDER BY fecha DESC")
    suspend fun obtenerHistorial(): List<HistorialProduccionEntity>

    // 📦 INVENTARIO DE INGREDIENTES
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarIngredienteInventario(ingrediente: IngredienteInventarioEntity)

    @Update
    suspend fun actualizarIngredienteInventario(ingrediente: IngredienteInventarioEntity)

    @Delete
    suspend fun eliminarIngredienteInventario(ingrediente: IngredienteInventarioEntity)

    @Query("SELECT * FROM ingredientes_inventario ORDER BY nombre ASC")
    fun getIngredientesInventario(): Flow<List<IngredienteInventarioEntity>>
}
