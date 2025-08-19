package com.fjrh.FabrikApp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fjrh.FabrikApp.data.local.repository.FormulaRepository
import com.fjrh.FabrikApp.data.local.ConfiguracionDataStore
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

    private val _pedidosPendientes = MutableStateFlow(0)
    val pedidosPendientes: StateFlow<Int> = _pedidosPendientes.asStateFlow()

    init {
        cargarMetricas()
    }

    private fun cargarMetricas() {
        viewModelScope.launch {
            // Combinar todos los Flows en una sola coroutine para evitar cancelaciones
            combine(
                repository.getVentas(),
                repository.getStockProductos(),
                repository.getHistorial(),
                repository.getPedidosProveedor(),
                configuracionDataStore.configuracion
            ) { ventas, stockProductos, historial, pedidos, config ->
                // Calcular fecha de hoy
                val hoy = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                val manana = hoy + (24 * 60 * 60 * 1000)
                
                // Calcular ventas del día
                val ventasHoy = ventas.filter { venta ->
                    venta.fecha >= hoy && venta.fecha < manana
                }.sumOf { it.litrosVendidos * it.precioPorLitro }
                
                // Calcular stock bajo
                val stockBajo = stockProductos.count { it.stock < config.stockBajoProductos }
                
                // Calcular producción del día
                val produccionHoy = historial.filter { produccion ->
                    produccion.fecha >= hoy && produccion.fecha < manana
                }.sumOf { it.litrosProducidos.toDouble() }
                
                // Calcular pedidos pendientes
                val pedidosPendientes = pedidos.count { it.estado == "PENDIENTE" }
                
                // Actualizar todos los valores de una vez
                _ventasHoy.value = ventasHoy
                _stockBajo.value = stockBajo
                _produccionHoy.value = produccionHoy
                _pedidosPendientes.value = pedidosPendientes
            }.collect()
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

    fun formatearPedidosPendientes(): String {
        return "${_pedidosPendientes.value}"
    }
} 