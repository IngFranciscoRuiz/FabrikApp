package com.fjrh.karycleanfactory.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fjrh.karycleanfactory.data.local.dao.FormulaDao
import com.fjrh.karycleanfactory.data.local.entity.IngredienteInventarioEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class InventarioViewModel @Inject constructor(
    private val dao: FormulaDao
) : ViewModel() {

    private val _inventario = MutableStateFlow<List<IngredienteInventarioEntity>>(emptyList())
    val inventario: StateFlow<List<IngredienteInventarioEntity>> = _inventario.asStateFlow()

    init {
        cargarInventario()
    }

    fun cargarInventario() {
        viewModelScope.launch {
            val lista = withContext(Dispatchers.IO) {
                dao.obtenerInventario()
            }
            _inventario.value = lista
        }
    }

    fun insertarIngrediente(ingrediente: IngredienteInventarioEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dao.insertarIngredienteInventario(ingrediente)
            }
            cargarInventario()
        }
    }
}
