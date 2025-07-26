package com.fjrh.karycleanfactory.domain.usecase

import com.fjrh.karycleanfactory.domain.model.Formula
import com.fjrh.karycleanfactory.data.local.repository.FormulaRepository
import com.fjrh.karycleanfactory.data.local.entity.FormulaEntity
import com.fjrh.karycleanfactory.data.local.entity.IngredienteEntity
import javax.inject.Inject

class AgregarFormulaUseCase @Inject constructor(
    private val repository: FormulaRepository
) {
    suspend operator fun invoke(formula: Formula) {
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
        repository.insertarFormulaConIngredientes(
            formulaEntity,
            ingredientes
        )
    }
} 