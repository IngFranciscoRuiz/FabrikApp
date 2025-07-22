package com.fjrh.karycleanfactory.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fjrh.karycleanfactory.data.local.dao.FormulaDao
import com.fjrh.karycleanfactory.data.local.entity.FormulaConIngredientes
import com.fjrh.karycleanfactory.data.local.entity.FormulaEntity
import com.fjrh.karycleanfactory.data.local.entity.IngredienteEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.fjrh.karycleanfactory.data.local.entity.HistorialProduccionEntity

class FormulaViewModel(private val dao: FormulaDao) : ViewModel() {

    private val _formulas = MutableStateFlow<List<FormulaConIngredientes>>(emptyList())
    val formulas: StateFlow<List<FormulaConIngredientes>> = _formulas.asStateFlow()

    init {
        cargarFormulas()
    }

    private fun cargarFormulas() {
        viewModelScope.launch {
            val lista = withContext(Dispatchers.IO) {
                dao.getAllFormulasConIngredientes()
            }
            _formulas.value = lista
        }
    }

    fun guardarFormulaConIngredientes(nombre: String, ingredientes: List<IngredienteEntity>) {
        viewModelScope.launch {
            val formula = FormulaEntity(nombre = nombre)
            dao.insertarFormulaConIngredientes(formula, ingredientes)
            cargarFormulas() // Recargar la lista después de guardar
        }
    }
    fun insertarHistorial(historial: HistorialProduccionEntity) {
        viewModelScope.launch {
            dao.insertarHistorial(historial)
        }
    }
}
