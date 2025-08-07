package com.fjrh.laxcleanfactory.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fjrh.laxcleanfactory.data.local.repository.FormulaRepository
import com.fjrh.laxcleanfactory.data.local.ConfiguracionDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainMenuViewModel @Inject constructor(
    private val repository: FormulaRepository,
    private val configuracionDataStore: ConfiguracionDataStore
) : ViewModel() {

    private val _ventasHoy = MutableStateFlow(0.0)
    val ventasHoy: StateFlow<Double> = _ventasHoy.asStateFlow()

    private val _stockBajo = MutableStateFlow(0)
    val stockBajo: StateFlow<Int> = _stockBajo.asStateFlow()

    private val _produccionHoy = MutableStateFlow(0.0)
    val produccionHoy: StateFlow<Double> = _produccionHoy.asStateFlow()

    init {
        cargarMetricas()
    }

    private fun cargarMetricas() {
        viewModelScope.launch {
            // Cargar ventas del día actual
            val hoy = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            
            val manana = hoy + (24 * 60 * 60 * 1000)
            
            repository.getVentas().collect { ventas ->
                val ventasHoy = ventas.filter { venta ->
                    venta.fecha >= hoy && venta.fecha < manana
                }.sumOf { it.litrosVendidos * it.precioPorLitro }
                _ventasHoy.value = ventasHoy
            }
        }

        viewModelScope.launch {
            // Cargar stock bajo usando configuración real
            combine(
                repository.getStockProductos(),
                configuracionDataStore.configuracion
            ) { stockProductos, config ->
                val stockBajo = stockProductos.count { it.stock < config.stockBajoProductos }
                _stockBajo.value = stockBajo
            }.collect()
        }

        viewModelScope.launch {
            // Cargar producción del día actual
            val hoy = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            
            val manana = hoy + (24 * 60 * 60 * 1000)
            
            repository.getHistorial().collect { historial ->
                val produccionHoy = historial.filter { produccion ->
                    produccion.fecha >= hoy && produccion.fecha < manana
                }.sumOf { it.litrosProducidos.toDouble() }
                _produccionHoy.value = produccionHoy
            }
        }
    }

    fun formatearVentasHoy(): String {
        return "$${String.format("%,.0f", _ventasHoy.value)}"
    }

    fun formatearStockBajo(): String {
        return "${_stockBajo.value} items"
    }

    fun formatearProduccionHoy(): String {
        return "${String.format("%.0f", _produccionHoy.value)} L"
    }
} 