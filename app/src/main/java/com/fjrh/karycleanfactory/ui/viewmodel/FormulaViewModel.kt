package com.fjrh.karycleanfactory.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fjrh.karycleanfactory.data.local.dao.FormulaDao
import com.fjrh.karycleanfactory.data.local.entity.FormulaEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class FormulaViewModel(private val dao: FormulaDao) : ViewModel() {

    val formulas = dao.getAll()
        .map { it }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertarFormula() {
        viewModelScope.launch {
            dao.insert(
                FormulaEntity(
                    nombre = "Multiusos",
                    descripcion = "Agua, detergente y esencia",
                    litros = 20.5
                )
            )
        }
    }
}
