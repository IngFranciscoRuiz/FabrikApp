package com.fjrh.karycleanfactory.data.local.dao

import androidx.room.*
import com.fjrh.karycleanfactory.data.local.entity.FormulaConIngredientes
import com.fjrh.karycleanfactory.data.local.entity.FormulaEntity
import com.fjrh.karycleanfactory.data.local.entity.IngredienteEntity
import com.fjrh.karycleanfactory.data.local.entity.IngredienteInventarioEntity

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

    // Historial de producción
    @Insert
    suspend fun insertarHistorial(historial: com.fjrh.karycleanfactory.data.local.entity.HistorialProduccionEntity)

    @Query("SELECT * FROM historial_produccion ORDER BY fecha DESC")
    suspend fun obtenerHistorial(): List<com.fjrh.karycleanfactory.data.local.entity.HistorialProduccionEntity>

    // Ingredientes de inventario
    @Insert
    suspend fun insertarIngrediente(ingrediente: IngredienteInventarioEntity)

    @Update
    suspend fun actualizarIngrediente(ingrediente: IngredienteInventarioEntity)

    @Delete
    suspend fun eliminarIngrediente(ingrediente: IngredienteInventarioEntity)

    @Query("SELECT * FROM ingredientes_inventario ORDER BY nombre ASC")
    suspend fun obtenerIngredientesInventario(): List<IngredienteInventarioEntity>

    // 🧪 INVENTARIO DE INGREDIENTES

    @Insert
    suspend fun insertarIngredienteInventario(ingrediente: com.fjrh.karycleanfactory.data.local.entity.IngredienteInventarioEntity)

    @Query("SELECT * FROM ingredientes_inventario ORDER BY fechaIngreso DESC")
    suspend fun obtenerInventario(): List<com.fjrh.karycleanfactory.data.local.entity.IngredienteInventarioEntity>



}
