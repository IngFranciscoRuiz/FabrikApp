package com.fjrh.FabrikApp.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fjrh.FabrikApp.data.local.ConfiguracionDataStore
import com.fjrh.FabrikApp.data.local.repository.FormulaRepository
import com.fjrh.FabrikApp.data.local.repository.InventarioRepository
import com.fjrh.FabrikApp.data.local.service.BackupManager
import com.fjrh.FabrikApp.domain.model.ConfiguracionStock
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
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

    private val backupManager = BackupManager(formulaRepository.database)

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
                
                // Crear archivo temporal para el backup
                val fileName = "fabrikapp_backup_${System.currentTimeMillis()}.json"
                val file = File(context.getExternalFilesDir(null), fileName)
                
                // Usar el BackupManager para exportar
                val result = backupManager.exportToUri(
                    context.contentResolver,
                    android.net.Uri.fromFile(file)
                )
                
                result.fold(
                    onSuccess = { message ->
                        cargarArchivosBackup()
                        _mensaje.value = "Datos exportados exitosamente a: ${file.name}"
                    },
                    onFailure = { exception ->
                        _mensaje.value = "Error al exportar datos: ${exception.message}"
                    }
                )
            } catch (e: Exception) {
                _mensaje.value = "Error al exportar datos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun exportarDatosExternos(uri: Uri) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                // Usar el BackupManager para exportar a ubicación externa
                val result = backupManager.exportToUri(context.contentResolver, uri)
                
                result.fold(
                    onSuccess = { message ->
                        _mensaje.value = "Datos exportados exitosamente a ubicación externa"
                    },
                    onFailure = { exception ->
                        _mensaje.value = "Error al exportar datos: ${exception.message}"
                    }
                )
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
                
                // Usar el BackupManager para importar
                val result = backupManager.importFromUri(
                    context.contentResolver,
                    android.net.Uri.fromFile(archivo)
                )
                
                result.fold(
                    onSuccess = { message ->
                        _mensaje.value = "Datos importados exitosamente desde: ${archivo.name}"
                    },
                    onFailure = { exception ->
                        _mensaje.value = "Error al importar datos: ${exception.message}"
                    }
                )
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
                
                // Usar el BackupManager para importar desde URI
                val result = backupManager.importFromUri(context.contentResolver, uri)
                
                result.fold(
                    onSuccess = { message ->
                        _mensaje.value = "Datos importados exitosamente desde archivo externo"
                    },
                    onFailure = { exception ->
                        _mensaje.value = "Error al importar datos: ${exception.message}"
                    }
                )
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