package com.fjrh.FabrikApp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fjrh.FabrikApp.data.local.repository.InventarioRepository
import com.fjrh.FabrikApp.data.local.ConfiguracionDataStore
import com.fjrh.FabrikApp.domain.model.ConfiguracionStock
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

    fun eliminarIngrediente(ingrediente: com.fjrh.FabrikApp.data.local.entity.IngredienteInventarioEntity) {
        viewModelScope.launch {
            repository.eliminarIngrediente(ingrediente)
        }
    }

    fun actualizarIngrediente(ingrediente: com.fjrh.FabrikApp.data.local.entity.IngredienteInventarioEntity) {
        viewModelScope.launch {
            repository.actualizarIngrediente(ingrediente)
        }
    }

    fun agregarIngrediente(ingrediente: com.fjrh.FabrikApp.data.local.entity.IngredienteInventarioEntity) {
        viewModelScope.launch {
            repository.insertarIngrediente(ingrediente)
        }
    }
}
