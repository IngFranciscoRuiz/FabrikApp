package com.fjrh.FabrikApp.data.local.service

import android.content.ContentResolver
import android.net.Uri
import com.fjrh.FabrikApp.data.local.AppDatabase
import com.fjrh.FabrikApp.data.local.entity.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

data class BackupData(
    val version: String = "1.0",
    val timestamp: Long = System.currentTimeMillis(),
    val formulas: List<FormulaEntity> = emptyList(),
    val ingredientes: List<IngredienteEntity> = emptyList(),
    val unidadesMedida: List<UnidadMedidaEntity> = emptyList(),
    val ventas: List<VentaEntity> = emptyList(),
    val balance: List<BalanceEntity> = emptyList(),
    val notas: List<NotaEntity> = emptyList(),
    val pedidosProveedor: List<PedidoProveedorEntity> = emptyList(),
    val historialProduccion: List<HistorialProduccionEntity> = emptyList(),
    val ingredientesInventario: List<IngredienteInventarioEntity> = emptyList()
)

class BackupManager(private val database: AppDatabase) {
    
    private val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(Long::class.java, LongTypeAdapter())
        .create()
    
    suspend fun exportToUri(contentResolver: ContentResolver, uri: Uri): Result<String> = withContext(Dispatchers.IO) {
        try {
            val backupData = BackupData(
                formulas = database.formulaDao().getAllFormulas(),
                ingredientes = database.formulaDao().getAllIngredientes(),
                unidadesMedida = database.formulaDao().getAllUnidadesMedida(),
                ventas = database.formulaDao().getAllVentas(),
                balance = database.formulaDao().getAllBalance(),
                notas = database.formulaDao().getAllNotas(),
                pedidosProveedor = database.formulaDao().getAllPedidosProveedor(),
                historialProduccion = database.formulaDao().getAllHistorialProduccion(),
                ingredientesInventario = database.formulaDao().getAllIngredientesInventario()
            )
            
            val jsonString = gson.toJson(backupData)
            
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                BufferedWriter(OutputStreamWriter(outputStream)).use { writer ->
                    writer.write(jsonString)
                }
            }
            
            Result.success("Backup exportado exitosamente")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun importFromUri(contentResolver: ContentResolver, uri: Uri): Result<String> = withContext(Dispatchers.IO) {
        try {
            val jsonString = contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    reader.readText()
                }
            } ?: throw Exception("No se pudo leer el archivo")
            
            val backupData = gson.fromJson(jsonString, BackupData::class.java)
            
            // Validar versión
            if (backupData.version != "1.0") {
                throw Exception("Versión de backup no compatible: ${backupData.version}")
            }
            
            // Limpiar base de datos existente
            database.formulaDao().limpiarHistorialProduccion()
            database.formulaDao().limpiarNotas()
            database.formulaDao().limpiarBalance()
            database.formulaDao().limpiarVentas()
            database.formulaDao().limpiarPedidosProveedor()
            database.formulaDao().limpiarIngredientesInventario()
            database.formulaDao().limpiarIngredientes()
            database.formulaDao().limpiarFormulas()
            database.formulaDao().limpiarUnidadesMedida()
            
            // Insertar datos del backup
            backupData.unidadesMedida.forEach { unidad ->
                database.formulaDao().insertUnidad(unidad)
            }
            
            backupData.formulas.forEach { formula ->
                database.formulaDao().insertFormula(formula)
            }
            
            backupData.ingredientes.forEach { ingrediente ->
                database.formulaDao().insertIngrediente(ingrediente)
            }
            
            backupData.ingredientesInventario.forEach { ingredienteInventario ->
                database.formulaDao().insertIngredienteInventario(ingredienteInventario)
            }
            
            backupData.ventas.forEach { venta ->
                database.formulaDao().insertVenta(venta)
            }
            
            backupData.balance.forEach { balance ->
                database.formulaDao().insertBalance(balance)
            }
            
            backupData.notas.forEach { nota ->
                database.formulaDao().insertNota(nota)
            }
            
            backupData.pedidosProveedor.forEach { pedido ->
                database.formulaDao().insertPedidoProveedor(pedido)
            }
            
            backupData.historialProduccion.forEach { historial ->
                database.formulaDao().insertHistorialProduccion(historial)
            }
            
            Result.success("Backup importado exitosamente")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// Adaptador para manejar Long como timestamp
class LongTypeAdapter : JsonSerializer<Long>, JsonDeserializer<Long> {
    override fun serialize(src: Long?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src)
    }
    
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Long {
        return json?.asLong ?: 0L
    }
}
