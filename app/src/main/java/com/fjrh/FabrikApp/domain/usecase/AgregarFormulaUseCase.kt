package com.fjrh.FabrikApp.domain.usecase

import com.fjrh.FabrikApp.domain.model.Formula
import com.fjrh.FabrikApp.data.local.repository.FormulaRepository
import com.fjrh.FabrikApp.data.local.entity.FormulaEntity
import com.fjrh.FabrikApp.data.local.entity.IngredienteEntity
import javax.inject.Inject

class AgregarFormulaUseCase @Inject constructor(
    private val repository: FormulaRepository
) {
    suspend operator fun invoke(formula: Formula): FormulaEntity {
        val formulaEntity = FormulaEntity(nombre = formula.nombre)
        val ingredientes = formula.ingredientes.map { ingrediente ->
            IngredienteEntity(
                formulaId = 0, // Se asignará correctamente en el DAO
                nombre = ingrediente.nombre,
                unidad = ingrediente.unidad,
                cantidad = ingrediente.cantidad,
                costoPorUnidad = ingrediente.costoPorUnidad
            )
        }
        
        // Insertar la fórmula primero para obtener el ID
        val formulaId = repository.insertarFormula(formulaEntity)
        val formulaConId = formulaEntity.copy(id = formulaId)
        
        // Insertar ingredientes con el ID correcto de la fórmula
        val ingredientesConId = ingredientes.map { it.copy(formulaId = formulaId) }
        repository.insertarIngredientes(ingredientesConId)
        
        return formulaConId
    }
} 
