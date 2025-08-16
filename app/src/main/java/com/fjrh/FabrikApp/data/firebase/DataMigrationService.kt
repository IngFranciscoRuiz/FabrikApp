package com.fjrh.FabrikApp.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.tasks.await

object DataMigrationService {
    private val db get() = FirebaseFirestore.getInstance()

    suspend fun migrateSharedDataToWorkspace(workspaceId: String) {
        println("DataMigrationService: Iniciando migración de datos a workspace: $workspaceId")
        
        try {
            // Migrar ingredientes
            migrateCollection("shared_data/ingredientes", "workspaces/$workspaceId/inventory")
            
            // Migrar fórmulas
            migrateCollection("shared_data/formulas", "workspaces/$workspaceId/formulas")
            
            // Migrar ventas
            migrateCollection("shared_data/ventas", "workspaces/$workspaceId/sales")
            
            // Migrar balance
            migrateCollection("shared_data/balance", "workspaces/$workspaceId/balance")
            
            // Migrar notas
            migrateCollection("shared_data/notas", "workspaces/$workspaceId/notes")
            
            // Migrar pedidos
            migrateCollection("shared_data/pedidos_proveedor", "workspaces/$workspaceId/orders")
            
            println("DataMigrationService: ✅ Migración completada exitosamente")
            
        } catch (e: Exception) {
            println("DataMigrationService: ❌ Error en migración: ${e.message}")
            throw e
        }
    }

    private suspend fun migrateCollection(fromPath: String, toPath: String) {
        try {
            println("DataMigrationService: Migrando $fromPath → $toPath")
            
            val sourceSnapshot = db.collection(fromPath).get().await()
            val batch = db.batch()
            
            sourceSnapshot.documents.forEach { doc ->
                if (doc.exists()) {
                    val data = doc.data ?: return@forEach
                    
                    // Agregar metadatos de migración
                    val migratedData = data.toMutableMap().apply {
                        put("migratedAt", FieldValue.serverTimestamp())
                        put("originalId", doc.id)
                    }
                    
                    val newDocRef = db.collection(toPath).document(doc.id)
                    batch.set(newDocRef, migratedData)
                    
                    println("DataMigrationService: Migrando documento ${doc.id}")
                }
            }
            
            batch.commit().await()
            println("DataMigrationService: ✅ Migrados ${sourceSnapshot.size()} documentos de $fromPath")
            
        } catch (e: Exception) {
            println("DataMigrationService: ❌ Error migrando $fromPath: ${e.message}")
            throw e
        }
    }

    suspend fun checkIfMigrationNeeded(workspaceId: String): Boolean {
        return try {
            val workspaceSnapshot = db.collection("workspaces/$workspaceId/formulas").get().await()
            val hasData = !workspaceSnapshot.isEmpty
            
            println("DataMigrationService: Workspace tiene datos: $hasData")
            !hasData // Necesita migración si no tiene datos
            
        } catch (e: Exception) {
            println("DataMigrationService: Error verificando migración: ${e.message}")
            true // Asumir que necesita migración si hay error
        }
    }
}
