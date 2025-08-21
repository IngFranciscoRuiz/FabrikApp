package com.fjrh.FabrikApp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fjrh.FabrikApp.data.local.SAFService
import com.fjrh.FabrikApp.data.local.entity.*
import com.fjrh.FabrikApp.data.local.repository.FormulaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val repository: FormulaRepository,
    private val safService: SAFService
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<BackupUiState>(BackupUiState.Idle)
    val uiState: StateFlow<BackupUiState> = _uiState.asStateFlow()
    
    private val _backupInfo = MutableStateFlow<SAFService.BackupInfo?>(null)
    val backupInfo: StateFlow<SAFService.BackupInfo?> = _backupInfo.asStateFlow()
    
    /**
     * Exporta todos los datos de la app
     */
    fun exportBackup(uri: android.net.Uri) {
        viewModelScope.launch {
            _uiState.value = BackupUiState.Loading("Preparando respaldo...")
            
            try {
                // Recopilar todos los datos
                val ingredientes = repository.getIngredientesInventario().first()
                val formulas = repository.obtenerFormulasConIngredientes().first()
                val ventas = repository.getVentas().first()
                val balance = repository.getBalance().first()
                val notas = repository.getNotas().first()
                val pedidos = repository.getPedidosProveedor().first()
                val historial = repository.getHistorial().first()
                val unidades = repository.getUnidadesMedida().first()
                
                val backupData = SAFService.BackupData(
                    ingredientes = ingredientes,
                    formulas = formulas,
                    ventas = ventas,
                    balance = balance,
                    notas = notas,
                    pedidos = pedidos,
                    historial = historial,
                    unidades = unidades
                )
                
                _uiState.value = BackupUiState.Loading("Exportando datos...")
                
                val result = safService.exportBackup(uri, backupData)
                result.fold(
                    onSuccess = { message ->
                        _uiState.value = BackupUiState.Success(message)
                    },
                    onFailure = { exception ->
                        _uiState.value = BackupUiState.Error("Error al exportar: ${exception.message}")
                    }
                )
                
            } catch (e: Exception) {
                _uiState.value = BackupUiState.Error("Error al preparar el respaldo: ${e.message}")
            }
        }
    }
    
    /**
     * Importa datos desde un archivo de respaldo
     */
    fun importBackup(uri: android.net.Uri) {
        viewModelScope.launch {
            _uiState.value = BackupUiState.Loading("Validando archivo...")
            
            try {
                // Validar el archivo
                val isValid = safService.validateBackupFile(uri)
                isValid.fold(
                    onSuccess = { valid ->
                        if (!valid) {
                            _uiState.value = BackupUiState.Error("El archivo no es un respaldo válido de FabrikApp")
                            return@launch
                        }
                    },
                    onFailure = { exception ->
                        _uiState.value = BackupUiState.Error("Error al validar el archivo: ${exception.message}")
                        return@launch
                    }
                )
                
                _uiState.value = BackupUiState.Loading("Importando datos...")
                
                val result = safService.importBackup(uri)
                result.fold(
                    onSuccess = { backupData ->
                        // Importar los datos a la base de datos
                        importBackupData(backupData)
                    },
                    onFailure = { exception ->
                        _uiState.value = BackupUiState.Error("Error al importar: ${exception.message}")
                    }
                )
                
            } catch (e: Exception) {
                _uiState.value = BackupUiState.Error("Error al procesar el respaldo: ${e.message}")
            }
        }
    }
    
    /**
     * Importa los datos del respaldo a la base de datos
     */
    private suspend fun importBackupData(backupData: SAFService.BackupData) {
        try {
            _uiState.value = BackupUiState.Loading("Restaurando ingredientes...")
            backupData.ingredientes.forEach { ingrediente ->
                repository.actualizarIngredienteInventario(ingrediente)
            }
            
            _uiState.value = BackupUiState.Loading("Restaurando fórmulas...")
            backupData.formulas.forEach { formula ->
                repository.insertarFormulaConIngredientes(formula.formula, formula.ingredientes)
            }
            
            _uiState.value = BackupUiState.Loading("Restaurando ventas...")
            backupData.ventas.forEach { venta ->
                repository.insertarVenta(venta)
            }
            
            _uiState.value = BackupUiState.Loading("Restaurando balance...")
            backupData.balance.forEach { balance ->
                repository.insertarBalance(balance)
            }
            
            _uiState.value = BackupUiState.Loading("Restaurando notas...")
            backupData.notas.forEach { nota ->
                repository.insertarNota(nota)
            }
            
            _uiState.value = BackupUiState.Loading("Restaurando pedidos...")
            backupData.pedidos.forEach { pedido ->
                repository.insertarPedidoProveedor(pedido)
            }
            
            _uiState.value = BackupUiState.Loading("Restaurando historial...")
            backupData.historial.forEach { historial ->
                repository.insertarHistorial(historial)
            }
            
            _uiState.value = BackupUiState.Loading("Restaurando unidades...")
            backupData.unidades.forEach { unidad ->
                repository.insertarUnidadMedida(unidad)
            }
            
            _uiState.value = BackupUiState.Success("Respaldo importado exitosamente")
            
        } catch (e: Exception) {
            _uiState.value = BackupUiState.Error("Error al restaurar datos: ${e.message}")
        }
    }
    
    /**
     * Obtiene información del archivo de respaldo
     */
    fun getBackupInfo(uri: android.net.Uri) {
        viewModelScope.launch {
            try {
                val result = safService.getBackupInfo(uri)
                result.fold(
                    onSuccess = { info ->
                        _backupInfo.value = info
                    },
                    onFailure = { exception ->
                        _uiState.value = BackupUiState.Error("Error al obtener información: ${exception.message}")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = BackupUiState.Error("Error al procesar archivo: ${e.message}")
            }
        }
    }
    
    /**
     * Limpia el estado de la UI
     */
    fun clearState() {
        _uiState.value = BackupUiState.Idle
        _backupInfo.value = null
    }
}

sealed class BackupUiState {
    object Idle : BackupUiState()
    data class Loading(val message: String) : BackupUiState()
    data class Success(val message: String) : BackupUiState()
    data class Error(val message: String) : BackupUiState()
}
