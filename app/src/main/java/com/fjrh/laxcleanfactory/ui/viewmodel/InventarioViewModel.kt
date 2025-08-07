package com.fjrh.laxcleanfactory.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fjrh.laxcleanfactory.data.local.repository.InventarioRepository
import com.fjrh.laxcleanfactory.data.local.ConfiguracionDataStore
import com.fjrh.laxcleanfactory.domain.model.ConfiguracionStock
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventarioViewModel @Inject constructor(
    private val repository: InventarioRepository,
    private val configuracionDataStore: ConfiguracionDataStore
) : ViewModel() {

    val ingredientes = repository.getIngredientesInventario()

    val configuracion: StateFlow<ConfiguracionStock> = configuracionDataStore.configuracion.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ConfiguracionStock()
    )

    fun eliminarIngrediente(ingrediente: com.fjrh.laxcleanfactory.data.local.entity.IngredienteInventarioEntity) {
        viewModelScope.launch {
            repository.eliminarIngrediente(ingrediente)
        }
    }

    fun actualizarIngrediente(ingrediente: com.fjrh.laxcleanfactory.data.local.entity.IngredienteInventarioEntity) {
        viewModelScope.launch {
            repository.actualizarIngrediente(ingrediente)
        }
    }

    fun agregarIngrediente(ingrediente: com.fjrh.laxcleanfactory.data.local.entity.IngredienteInventarioEntity) {
        viewModelScope.launch {
            repository.insertarIngrediente(ingrediente)
        }
    }
}
