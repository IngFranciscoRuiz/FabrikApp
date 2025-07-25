package com.fjrh.karycleanfactory.data.local.repository

import com.fjrh.karycleanfactory.data.local.dao.FormulaDao
import com.fjrh.karycleanfactory.data.local.entity.FormulaConIngredientes
import com.fjrh.karycleanfactory.data.local.entity.FormulaEntity
import com.fjrh.karycleanfactory.data.local.entity.IngredienteEntity
import com.fjrh.karycleanfactory.data.local.entity.HistorialProduccionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FormulaRepository @Inject constructor(
    private val formulaDao: FormulaDao
) {

    fun obtenerFormulasConIngredientes(): Flow<List<FormulaConIngredientes>> {
        return formulaDao.getAllFormulasConIngredientes()
    }

    suspend fun eliminarFormulaConIngredientes(formula: FormulaEntity) {
        formulaDao.deleteIngredientesByFormulaId(formula.id)
        formulaDao.deleteFormula(formula)
    }

    suspend fun insertarFormulaConIngredientes(
        formula: FormulaEntity,
        ingredientes: List<IngredienteEntity>
    ) {
        formulaDao.insertarFormulaConIngredientes(formula, ingredientes)
    }

    suspend fun insertarHistorial(historial: HistorialProduccionEntity) {
        formulaDao.insertarHistorial(historial)
    }

    suspend fun insertarFormula(formula: FormulaEntity): Long {
        return formulaDao.insertFormula(formula)
    }
}
