package com.fjrh.FabrikApp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fjrh.FabrikApp.data.local.ConfiguracionDataStore
import com.fjrh.FabrikApp.data.local.repository.InventarioRepository
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

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    val ingredientes = repository.getIngredientesInventario()

    val configuracion: StateFlow<ConfiguracionStock> = configuracionDataStore.configuracion.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ConfiguracionStock()
    )

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }

    fun eliminarIngrediente(ingrediente: com.fjrh.FabrikApp.data.local.entity.IngredienteInventarioEntity) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                repository.eliminarIngrediente(ingrediente)
                _successMessage.value = "Ingrediente eliminado correctamente"
            } catch (e: Exception) {
                _errorMessage.value = "Error al eliminar ingrediente: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun actualizarIngrediente(ingrediente: com.fjrh.FabrikApp.data.local.entity.IngredienteInventarioEntity) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                // Validaciones básicas
                if (ingrediente.nombre.trim().isBlank()) {
                    _errorMessage.value = "El nombre del ingrediente no puede estar vacío"
                    return@launch
                }
                
                if (ingrediente.cantidadDisponible < 0) {
                    _errorMessage.value = "La cantidad no puede ser negativa"
                    return@launch
                }
                
                if (ingrediente.costoPorUnidad < 0) {
                    _errorMessage.value = "El costo no puede ser negativo"
                    return@launch
                }

                // 1. Actualizar el ingrediente en inventario
                repository.actualizarIngrediente(ingrediente)
                
                // 2. Actualizar costos en todas las fórmulas que usen este ingrediente
                actualizarCostosEnFormulas(ingrediente.nombre, ingrediente.costoPorUnidad)
                
                _successMessage.value = "Ingrediente actualizado correctamente"
            } catch (e: Exception) {
                _errorMessage.value = "Error al actualizar ingrediente: ${e.message}"
            } finally {
                _isLoading.value = false
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
            _errorMessage.value = "Error al actualizar costos en fórmulas: ${e.message}"
        }
    }

    fun agregarIngrediente(ingrediente: com.fjrh.FabrikApp.data.local.entity.IngredienteInventarioEntity) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                // Validaciones básicas
                if (ingrediente.nombre.trim().isBlank()) {
                    _errorMessage.value = "El nombre del ingrediente no puede estar vacío"
                    return@launch
                }
                
                if (ingrediente.cantidadDisponible < 0) {
                    _errorMessage.value = "La cantidad no puede ser negativa"
                    return@launch
                }
                
                if (ingrediente.costoPorUnidad < 0) {
                    _errorMessage.value = "El costo no puede ser negativo"
                    return@launch
                }
                
                // Verificar si ya existe un ingrediente con el mismo nombre
                val ingredientesExistentes = repository.getIngredientesInventario().first()
                if (ingredientesExistentes.any { it.nombre.trim().equals(ingrediente.nombre.trim(), ignoreCase = true) }) {
                    _errorMessage.value = "Ya existe un ingrediente con ese nombre"
                    return@launch
                }
                
                repository.insertarIngrediente(ingrediente)
                _successMessage.value = "Ingrediente agregado correctamente"
            } catch (e: Exception) {
                _errorMessage.value = "Error al agregar ingrediente: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
