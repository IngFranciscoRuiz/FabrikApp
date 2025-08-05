package com.fjrh.karycleanfactory.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fjrh.karycleanfactory.data.local.entity.PedidoProveedorEntity
import com.fjrh.karycleanfactory.data.local.entity.BalanceEntity
import com.fjrh.karycleanfactory.data.local.repository.FormulaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PedidosProveedorViewModel @Inject constructor(
    private val repository: FormulaRepository
) : ViewModel() {

    val pedidos: StateFlow<List<PedidoProveedorEntity>> =
        repository.getPedidosProveedor()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun agregarPedido(pedido: PedidoProveedorEntity) {
        viewModelScope.launch {
            // 1. Insertar el pedido
            repository.insertarPedidoProveedor(pedido)
            
            // 2. Si el estado es PAGADO, registrar como egreso en balance
            if (pedido.estado == "PAGADO") {
                val egreso = BalanceEntity(
                    fecha = System.currentTimeMillis(),
                    tipo = "EGRESO",
                    concepto = "Pago a proveedor: ${pedido.nombreProveedor}",
                    monto = pedido.monto,
                    descripcion = "Pago de pedido: ${pedido.productos}"
                )
                repository.insertarBalance(egreso)
            }
        }
    }

    fun actualizarPedido(pedido: PedidoProveedorEntity) {
        viewModelScope.launch {
            // Obtener el pedido anterior para verificar si cambió el estado
            val pedidosActuales = repository.getPedidosProveedor().first()
            val pedidoAnterior = pedidosActuales.find { it.id == pedido.id }
            
            // Actualizar el pedido
            repository.actualizarPedidoProveedor(pedido)
            
            // Si el estado cambió de PENDIENTE a PAGADO, registrar egreso
            if (pedidoAnterior?.estado == "PENDIENTE" && pedido.estado == "PAGADO") {
                val egreso = BalanceEntity(
                    fecha = System.currentTimeMillis(),
                    tipo = "EGRESO",
                    concepto = "Pago a proveedor: ${pedido.nombreProveedor}",
                    monto = pedido.monto,
                    descripcion = "Pago de pedido: ${pedido.productos}"
                )
                repository.insertarBalance(egreso)
            }
        }
    }

    fun eliminarPedido(pedido: PedidoProveedorEntity) {
        viewModelScope.launch {
            repository.eliminarPedidoProveedor(pedido)
        }
    }
} 