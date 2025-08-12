package com.fjrh.FabrikApp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fjrh.FabrikApp.data.local.repository.InventarioRepository
import com.fjrh.FabrikApp.data.local.ConfiguracionDataStore
import com.fjrh.FabrikApp.domain.model.ConfiguracionStock
import com.fjrh.FabrikApp.domain.util.UnitConversionUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class InventarioViewModel @Inject constructor(
    private val repository: InventarioRepository,
    private val configuracionDataStore: ConfiguracionDataStore,
    private val formulaRepository: com.fjrh.FabrikApp.data.local.repository.FormulaRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

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
            try {
                // 1. Actualizar el ingrediente en inventario
                repository.actualizarIngrediente(ingrediente)
                
                // 2. Actualizar costos en todas las fórmulas que usen este ingrediente
                actualizarCostosEnFormulas(ingrediente.nombre, ingrediente.costoPorUnidad)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    private suspend fun actualizarCostosEnFormulas(nombreIngrediente: String, nuevoCostoInventario: Double) {
        try {
            // Get the original unit of the ingredient from inventory
            val ingredienteInventarioOriginal = repository.getIngredientesInventario().first()
                .find { it.nombre == nombreIngrediente } ?: return // If not found, nothing to do

            // Obtener todas las fórmulas
            val formulas = formulaRepository.obtenerFormulasConIngredientes().first()

            // Para cada fórmula que contenga el ingrediente modificado
            formulas.forEach { formulaConIngredientes ->
                val ingredientesActualizados = formulaConIngredientes.ingredientes.map { ingredienteEnFormula ->
                    if (ingredienteEnFormula.nombre == nombreIngrediente) {
                        // Convert the new inventory cost to the unit of the ingredient in the formula
                        val costoConvertidoParaFormula = UnitConversionUtil.calcularCostoPorUnidad(
                            ingredienteInventarioOriginal.copy(costoPorUnidad = nuevoCostoInventario), // Use the updated inventory cost
                            ingredienteEnFormula.unidad // Target unit is the unit of the ingredient in the formula
                        )
                        ingredienteEnFormula.copy(costoPorUnidad = costoConvertidoParaFormula)
                    } else {
                        ingredienteEnFormula
                    }
                }

                // Solo actualizar si hubo cambios
                if (ingredientesActualizados != formulaConIngredientes.ingredientes) {
                    // Eliminar ingredientes existentes
                    formulaRepository.eliminarIngredientesByFormulaId(formulaConIngredientes.formula.id)

                    // Insertar ingredientes actualizados
                    val ingredientesEntities = ingredientesActualizados.map { ingrediente ->
                        com.fjrh.FabrikApp.data.local.entity.IngredienteEntity(
                            formulaId = formulaConIngredientes.formula.id,
                            nombre = ingrediente.nombre,
                            unidad = ingrediente.unidad,
                            cantidad = ingrediente.cantidad,
                            costoPorUnidad = ingrediente.costoPorUnidad
                        )
                    }

                    formulaRepository.insertarIngredientes(ingredientesEntities)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun agregarIngrediente(ingrediente: com.fjrh.FabrikApp.data.local.entity.IngredienteInventarioEntity) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.insertarIngrediente(ingrediente)
            } catch (e: Exception) {
                // TODO: Manejar error - podríamos emitir un evento de error
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
