package com.fjrh.FabrikApp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fjrh.FabrikApp.data.remote.FirebaseService
import com.fjrh.FabrikApp.data.firebase.WorkspaceService
import com.fjrh.FabrikApp.data.firebase.WorkspaceHolder
import com.fjrh.FabrikApp.domain.result.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseService: FirebaseService
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                _successMessage.value = null

                println("LoginViewModel: Iniciando autenticación para $email")
                
                // Ejecutar Firebase en hilo de IO
                val result = withContext(Dispatchers.IO) {
                    firebaseService.signInWithEmail(email, password)
                }
                
                when (result) {
                    is Result.Success -> {
                        println("LoginViewModel: Autenticación exitosa")
                        _successMessage.value = "¡Bienvenido de vuelta!"
                        
                        // Después del login exitoso, sincronizar datos automáticamente
                        try {
                            println("LoginViewModel: Sincronizando datos después del login")
                            syncUserData()
                        } catch (e: Exception) {
                            println("LoginViewModel: Error en sincronización post-login: ${e.message}")
                        }
                    }
                    is Result.Error -> {
                        println("LoginViewModel: Error de autenticación: ${result.exception.message}")
                        _errorMessage.value = result.exception.message
                    }
                    is Result.Loading -> {
                        println("LoginViewModel: Estado de carga inesperado")
                        // No debería ocurrir aquí
                    }
                }
            } catch (e: Exception) {
                println("LoginViewModel: Excepción no manejada: ${e.message}")
                e.printStackTrace()
                _errorMessage.value = "Error inesperado: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                _successMessage.value = null

                println("LoginViewModel: Iniciando registro para $email")
                
                // Ejecutar Firebase en hilo de IO
                val result = withContext(Dispatchers.IO) {
                    firebaseService.signUpWithEmail(email, password)
                }
                
                when (result) {
                    is Result.Success -> {
                        println("LoginViewModel: Registro exitoso")
                        _successMessage.value = "¡Cuenta creada exitosamente!"
                        
                        // Después del registro exitoso, también sincronizar datos
                        try {
                            println("LoginViewModel: Sincronizando datos después del registro")
                            syncUserData()
                        } catch (e: Exception) {
                            println("LoginViewModel: Error en sincronización post-registro: ${e.message}")
                        }
                    }
                    is Result.Error -> {
                        println("LoginViewModel: Error de registro: ${result.exception.message}")
                        _errorMessage.value = result.exception.message
                    }
                    is Result.Loading -> {
                        println("LoginViewModel: Estado de carga inesperado")
                        // No debería ocurrir aquí
                    }
                }
            } catch (e: Exception) {
                println("LoginViewModel: Excepción no manejada en registro: ${e.message}")
                e.printStackTrace()
                _errorMessage.value = "Error inesperado: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearSuccess() {
        _successMessage.value = null
    }
    
    fun bootstrapWorkspace() {
        viewModelScope.launch {
            try {
                println("LoginViewModel: Iniciando bootstrap del workspace")
                val wid = withContext(Dispatchers.IO) {
                    WorkspaceService.bootstrapWorkspaceForCurrentUser()
                }
                WorkspaceHolder.set(wid)
                println("LoginViewModel: Workspace bootstrap completado: $wid")
            } catch (e: Exception) {
                println("LoginViewModel: Error en bootstrap del workspace: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    
    fun syncUserData() {
        viewModelScope.launch {
            try {
                println("LoginViewModel: Iniciando sincronización automática de datos compartidos")
                
                // Ejecutar sincronización en hilo de IO
                withContext(Dispatchers.IO) {
                    // Descargar TODOS los datos compartidos desde Firebase
                    val ingredientesResult = firebaseService.downloadIngredientes()
                    val formulasResult = firebaseService.downloadFormulas()
                    val ventasResult = firebaseService.downloadVentas()
                    val ingredientesInventarioResult = firebaseService.downloadIngredientesInventario()
                    val historialResult = firebaseService.downloadHistorial()
                    val balanceResult = firebaseService.downloadBalance()
                    val notasResult = firebaseService.downloadNotas()
                    val pedidosResult = firebaseService.downloadPedidosProveedor()
                    val unidadesResult = firebaseService.downloadUnidadesMedida()
                    
                    // Procesar resultados y mostrar logs
                    println("LoginViewModel: 🔍 Procesando resultados de sincronización...")
                    
                    when (ingredientesResult) {
                        is Result.Success -> {
                            println("LoginViewModel: ✅ Descargados ${ingredientesResult.data.size} ingredientes")
                            ingredientesResult.data.forEach { ingrediente ->
                                println("LoginViewModel: 📦 Ingrediente: ${ingrediente.nombre} (ID: ${ingrediente.id})")
                            }
                        }
                        is Result.Error -> {
                            println("LoginViewModel: ❌ Error descargando ingredientes: ${ingredientesResult.exception.message}")
                            ingredientesResult.exception.printStackTrace()
                        }
                        is Result.Loading -> println("LoginViewModel: ⏳ Descargando ingredientes...")
                    }
                    
                    when (formulasResult) {
                        is Result.Success -> {
                            println("LoginViewModel: ✅ Descargadas ${formulasResult.data.size} fórmulas")
                            formulasResult.data.forEach { formula ->
                                println("LoginViewModel: 📦 Fórmula: ${formula.nombre} (ID: ${formula.id})")
                            }
                        }
                        is Result.Error -> {
                            println("LoginViewModel: ❌ Error descargando fórmulas: ${formulasResult.exception.message}")
                            formulasResult.exception.printStackTrace()
                        }
                        is Result.Loading -> println("LoginViewModel: ⏳ Descargando fórmulas...")
                    }
                    
                    when (ventasResult) {
                        is Result.Success -> println("LoginViewModel: ✅ Descargadas ${ventasResult.data.size} ventas")
                        is Result.Error -> println("LoginViewModel: ❌ Error descargando ventas: ${ventasResult.exception.message}")
                        is Result.Loading -> println("LoginViewModel: ⏳ Descargando ventas...")
                    }
                    
                    when (ingredientesInventarioResult) {
                        is Result.Success -> println("LoginViewModel: ✅ Descargados ${ingredientesInventarioResult.data.size} ingredientes de inventario")
                        is Result.Error -> println("LoginViewModel: ❌ Error descargando ingredientes de inventario: ${ingredientesInventarioResult.exception.message}")
                        is Result.Loading -> println("LoginViewModel: ⏳ Descargando ingredientes de inventario...")
                    }
                    
                    when (historialResult) {
                        is Result.Success -> println("LoginViewModel: ✅ Descargado ${historialResult.data.size} historial")
                        is Result.Error -> println("LoginViewModel: ❌ Error descargando historial: ${historialResult.exception.message}")
                        is Result.Loading -> println("LoginViewModel: ⏳ Descargando historial...")
                    }
                    
                    when (balanceResult) {
                        is Result.Success -> println("LoginViewModel: ✅ Descargado ${balanceResult.data.size} balance")
                        is Result.Error -> println("LoginViewModel: ❌ Error descargando balance: ${balanceResult.exception.message}")
                        is Result.Loading -> println("LoginViewModel: ⏳ Descargando balance...")
                    }
                    
                    when (notasResult) {
                        is Result.Success -> println("LoginViewModel: ✅ Descargadas ${notasResult.data.size} notas")
                        is Result.Error -> println("LoginViewModel: ❌ Error descargando notas: ${notasResult.exception.message}")
                        is Result.Loading -> println("LoginViewModel: ⏳ Descargando notas...")
                    }
                    
                    when (pedidosResult) {
                        is Result.Success -> println("LoginViewModel: ✅ Descargados ${pedidosResult.data.size} pedidos")
                        is Result.Error -> println("LoginViewModel: ❌ Error descargando pedidos: ${pedidosResult.exception.message}")
                        is Result.Loading -> println("LoginViewModel: ⏳ Descargando pedidos...")
                    }
                    
                    when (unidadesResult) {
                        is Result.Success -> println("LoginViewModel: ✅ Descargadas ${unidadesResult.data.size} unidades de medida")
                        is Result.Error -> println("LoginViewModel: ❌ Error descargando unidades de medida: ${unidadesResult.exception.message}")
                        is Result.Loading -> println("LoginViewModel: ⏳ Descargando unidades de medida...")
                    }
                    
                    println("LoginViewModel: ✅ Sincronización automática completada - Todos los datos compartidos descargados")
                    
                    // TODO: Guardar en la base de datos local
                    // Por ahora solo log para debugging
                }
            } catch (e: Exception) {
                println("LoginViewModel: ❌ Error en sincronización automática: ${e.message}")
            }
        }
    }
}
