package com.fjrh.laxcleanfactory.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fjrh.laxcleanfactory.data.local.entity.IngredienteInventarioEntity
import com.fjrh.laxcleanfactory.data.local.repository.InventarioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventarioViewModel @Inject constructor(
    private val repository: InventarioRepository
) : ViewModel() {

    val ingredientes = repository.getIngredientesInventario()
        .map { it.sortedBy { ingrediente -> ingrediente.nombre.lowercase() } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun agregarIngrediente(ingrediente: IngredienteInventarioEntity) {
        viewModelScope.launch {
            repository.insertarIngrediente(ingrediente)
        }
    }

    fun actualizarIngrediente(ingrediente: IngredienteInventarioEntity) {
        viewModelScope.launch {
            repository.actualizarIngrediente(ingrediente)
        }
    }

    fun eliminarIngrediente(ingrediente: IngredienteInventarioEntity) {
        viewModelScope.launch {
            repository.eliminarIngrediente(ingrediente)
        }
    }
}
