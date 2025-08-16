package com.fjrh.FabrikApp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fjrh.FabrikApp.data.local.entity.BalanceEntity
import com.fjrh.FabrikApp.data.local.repository.FormulaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BalanceViewModel @Inject constructor(
    private val repository: FormulaRepository,
    private val syncManager: com.fjrh.FabrikApp.domain.usecase.SyncManager
) : ViewModel() {

    private val _balance = repository.getBalance()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val balance: StateFlow<List<BalanceEntity>> = _balance

    val totalIngresos: StateFlow<Double> = _balance
        .map { balanceList ->
            balanceList.filter { it.tipo == "INGRESO" }.sumOf { it.monto }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalEgresos: StateFlow<Double> = _balance
        .map { balanceList ->
            balanceList.filter { it.tipo == "EGRESO" }.sumOf { it.monto }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun agregarMovimiento(movimiento: BalanceEntity) {
        viewModelScope.launch {
            repository.insertarBalance(movimiento)
            
            // Sincronizar automáticamente con Firebase
            try {
                syncManager.syncNewBalance(movimiento)
            } catch (e: Exception) {
                println("Error en sincronización automática de balance: ${e.message}")
            }
        }
    }

    fun calcularUtilidad(): Double {
        return totalIngresos.value - totalEgresos.value
    }
} 
