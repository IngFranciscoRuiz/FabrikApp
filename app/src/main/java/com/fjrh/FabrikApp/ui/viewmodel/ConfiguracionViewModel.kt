package com.fjrh.FabrikApp.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fjrh.FabrikApp.data.local.ConfiguracionDataStore
import com.fjrh.FabrikApp.data.local.repository.FormulaRepository
import com.fjrh.FabrikApp.data.local.repository.InventarioRepository
import com.fjrh.FabrikApp.domain.model.ConfiguracionStock
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ConfiguracionViewModel @Inject constructor(
    private val configuracionDataStore: ConfiguracionDataStore,
    private val formulaRepository: FormulaRepository,
    private val inventarioRepository: InventarioRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _archivosBackup = MutableStateFlow<List<File>>(emptyList())
    val archivosBackup: StateFlow<List<File>> = _archivosBackup

    val configuracion: StateFlow<ConfiguracionStock> = configuracionDataStore.configuracion.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ConfiguracionStock()
    )

    init {
        cargarArchivosBackup()
    }

    private fun cargarArchivosBackup() {
        viewModelScope.launch {
            try {
                val backupDir = context.getExternalFilesDir(null)
                val archivos = backupDir?.listFiles { file ->
                    file.name.startsWith("fabrikapp_backup_") && file.name.endsWith(".json")
                }?.sortedByDescending { it.lastModified() } ?: emptyList()
                _archivosBackup.value = archivos
            } catch (e: Exception) {
                // Ignorar errores al cargar archivos
            }
        }
    }

    fun guardarConfiguracion(config: ConfiguracionStock) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
            configuracionDataStore.guardarConfiguracion(config)
                _mensaje.value = "Configuración guardada exitosamente"
            } catch (e: Exception) {
                _mensaje.value = "Error al guardar configuración: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetearConfiguracion() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                configuracionDataStore.resetearConfiguracion()
                _mensaje.value = "Configuración restablecida exitosamente"
            } catch (e: Exception) {
                _mensaje.value = "Error al restablecer configuración: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun exportarDatos() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                // Recopilar todos los datos
                val configuracion = configuracionDataStore.configuracion.first()
                val formulas = formulaRepository.obtenerFormulasConIngredientes().first()
                val ingredientes = inventarioRepository.getIngredientesInventario().first()
                val stockProductos = formulaRepository.getStockProductos().first()
                val ventas = formulaRepository.getVentas().first()
                val historial = formulaRepository.getHistorial().first()
                val balance = formulaRepository.getBalance().first()
                val pedidosProveedor = formulaRepository.getPedidosProveedor().first()
                
                // Crear objeto de datos completo
                val datosCompletos = mapOf(
                    "configuracion" to configuracion,
                    "formulas" to formulas,
                    "ingredientes" to ingredientes,
                    "stockProductos" to stockProductos,
                    "ventas" to ventas,
                    "historial" to historial,
                    "balance" to balance,
                    "pedidosProveedor" to pedidosProveedor,
                    "fechaExportacion" to SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
                    "version" to "1.0"
                )
                
                // Convertir a JSON
                val gson = Gson()
                val jsonData = gson.toJson(datosCompletos)
                
                // Guardar archivo
                val fileName = "fabrikapp_backup_${System.currentTimeMillis()}.json"
                val file = File(context.getExternalFilesDir(null), fileName)
                file.writeText(jsonData)
                
                // Recargar lista de archivos
                cargarArchivosBackup()
                
                _mensaje.value = "Datos exportados exitosamente a: ${file.name}"
            } catch (e: Exception) {
                _mensaje.value = "Error al exportar datos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun importarDatos(archivo: File) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                val jsonData = archivo.readText()
                
                // Parsear JSON
                val gson = Gson()
                val type = object : TypeToken<Map<String, Any>>() {}.type
                val datosCompletos = gson.fromJson<Map<String, Any>>(jsonData, type)
                
                // Limpiar todos los datos existentes antes de importar
                formulaRepository.limpiarTodosLosDatos()
                
                // Importar configuración
                val configJson = gson.toJson(datosCompletos["configuracion"])
                val configuracion = gson.fromJson(configJson, ConfiguracionStock::class.java)
                configuracionDataStore.guardarConfiguracion(configuracion)
                
                // Importar ingredientes
                val ingredientesJson = gson.toJson(datosCompletos["ingredientes"])
                val ingredientesType = object : TypeToken<List<com.fjrh.FabrikApp.data.local.entity.IngredienteInventarioEntity>>() {}.type
                val ingredientes = gson.fromJson<List<com.fjrh.FabrikApp.data.local.entity.IngredienteInventarioEntity>>(ingredientesJson, ingredientesType)
                
                // Importar ingredientes
                ingredientes?.forEach { ingrediente ->
                    inventarioRepository.insertarIngrediente(ingrediente)
                }
                
                // Importar fórmulas
                val formulasJson = gson.toJson(datosCompletos["formulas"])
                val formulasType = object : TypeToken<List<com.fjrh.FabrikApp.data.local.entity.FormulaConIngredientes>>() {}.type
                val formulas = gson.fromJson<List<com.fjrh.FabrikApp.data.local.entity.FormulaConIngredientes>>(formulasJson, formulasType)
                
                // Importar fórmulas con sus ingredientes
                formulas?.forEach { formulaConIngredientes ->
                    formulaRepository.insertarFormulaConIngredientes(
                        formulaConIngredientes.formula,
                        formulaConIngredientes.ingredientes
                    )
                }
                
                // Importar ventas
                val ventasJson = gson.toJson(datosCompletos["ventas"])
                val ventasType = object : TypeToken<List<com.fjrh.FabrikApp.data.local.entity.VentaEntity>>() {}.type
                val ventas = gson.fromJson<List<com.fjrh.FabrikApp.data.local.entity.VentaEntity>>(ventasJson, ventasType)
                
                ventas?.forEach { venta ->
                    formulaRepository.insertarVenta(venta)
                }
                
                // Importar historial
                val historialJson = gson.toJson(datosCompletos["historial"])
                val historialType = object : TypeToken<List<com.fjrh.FabrikApp.data.local.entity.HistorialProduccionEntity>>() {}.type
                val historial = gson.fromJson<List<com.fjrh.FabrikApp.data.local.entity.HistorialProduccionEntity>>(historialJson, historialType)
                
                historial?.forEach { produccion ->
                    formulaRepository.insertarHistorial(produccion)
                }
                
                // Importar balance
                val balanceJson = gson.toJson(datosCompletos["balance"])
                val balanceType = object : TypeToken<List<com.fjrh.FabrikApp.data.local.entity.BalanceEntity>>() {}.type
                val balance = gson.fromJson<List<com.fjrh.FabrikApp.data.local.entity.BalanceEntity>>(balanceJson, balanceType)
                
                balance?.forEach { balanceItem ->
                    formulaRepository.insertarBalance(balanceItem)
                }
                
                // Importar pedidos a proveedor
                val pedidosJson = gson.toJson(datosCompletos["pedidosProveedor"])
                val pedidosType = object : TypeToken<List<com.fjrh.FabrikApp.data.local.entity.PedidoProveedorEntity>>() {}.type
                val pedidos = gson.fromJson<List<com.fjrh.FabrikApp.data.local.entity.PedidoProveedorEntity>>(pedidosJson, pedidosType)
                
                pedidos?.forEach { pedido ->
                    formulaRepository.insertarPedidoProveedor(pedido)
                }
                
                _mensaje.value = "Datos importados exitosamente desde: ${archivo.name}"
            } catch (e: Exception) {
                _mensaje.value = "Error al importar datos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun importarDatosDesdeUri(uri: Uri) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                val inputStream = context.contentResolver.openInputStream(uri)
                val jsonData = inputStream?.bufferedReader().use { it?.readText() } ?: ""
                
                // Parsear JSON
                val gson = Gson()
                val type = object : TypeToken<Map<String, Any>>() {}.type
                val datosCompletos = gson.fromJson<Map<String, Any>>(jsonData, type)
                
                // Importar configuración
                val configJson = gson.toJson(datosCompletos["configuracion"])
                val configuracion = gson.fromJson(configJson, ConfiguracionStock::class.java)
                configuracionDataStore.guardarConfiguracion(configuracion)
                
                _mensaje.value = "Datos importados exitosamente desde archivo externo"
            } catch (e: Exception) {
                _mensaje.value = "Error al importar datos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun limpiarTodosLosDatos() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                // Limpiar todos los datos de la base de datos
                formulaRepository.limpiarTodosLosDatos()
                
                // Resetear configuración
                configuracionDataStore.resetearConfiguracion()
                
                _mensaje.value = "Todos los datos han sido eliminados exitosamente"
            } catch (e: Exception) {
                _mensaje.value = "Error al limpiar datos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
} 