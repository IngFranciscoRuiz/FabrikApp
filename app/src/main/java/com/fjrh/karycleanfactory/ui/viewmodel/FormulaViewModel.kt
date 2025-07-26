package com.fjrh.karycleanfactory.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fjrh.karycleanfactory.data.local.entity.FormulaConIngredientes
import com.fjrh.karycleanfactory.data.local.entity.FormulaEntity
import com.fjrh.karycleanfactory.data.local.entity.IngredienteEntity
import com.fjrh.karycleanfactory.data.local.entity.IngredienteInventarioEntity
import com.fjrh.karycleanfactory.data.local.entity.HistorialProduccionEntity
import com.fjrh.karycleanfactory.data.local.repository.FormulaRepository
import com.fjrh.karycleanfactory.domain.model.Formula
import com.fjrh.karycleanfactory.domain.model.Ingrediente
import com.fjrh.karycleanfactory.domain.usecase.AgregarFormulaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// Definición local para evitar errores de símbolo no encontrado
data class StockProducto(
    val nombre: String,
    val stock: Float
)

@HiltViewModel
class FormulaViewModel @Inject constructor(
    private val repository: FormulaRepository,
    private val agregarFormulaUseCase: AgregarFormulaUseCase
) : ViewModel() {

    val formulas: StateFlow<List<FormulaConIngredientes>> =
        repository.obtenerFormulasConIngredientes()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val stockProductos: StateFlow<List<StockProducto>> =
        repository.getStockProductos().map { list ->
            list.map { StockProducto(nombre = it.nombre, stock = it.stock) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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
            // Primero eliminar ingredientes existentes
            repository.eliminarIngredientesByFormulaId(formulaId)
            
            // Luego insertar los nuevos ingredientes
            val formulaEntity = FormulaEntity(id = formulaId, nombre = nombre)
            val ingredientesEntities = ingredientes.map { ingrediente ->
                IngredienteEntity(
                    formulaId = formulaId,
                    nombre = ingrediente.nombre,
                    unidad = ingrediente.unidad,
                    cantidad = ingrediente.cantidad,
                    costoPorUnidad = ingrediente.costoPorUnidad
                )
            }
            
            repository.insertarFormulaConIngredientes(formulaEntity, ingredientesEntities)
            _uiEvent.emit(UiEvent.FormulaGuardada)
        }
    }

    fun actualizarIngredienteInventario(ingrediente: IngredienteInventarioEntity) {
        viewModelScope.launch {
            repository.actualizarIngredienteInventario(ingrediente)
        }
    }

    sealed class UiEvent {
        object FormulaGuardada : UiEvent()
        object LoteProducido : UiEvent()
    }
}
