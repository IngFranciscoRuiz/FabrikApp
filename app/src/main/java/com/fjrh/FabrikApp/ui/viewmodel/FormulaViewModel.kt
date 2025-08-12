package com.fjrh.FabrikApp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fjrh.FabrikApp.data.local.entity.FormulaConIngredientes
import com.fjrh.FabrikApp.data.local.entity.FormulaEntity
import com.fjrh.FabrikApp.data.local.entity.IngredienteEntity
import com.fjrh.FabrikApp.data.local.entity.IngredienteInventarioEntity
import com.fjrh.FabrikApp.data.local.entity.HistorialProduccionEntity
import com.fjrh.FabrikApp.data.local.repository.FormulaRepository
import com.fjrh.FabrikApp.data.local.dao.StockProductoQuery
import com.fjrh.FabrikApp.domain.model.Formula
import com.fjrh.FabrikApp.domain.model.Ingrediente
import com.fjrh.FabrikApp.domain.model.StockProducto
import com.fjrh.FabrikApp.domain.usecase.AgregarFormulaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import com.fjrh.FabrikApp.domain.util.UnitConversionUtil


@HiltViewModel
class FormulaViewModel @Inject constructor(
    private val repository: FormulaRepository,
    private val agregarFormulaUseCase: AgregarFormulaUseCase
) : ViewModel() {

    val formulas: StateFlow<List<FormulaConIngredientes>> =
        repository.obtenerFormulasConIngredientes()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val stockProductos: StateFlow<List<StockProducto>> =
        repository.getStockProductos()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val ingredientesInventario: StateFlow<List<IngredienteInventarioEntity>> =
        repository.getIngredientesInventario()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val historial: StateFlow<List<HistorialProduccionEntity>> =
        repository.getHistorial()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent

    fun guardarFormula(formula: Formula) {
        viewModelScope.launch {
            agregarFormulaUseCase(formula)
            _uiEvent.emit(UiEvent.FormulaGuardada)
        }
    }

    fun insertarHistorial(historial: HistorialProduccionEntity) {
        viewModelScope.launch {
            repository.insertarHistorial(historial)
            _uiEvent.emit(UiEvent.LoteProducido)
        }
    }

    fun eliminarFormula(formula: FormulaEntity) {
        viewModelScope.launch {
            repository.eliminarFormulaConIngredientes(formula)
        }
    }

    fun actualizarFormula(formulaId: Long, nombre: String, ingredientes: List<Ingrediente>) {
        viewModelScope.launch {
            try {
                // 1. Actualizar la fórmula existente
                val formulaEntity = FormulaEntity(id = formulaId, nombre = nombre)
                repository.actualizarFormula(formulaEntity)
                
                // 2. Eliminar ingredientes existentes
                repository.eliminarIngredientesByFormulaId(formulaId)
                
                // 3. Insertar los nuevos ingredientes
                val ingredientesEntities = ingredientes.map { ingrediente ->
                    IngredienteEntity(
                        formulaId = formulaId,
                        nombre = ingrediente.nombre,
                        unidad = ingrediente.unidad,
                        cantidad = ingrediente.cantidad,
                        costoPorUnidad = ingrediente.costoPorUnidad
                    )
                }
                
                repository.insertarIngredientes(ingredientesEntities)
                
                _uiEvent.emit(UiEvent.FormulaGuardada)
            } catch (e: Exception) {
                // TODO: Manejar error
                e.printStackTrace()
            }
        }
    }

    fun actualizarIngredienteInventario(ingrediente: IngredienteInventarioEntity) {
        viewModelScope.launch {
            try {
                // 1. Actualizar el ingrediente en inventario
                repository.actualizarIngredienteInventario(ingrediente)
                
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
            val formulas = repository.obtenerFormulasConIngredientes().first()
            
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
                    repository.eliminarIngredientesByFormulaId(formulaConIngredientes.formula.id)
                    
                    // Insertar ingredientes actualizados
                    val ingredientesEntities = ingredientesActualizados.map { ingrediente ->
                        IngredienteEntity(
                            formulaId = formulaConIngredientes.formula.id,
                            nombre = ingrediente.nombre,
                            unidad = ingrediente.unidad,
                            cantidad = ingrediente.cantidad,
                            costoPorUnidad = ingrediente.costoPorUnidad
                        )
                    }
                    
                    repository.insertarIngredientes(ingredientesEntities)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    sealed class UiEvent {
        object FormulaGuardada : UiEvent()
        object LoteProducido : UiEvent()
    }
}
