package com.fjrh.karycleanfactory.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fjrh.karycleanfactory.data.local.entity.UnidadMedidaEntity
import com.fjrh.karycleanfactory.data.local.repository.FormulaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UnidadesViewModel @Inject constructor(
    private val repository: FormulaRepository
) : ViewModel() {

    val unidades: StateFlow<List<UnidadMedidaEntity>> =
        repository.getUnidadesMedida()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertarUnidad(unidad: UnidadMedidaEntity) {
        viewModelScope.launch {
            repository.insertarUnidadMedida(unidad)
        }
    }

    fun actualizarUnidad(unidad: UnidadMedidaEntity) {
        viewModelScope.launch {
            repository.actualizarUnidadMedida(unidad)
        }
    }

    fun eliminarUnidad(unidad: UnidadMedidaEntity) {
        viewModelScope.launch {
            repository.eliminarUnidadMedida(unidad)
        }
    }

    // Función para precargar unidades básicas si no existen
    fun precargarUnidadesBasicas() {
        viewModelScope.launch {
            val unidadesExistentes = repository.getUnidadesMedidaSync()
            if (unidadesExistentes.isEmpty()) {
                val unidadesBasicas = listOf(
                    UnidadMedidaEntity(nombre = "L", descripcion = "Litros"),
                    UnidadMedidaEntity(nombre = "ml", descripcion = "Mililitros"),
                    UnidadMedidaEntity(nombre = "gr", descripcion = "Gramos"),
                    UnidadMedidaEntity(nombre = "Kg", descripcion = "Kilogramos"),
                    UnidadMedidaEntity(nombre = "Pzas", descripcion = "Piezas")
                )
                unidadesBasicas.forEach { unidad ->
                    repository.insertarUnidadMedida(unidad)
                }
            }
        }
    }
} 