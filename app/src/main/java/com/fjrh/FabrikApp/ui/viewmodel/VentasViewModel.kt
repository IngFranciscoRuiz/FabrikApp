package com.fjrh.FabrikApp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fjrh.FabrikApp.data.local.entity.VentaEntity
import com.fjrh.FabrikApp.data.local.entity.BalanceEntity
import com.fjrh.FabrikApp.data.local.repository.FormulaRepository
import com.fjrh.FabrikApp.domain.model.StockProducto
import com.fjrh.FabrikApp.data.local.dao.StockProductoQuery
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VentasViewModel @Inject constructor(
    private val repository: FormulaRepository,
    private val syncManager: com.fjrh.FabrikApp.domain.usecase.SyncManager
) : ViewModel() {

    val ventas: StateFlow<List<VentaEntity>> =
        repository.getVentas()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val stockProductos: StateFlow<List<StockProducto>> =
        repository.getStockProductos()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun agregarVenta(venta: VentaEntity) {
        viewModelScope.launch {
            // 1. Verificar stock disponible
            val stockActual = repository.getStockProductosSync()
            val productoEnStock = stockActual.find { it.nombre == venta.nombreProducto }
            
            if (productoEnStock != null && productoEnStock.stock >= venta.litrosVendidos) {
                // 2. Insertar la venta
                repository.insertarVenta(venta)
                
                // 3. Sincronizar automáticamente con Firebase
                try {
                    syncManager.syncNewVenta(venta)
                } catch (e: Exception) {
                    println("Error en sincronización automática: ${e.message}")
                }
                
                // 4. Registrar ingreso en balance
                val ingreso = BalanceEntity(
                    fecha = System.currentTimeMillis(),
                    tipo = "INGRESO",
                    concepto = "Venta de ${venta.nombreProducto}",
                    monto = venta.litrosVendidos * venta.precioPorLitro,
                    descripcion = "Venta de ${venta.litrosVendidos}L de ${venta.nombreProducto} a $${venta.precioPorLitro}/L"
                )
                repository.insertarBalance(ingreso)
            } else {
                // TODO: Mostrar error de stock insuficiente
                throw Exception("Stock insuficiente para ${venta.nombreProducto}")
            }
        }
    }

    fun obtenerVentasPorProducto(nombreProducto: String) {
        viewModelScope.launch {
            repository.getLitrosVendidosPorProducto(nombreProducto)
        }
    }

    fun eliminarVenta(venta: VentaEntity) {
        viewModelScope.launch {
            try {
                // 1. Eliminar la venta
                repository.eliminarVenta(venta)
                
                // 2. Registrar egreso en balance (restar el ingreso)
                val egreso = BalanceEntity(
                    fecha = System.currentTimeMillis(),
                    tipo = "EGRESO",
                    concepto = "Eliminación de venta de ${venta.nombreProducto}",
                    monto = venta.litrosVendidos * venta.precioPorLitro,
                    descripcion = "Eliminación de venta de ${venta.litrosVendidos}L de ${venta.nombreProducto} a $${venta.precioPorLitro}/L"
                )
                repository.insertarBalance(egreso)
            } catch (e: Exception) {
                // TODO: Manejar error
                throw e
            }
        }
    }
} 
