package com.fjrh.FabrikApp.data.local.repository

import com.fjrh.FabrikApp.data.local.dao.FormulaDao
import com.fjrh.FabrikApp.data.local.entity.IngredienteInventarioEntity

import javax.inject.Inject

class InventarioRepository @Inject constructor(
    private val dao: FormulaDao
) {
    fun getIngredientesInventario() = dao.getIngredientesInventario()

    suspend fun insertarIngrediente(ingrediente: IngredienteInventarioEntity) {
        dao.insertarIngredienteInventario(ingrediente)
    }

    suspend fun actualizarIngrediente(ingrediente: IngredienteInventarioEntity) {
        dao.actualizarIngredienteInventario(ingrediente)
    }

    suspend fun eliminarIngrediente(ingrediente: IngredienteInventarioEntity) {
        dao.eliminarIngredienteInventario(ingrediente)
    }
}
