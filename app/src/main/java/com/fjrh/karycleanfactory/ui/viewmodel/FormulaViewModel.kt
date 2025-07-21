package com.fjrh.karycleanfactory.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fjrh.karycleanfactory.data.local.dao.FormulaDao
import com.fjrh.karycleanfactory.data.local.entity.FormulaConIngredientes
import com.fjrh.karycleanfactory.data.local.entity.FormulaEntity
import com.fjrh.karycleanfactory.data.local.entity.IngredienteEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch

class FormulaViewModel(private val dao: FormulaDao) : ViewModel() {

    fun guardarFormulaConIngredientes(nombre: String, ingredientes: List<IngredienteEntity>) {
        viewModelScope.launch {
            val formula = FormulaEntity(nombre = nombre)
            dao.insertarFormulaConIngredientes(formula, ingredientes)
        }
    }

    suspend fun obtenerFormulas(): List<FormulaConIngredientes> {
        return withContext(Dispatchers.IO) {
            dao.getAllFormulasConIngredientes()
        }
    }
}
