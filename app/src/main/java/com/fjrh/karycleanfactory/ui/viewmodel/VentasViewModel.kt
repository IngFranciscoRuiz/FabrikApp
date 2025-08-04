package com.fjrh.karycleanfactory.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fjrh.karycleanfactory.data.local.entity.VentaEntity
import com.fjrh.karycleanfactory.data.local.entity.BalanceEntity
import com.fjrh.karycleanfactory.data.local.repository.FormulaRepository
import com.fjrh.karycleanfactory.domain.model.StockProducto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VentasViewModel @Inject constructor(
    private val repository: FormulaRepository
) : ViewModel() {

    val ventas: StateFlow<List<VentaEntity>> =
        repository.getVentas()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val stockProductos: StateFlow<List<StockProducto>> =
        repository.getStockProductos().map { list ->
            list.map { StockProducto(nombre = it.nombre, stock = it.stock) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun agregarVenta(venta: VentaEntity) {
        viewModelScope.launch {
            // 1. Insertar la venta
            repository.insertarVenta(venta)
            
            // 2. Descontar del stock de productos
            val stockActual = repository.getStockProductosSync()
            val productoEnStock = stockActual.find { it.nombre == venta.nombreProducto }
            
            if (productoEnStock != null && productoEnStock.stock >= venta.litrosVendidos) {
                // Actualizar stock (esto requeriría un método en el repository)
                // repository.actualizarStockProducto(venta.nombreProducto, productoEnStock.stock - venta.litrosVendidos)
            }
            
            // 3. Registrar ingreso en balance
            val ingreso = BalanceEntity(
                fecha = System.currentTimeMillis(),
                tipo = "INGRESO",
                concepto = "Venta de ${venta.nombreProducto}",
                monto = venta.litrosVendidos * venta.precioPorLitro,
                descripcion = "Venta de ${venta.litrosVendidos}L de ${venta.nombreProducto} a $${venta.precioPorLitro}/L"
            )
            repository.insertarBalance(ingreso)
        }
    }

    fun obtenerVentasPorProducto(nombreProducto: String) {
        viewModelScope.launch {
            repository.getLitrosVendidosPorProducto(nombreProducto)
        }
    }
} 