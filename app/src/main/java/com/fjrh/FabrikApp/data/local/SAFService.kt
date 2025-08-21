package com.fjrh.FabrikApp.data.local

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import com.fjrh.FabrikApp.data.local.entity.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class SAFService(private val context: Context) {
    
    companion object {
        const val BACKUP_FILE_PREFIX = "fabrikapp_backup_"
        const val BACKUP_FILE_EXTENSION = ".json"
        const val MIME_TYPE_JSON = "application/json"
    }
    
    /**
     * Estructura de datos para el respaldo completo
     */
    data class AppBackup(
        val timestamp: Long = System.currentTimeMillis(),
        val version: String = "1.0",
        val data: BackupData
    )
    
    data class BackupData(
        val ingredientes: List<IngredienteInventarioEntity>,
        val formulas: List<FormulaConIngredientes>,
        val ventas: List<VentaEntity>,
        val balance: List<BalanceEntity>,
        val notas: List<NotaEntity>,
        val pedidos: List<PedidoProveedorEntity>,
        val historial: List<HistorialProduccionEntity>,
        val unidades: List<UnidadMedidaEntity>
    )
    
    /**
     * Solicita permisos para crear un archivo de respaldo
     */
    fun createBackupFile(activity: FragmentActivity, onFileCreated: (Uri?) -> Unit) {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "${BACKUP_FILE_PREFIX}${timestamp}${BACKUP_FILE_EXTENSION}"
        
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = MIME_TYPE_JSON
            putExtra(Intent.EXTRA_TITLE, fileName)
        }
        
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                onFileCreated(uri)
            } else {
                onFileCreated(null)
            }
        }.launch(intent)
    }
    
    /**
     * Solicita permisos para abrir un archivo de respaldo
     */
    fun openBackupFile(activity: FragmentActivity, onFileSelected: (Uri?) -> Unit) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = MIME_TYPE_JSON
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf(MIME_TYPE_JSON))
        }
        
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                onFileSelected(uri)
            } else {
                onFileSelected(null)
            }
        }.launch(intent)
    }
    
    /**
     * Exporta los datos a un archivo usando SAF
     */
    suspend fun exportBackup(
        uri: Uri,
        backupData: BackupData
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val backup = AppBackup(data = backupData)
            val json = Gson().toJson(backup)
            
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(json.toByteArray())
            }
            
            Result.success("Respaldo exportado exitosamente")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Importa los datos desde un archivo usando SAF
     */
    suspend fun importBackup(uri: Uri): Result<BackupData> = withContext(Dispatchers.IO) {
        try {
            val json = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.bufferedReader().readText()
            } ?: throw IOException("No se pudo leer el archivo")
            
            val backup = Gson().fromJson(json, AppBackup::class.java)
            Result.success(backup.data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Verifica si un archivo es un respaldo válido de FabrikApp
     */
    suspend fun validateBackupFile(uri: Uri): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val json = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.bufferedReader().readText()
            } ?: throw IOException("No se pudo leer el archivo")
            
            val backup = Gson().fromJson(json, AppBackup::class.java)
            val isValid = backup.data.ingredientes.isNotEmpty() || 
                         backup.data.formulas.isNotEmpty() ||
                         backup.data.ventas.isNotEmpty() ||
                         backup.data.balance.isNotEmpty() ||
                         backup.data.notas.isNotEmpty() ||
                         backup.data.pedidos.isNotEmpty() ||
                         backup.data.historial.isNotEmpty() ||
                         backup.data.unidades.isNotEmpty()
            
            Result.success(isValid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Obtiene información del archivo de respaldo
     */
    suspend fun getBackupInfo(uri: Uri): Result<BackupInfo> = withContext(Dispatchers.IO) {
        try {
            val json = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.bufferedReader().readText()
            } ?: throw IOException("No se pudo leer el archivo")
            
            val backup = Gson().fromJson(json, AppBackup::class.java)
            val date = Date(backup.timestamp)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            
            val info = BackupInfo(
                timestamp = backup.timestamp,
                dateString = dateFormat.format(date),
                version = backup.version,
                totalItems = backup.data.ingredientes.size + 
                           backup.data.formulas.size + 
                           backup.data.ventas.size + 
                           backup.data.balance.size + 
                           backup.data.notas.size + 
                           backup.data.pedidos.size + 
                           backup.data.historial.size + 
                           backup.data.unidades.size
            )
            
            Result.success(info)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    data class BackupInfo(
        val timestamp: Long,
        val dateString: String,
        val version: String,
        val totalItems: Int
    )
}
